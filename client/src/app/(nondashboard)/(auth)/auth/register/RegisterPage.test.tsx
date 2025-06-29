import RegisterPage from './page';
import { render, screen, waitFor } from '@testing-library/react';
import { useRouter } from 'next/navigation';
import { useRegisterMutation } from '@/state/endpoints/auth/auth';
import userEvent, { UserEvent } from '@testing-library/user-event';
import { assert } from 'node:console';

const correctInputs = async (user: UserEvent) => {
  const usernameInput = screen.getByLabelText(/First Name/);
  const userLastnameInput = screen.getByLabelText(/Last Name/);
  const emailInput = screen.getByLabelText(/Email/);
  const passwordInput = screen.getByLabelText(/Password/);
  const button = screen.getByRole('button', { name: /Sign up/i });
  await user.type(usernameInput, 'test');
  await user.type(userLastnameInput, 'test');
  await user.type(emailInput, 'test@example.com');
  await user.type(passwordInput, 'password123');
  return { button };
};

jest.mock('@/state/endpoints/auth/auth', () => ({
  useRegisterMutation: jest.fn(),
}));

jest.mock('next/navigation', () => ({
  useRouter: jest.fn(),
}));
const push = jest.fn();
const refresh = jest.fn();
const registerUserMock = jest.fn();
beforeEach(() => {
  (useRouter as jest.Mock).mockReturnValue({ push, refresh });
  (useRegisterMutation as jest.Mock).mockReturnValue([registerUserMock]);
  registerUserMock.mockReset();
  push.mockReset();
  refresh.mockReset();
});

test('renders inputs and button', () => {
  render(<RegisterPage />);
  const usernameInput = screen.getByLabelText(/First Name/);
  const userLastnameInput = screen.getByLabelText(/Last Name/);
  const emailInput = screen.getByLabelText(/Email/);
  const passwordInput = screen.getByLabelText(/Password/);
  const button = screen.getByRole('button', { name: /Sign up/i });
  expect(usernameInput).toBeInTheDocument();
  expect(userLastnameInput).toBeInTheDocument();
  expect(emailInput).toBeInTheDocument();
  expect(passwordInput).toBeInTheDocument();
  expect(button).toBeInTheDocument();
});

test('button is being disabled and loading is showing when isLoading is true', async () => {
  registerUserMock.mockReturnValue({ unwrap: () => new Promise(() => {}) });
  render(<RegisterPage />);
  const button = screen.getByRole('button', { name: /Sign up/i });
  await correctInputs(userEvent.setup());
  await userEvent.click(button);
  await waitFor(() => {
    expect(button).toBeDisabled();
    expect(screen.getByTestId('spinner')).toBeInTheDocument();
  });
});

test('submitting empty form shows required errors', async () => {
  const user = userEvent.setup();
  render(<RegisterPage />);
  const button = screen.getByRole('button', { name: /Sign up/i });
  await user.click(button);
  await waitFor(() => {
    expect(screen.getByText(/First Name is required/i)).toBeInTheDocument();
    expect(screen.getByText(/Last Name is required/i)).toBeInTheDocument();
    expect(screen.getByText(/Email is required/i)).toBeInTheDocument();
    expect(screen.getByText(/Password is required/i)).toBeInTheDocument();
  });
});

test('Api call is triggered correctly ', async () => {
  const user = userEvent.setup();
  render(<RegisterPage />);
  const { button } = await correctInputs(user);
  await user.click(button);

  assert(registerUserMock.mock.calls.length === 1);
});

test('On success redirect and router.refresh() is called', async () => {
  const user = userEvent.setup();
  render(<RegisterPage />);
  registerUserMock.mockReturnValue({ unwrap: () => Promise.resolve() });
  const { button } = await correctInputs(user);
  await user.click(button);
  await waitFor(() => {
    expect(push).toHaveBeenCalledTimes(1);
    expect(push).toHaveBeenCalledWith('/confirm-email');
    expect(refresh).toHaveBeenCalledTimes(1);
  });
});

test('When no message in API error response, shows default message', async () => {
  const user = userEvent.setup();
  render(<RegisterPage />);
  registerUserMock.mockReturnValue({ unwrap: () => Promise.reject({ data: {} }) });
  const { button } = await correctInputs(user);
  await user.click(button);
  await waitFor(() => {
    expect(screen.getByText(/An unexpected error occurred./i)).toBeInTheDocument();
    expect(screen.queryByTestId('spinner')).not.toBeInTheDocument();
  });
});

test('Shows api error messsage', async () => {
  const user = userEvent.setup();
  render(<RegisterPage />);
  const apiErrorMessage = 'Invalid credentials provided.';
  registerUserMock.mockReturnValue({ unwrap: () => Promise.reject({ data: { message: apiErrorMessage } }) });
  const { button } = await correctInputs(user);
  await user.click(button);
  await waitFor(() => {
    expect(screen.getByText(apiErrorMessage)).toBeInTheDocument();
    expect(registerUserMock).toHaveBeenCalledTimes(1);
    expect(screen.queryByTestId('spinner')).not.toBeInTheDocument();
  });
});

// Errors disapear when corrected
test('validation errors disapear after correction', async () => {
  const user = userEvent.setup();
  render(<RegisterPage />);
  const button = screen.getByRole('button', { name: /Sign up/i });
  await user.click(button);
  await waitFor(() => {
    expect(screen.getByText(/First Name is required/i)).toBeInTheDocument();
    expect(screen.getByText(/Last Name is required/i)).toBeInTheDocument();
    expect(screen.getByText(/Email is required/i)).toBeInTheDocument();
    expect(screen.getByText(/Password is required/i)).toBeInTheDocument();
  });
  await correctInputs(user);
  await waitFor(() => {
    expect(screen.queryByText(/First Name is required/i)).not.toBeInTheDocument();
    expect(screen.queryByText(/Last Name is required/i)).not.toBeInTheDocument();
    expect(screen.queryByText(/Email is required/i)).not.toBeInTheDocument();
    expect(screen.queryByText(/Password is required/i)).not.toBeInTheDocument();
  });
});

test('shows field validation errors', async () => {
  const user = userEvent.setup();
  render(<RegisterPage />);
  const emailError = { field: 'email', message: 'Invalid Email' };
  const usernameError = { field: 'username', message: 'Invalid UserNAme' };
  const userLastnameError = { field: 'userLastname', message: 'Invalid Last Name' };
  const passwordError = { field: 'password', message: 'Invalid Password' };
  registerUserMock.mockReturnValue({ unwrap: () => Promise.reject({ data: { errors: [emailError, usernameError, userLastnameError, passwordError] } }) });
  const { button } = await correctInputs(user);
  await user.click(button);
  await waitFor(() => {
    expect(screen.getByText(emailError.message)).toBeInTheDocument();
    expect(screen.getByText(usernameError.message)).toBeInTheDocument();
    expect(screen.getByText(userLastnameError.message)).toBeInTheDocument();
    expect(screen.getByText(passwordError.message)).toBeInTheDocument();
  });
});
