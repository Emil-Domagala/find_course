import LoginPage from './page';
import { render, screen, waitFor } from '@testing-library/react';
import { useRouter, useSearchParams } from 'next/navigation';
import { useLoginMutation } from '@/state/endpoints/auth/auth';
import userEvent from '@testing-library/user-event';
import { assert } from 'node:console';

jest.mock('next/navigation', () => ({
  useRouter: jest.fn(),
  useSearchParams: jest.fn(),
}));

jest.mock('@/state/endpoints/auth/auth', () => ({
  useLoginMutation: jest.fn(),
}));

const push = jest.fn();
const refresh = jest.fn();
const loginUserMock = jest.fn();

beforeEach(() => {
  (useRouter as jest.Mock).mockReturnValue({ push, refresh });
  (useSearchParams as jest.Mock).mockReturnValue(new URLSearchParams('redirect=/dashboard'));
  (useLoginMutation as jest.Mock).mockReturnValue([loginUserMock]);
  loginUserMock.mockReset();
  push.mockReset();
  refresh.mockReset();
});

test('renders inputs and button', () => {
  render(<LoginPage />);
  const emailInput = screen.getByLabelText(/Email/);
  const passwordInput = screen.getByLabelText(/Password/);
  const button = screen.getByRole('button', { name: /continue/i });
  expect(emailInput).toBeInTheDocument();
  expect(passwordInput).toBeInTheDocument();
  expect(button).toBeInTheDocument();
});

test('button is being disabled and loading is showing when isLoading is true', async () => {
  loginUserMock.mockReturnValue({ unwrap: () => new Promise(() => {}) });
  const user = userEvent.setup();

  render(<LoginPage />);

  const emailInput = screen.getByLabelText(/Email/);
  const passwordInput = screen.getByLabelText(/Password/);
  const button = screen.getByRole('button', { name: /continue/i });
  await user.type(emailInput, 'test@example.com');
  await user.type(passwordInput, 'password123');
  await user.click(button);

  await waitFor(() => {
    expect(button).toBeDisabled();
    expect(screen.getByTestId('spinner')).toBeInTheDocument();
  });
});

test('submitting empty form shows required errors', async () => {
  render(<LoginPage />);

  const button = screen.getByRole('button', { name: /continue/i });
  await userEvent.click(button);

  await waitFor(() => {
    expect(screen.getByText(/Email is required/i)).toBeInTheDocument();
    expect(screen.getByText(/Password is required/i)).toBeInTheDocument();
  });
});

const InvalidPasswordTestCases = [
  {
    description: 'to short password',
    password: 'pa',
    expectedErrors: {
      password: /At least 6 characters long/i,
    },
  },
  {
    description: 'to long password',
    password: '0123456789_01234567890_01234567890_0123456789',
    expectedErrors: {
      password: /At most 30 characters long/i,
    },
  },
];

test.each(InvalidPasswordTestCases)(`shows validation error for invalid password: %s`, async ({ password, expectedErrors }) => {
  const user = userEvent.setup();
  render(<LoginPage />);

  const button = screen.getByRole('button', { name: /continue/i });
  const passwordInput = screen.getByLabelText(/Password/);
  await user.clear(passwordInput);

  await user.type(passwordInput, password);

  await user.click(button);

  await waitFor(() => {
    expect(screen.getByText(expectedErrors.password)).toBeInTheDocument();
  });
});

test(`shows validation error for invalid email`, async () => {
  const user = userEvent.setup();
  render(<LoginPage />);

  const button = screen.getByRole('button', { name: /continue/i });
  const emailInput = screen.getByLabelText(/Email/);
  await user.clear(emailInput);
  await user.type(emailInput, 'invalid@email');
  await user.click(button);

  await waitFor(() => {
    expect(screen.getByText(/Invalid email format/i)).toBeInTheDocument();
  });
});

test('validation errors disapear after correction', async () => {
  const user = userEvent.setup();

  render(<LoginPage />);
  const button = screen.getByRole('button', { name: /continue/i });
  const emailInput = screen.getByLabelText(/Email/);
  const passwordInput = screen.getByLabelText(/Password/);

  await user.type(emailInput, 'invalid@email');
  await user.type(passwordInput, 'Pas');
  await user.click(button);

  await waitFor(() => {
    expect(screen.queryByText(/Invalid email format/i)).toBeInTheDocument();
    expect(screen.queryByText(/At least 6 characters long/i)).toBeInTheDocument();
  });

  await user.type(emailInput, '.com');
  await user.type(passwordInput, 'sword');
  await user.click(button);

  await waitFor(() => {
    expect(screen.queryByText(/Invalid email format/i)).not.toBeInTheDocument();
    expect(screen.queryByText(/At least 6 characters long/i)).not.toBeInTheDocument();
  });
});
test('Api call is triggered correctly and isLoading is set correctly', async () => {
  const user = userEvent.setup();
  render(<LoginPage />);
  const button = screen.getByRole('button', { name: /continue/i });
  const emailInput = screen.getByLabelText(/Email/);
  const passwordInput = screen.getByLabelText(/Password/);

  await user.type(emailInput, 'invalid@email.com');
  await user.type(passwordInput, 'Password');
  await user.click(button);

  assert(loginUserMock.mock.calls.length === 1);
});

test('On success redirect and router.refresh() is called', async () => {
  const user = userEvent.setup();
  render(<LoginPage />);
  const button = screen.getByRole('button', { name: /continue/i });
  const emailInput = screen.getByLabelText(/Email/);
  const passwordInput = screen.getByLabelText(/Password/);

  await user.type(emailInput, 'test@example.com');
  await user.type(passwordInput, 'password123');

  loginUserMock.mockReturnValue({ unwrap: () => Promise.resolve() });

  await user.click(button);

  await waitFor(() => {
    expect(push).toHaveBeenCalledTimes(1);
    expect(push).toHaveBeenCalledWith('/dashboard');
    expect(refresh).toHaveBeenCalledTimes(1);
  });
});

test('When no message in API error response, shows default message', async () => {
  const user = userEvent.setup();
  render(<LoginPage />);
  const button = screen.getByRole('button', { name: /continue/i });
  const emailInput = screen.getByLabelText(/Email/);
  const passwordInput = screen.getByLabelText(/Password/);

  await user.type(emailInput, 'invalid@email.com');
  await user.type(passwordInput, 'Password');
  loginUserMock.mockReturnValue({ unwrap: () => Promise.reject({ data: {} }) });
  await user.click(button);
  await waitFor(() => {
    expect(screen.getByText(/An unexpected error occurred./i)).toBeInTheDocument();
    expect(screen.queryByTestId('spinner')).not.toBeInTheDocument();
  });
});
test('Shows api error messsage', async () => {
  const user = userEvent.setup();
  render(<LoginPage />);
  const button = screen.getByRole('button', { name: /continue/i });
  const emailInput = screen.getByLabelText(/Email/);
  const passwordInput = screen.getByLabelText(/Password/);

  await user.type(emailInput, 'invalid@email.com');
  await user.type(passwordInput, 'Password');

  const apiErrorMessage = 'Invalid credentials provided.';

  loginUserMock.mockReturnValue({ unwrap: () => Promise.reject({ data: { message: apiErrorMessage } }) });

  await user.click(button);

  await waitFor(() => {
    expect(screen.getByText(apiErrorMessage)).toBeInTheDocument();
    expect(loginUserMock).toHaveBeenCalledTimes(1);
    expect(screen.queryByTestId('spinner')).not.toBeInTheDocument();
  });
});
