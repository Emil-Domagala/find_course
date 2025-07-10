import Register from '.';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { useRouter } from 'next/navigation';
import { useRegisterMutation } from './api';

jest.mock('./api', () => ({
  useRegisterMutation: jest.fn(),
}));

jest.mock('next/navigation', () => ({
  useRouter: jest.fn(),
}));

// Test Mocks
const push = jest.fn();
const refresh = jest.fn();
const registerUserMock = jest.fn();

// Utility for filling valid form inputs
const fillValidInputs = async (user: ReturnType<typeof userEvent.setup>) => {
  await user.type(screen.getByLabelText(/First Name/i), 'John');
  await user.type(screen.getByLabelText(/Last Name/i), 'Doe');
  await user.type(screen.getByLabelText(/Email/i), 'john@example.com');
  await user.type(screen.getByLabelText(/Password/i), 'strongPassword123');
};

beforeEach(() => {
  (useRouter as jest.Mock).mockReturnValue({ push, refresh });
  (useRegisterMutation as jest.Mock).mockReturnValue([registerUserMock]);
  jest.clearAllMocks();
});

describe('RegisterPage', () => {
  test('renders all input fields and the sign-up button', () => {
    render(<Register />);
    expect(screen.getByLabelText(/First Name/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/Last Name/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/Email/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/Password/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /Sign Up/i })).toBeInTheDocument();
  });

  test('disables button and shows spinner when loading', async () => {
    registerUserMock.mockReturnValue({ unwrap: () => new Promise(() => {}) });
    const user = userEvent.setup();
    render(<Register />);
    await fillValidInputs(user);

    const button = screen.getByRole('button', { name: /Sign Up/i });
    await user.click(button);

    await waitFor(() => {
      expect(button).toBeDisabled();
      expect(screen.getByTestId('spinner')).toBeInTheDocument();
    });
  });

  describe('API interaction', () => {
    test('triggers register API call with correct data', async () => {
      const user = userEvent.setup();
      render(<Register />);
      await fillValidInputs(user);

      await user.click(screen.getByRole('button', { name: /Sign Up/i }));

      await waitFor(() => {
        expect(registerUserMock).toHaveBeenCalledTimes(1);
      });
    });

    test('redirects and refreshes router on success', async () => {
      registerUserMock.mockReturnValue({ unwrap: () => Promise.resolve() });
      const user = userEvent.setup();
      render(<Register />);
      await fillValidInputs(user);

      await user.click(screen.getByRole('button', { name: /Sign Up/i }));

      await waitFor(() => {
        expect(push).toHaveBeenCalledWith('/confirm-email');
        expect(refresh).toHaveBeenCalledTimes(1);
      });
    });

    test('shows default error message when API error has no message', async () => {
      registerUserMock.mockReturnValue({ unwrap: () => Promise.reject({ data: {} }) });
      const user = userEvent.setup();
      render(<Register />);
      await fillValidInputs(user);

      await user.click(screen.getByRole('button', { name: /Sign Up/i }));

      await waitFor(() => {
        expect(screen.getByText(/An unexpected error occurred/i)).toBeInTheDocument();
        expect(screen.queryByTestId('spinner')).not.toBeInTheDocument();
      });
    });

    test('displays API root-level error message', async () => {
      const errorMessage = 'Invalid credentials provided.';
      registerUserMock.mockReturnValue({ unwrap: () => Promise.reject({ data: { message: errorMessage } }) });
      const user = userEvent.setup();
      render(<Register />);
      await fillValidInputs(user);

      await user.click(screen.getByRole('button', { name: /Sign Up/i }));

      await waitFor(() => {
        expect(screen.getByText(errorMessage)).toBeInTheDocument();
      });
    });

    test('displays field-level validation errors from API', async () => {
      const apiErrors = [
        { field: 'email', message: 'Invalid Email' },
        { field: 'username', message: 'Invalid First Name' },
        { field: 'userLastname', message: 'Invalid Last Name' },
        { field: 'password', message: 'Invalid Password' },
      ];
      registerUserMock.mockReturnValue({ unwrap: () => Promise.reject({ data: { errors: apiErrors } }) });

      const user = userEvent.setup();
      render(<Register />);
      await fillValidInputs(user);

      await user.click(screen.getByRole('button', { name: /Sign Up/i }));

      await waitFor(() => {
        for (const error of apiErrors) {
          expect(screen.getByText(error.message)).toBeInTheDocument();
        }
      });
    });
  });

  describe('Validation', () => {
    test('shows required field errors on empty submit', async () => {
      const user = userEvent.setup();
      render(<Register />);

      await user.click(screen.getByRole('button', { name: /Sign Up/i }));

      await waitFor(() => {
        expect(screen.getByText(/First Name is required/i)).toBeInTheDocument();
        expect(screen.getByText(/Last Name is required/i)).toBeInTheDocument();
        expect(screen.getByText(/Email is required/i)).toBeInTheDocument();
        expect(screen.getByText(/Password is required/i)).toBeInTheDocument();
      });
    });

    test('clears validation errors after correcting inputs', async () => {
      const user = userEvent.setup();
      render(<Register />);

      await user.click(screen.getByRole('button', { name: /Sign Up/i }));

      await waitFor(() => {
        expect(screen.getByText(/First Name is required/i)).toBeInTheDocument();
      });

      await fillValidInputs(user);

      await waitFor(() => {
        expect(screen.queryByText(/First Name is required/i)).not.toBeInTheDocument();
      });
    });
    test('rejects too short First Name', async () => {
      const user = userEvent.setup();
      render(<Register />);

      await user.type(screen.getByLabelText(/First Name/i), 'A');
      await user.type(screen.getByLabelText(/Last Name/i), 'Doe');
      await user.type(screen.getByLabelText(/Email/i), 'john@example.com');
      await user.type(screen.getByLabelText(/Password/i), 'password123');

      await user.click(screen.getByRole('button', { name: /Sign Up/i }));

      await waitFor(() => {
        expect(screen.getByText(/At least 3 characters long/i)).toBeInTheDocument();
      });
    });

    test('rejects invalid email format', async () => {
      const user = userEvent.setup();
      render(<Register />);

      await user.type(screen.getByLabelText(/First Name/i), 'John');
      await user.type(screen.getByLabelText(/Last Name/i), 'Doe');
      await user.type(screen.getByLabelText(/Email/i), 'invalid@example');
      await user.type(screen.getByLabelText(/Password/i), 'password123');

      await user.click(screen.getByRole('button', { name: /Sign Up/i }));

      await waitFor(() => {
        expect(screen.getByText(/Invalid email format/i)).toBeInTheDocument();
      });
    });
  });
});
