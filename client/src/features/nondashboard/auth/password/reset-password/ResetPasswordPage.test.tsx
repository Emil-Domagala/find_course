import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import ResetPassword from '.';
import { useResetPasswordMutation } from '../api';

const push = jest.fn();
const resetPasswordMock = jest.fn();
const get = jest.fn(() => 'mocked-token');

jest.mock('next/navigation', () => ({
  useRouter: () => ({
    push: push,
  }),
  useSearchParams: () => ({
    get: get,
  }),
}));

jest.mock('../api', () => ({
  useResetPasswordMutation: jest.fn(),
}));

beforeEach(() => {
  (useResetPasswordMutation as jest.Mock).mockReturnValue([resetPasswordMock, { isLoading: false }]);
});
afterEach(() => {
  jest.clearAllMocks();
});

const basicSetup = () => {
  const user = userEvent.setup();
  render(<ResetPassword />);
  const passwordInput = screen.getByLabelText(/New Password/i);
  const confirmPasswordInput = screen.getByLabelText(/Confirm Password/i);
  const button = screen.getByRole('button', { name: /Reset Password/i });
  return { user, passwordInput, confirmPasswordInput, button };
};

describe('ResetPasswordPage', () => {
  describe('Renders', () => {
    test('renders all input fields and the continue button', () => {
      render(<ResetPassword />);
      expect(screen.getByLabelText(/New Password/i)).toBeInTheDocument();
      expect(screen.getByLabelText(/Confirm Password/i)).toBeInTheDocument();
      expect(screen.getByRole('button', { name: /Reset Password/i })).toBeInTheDocument();
    });
  });
  describe('Validation', () => {
    test('shows error message when password is invalid', async () => {
      const { user, passwordInput, button } = basicSetup();
      await user.type(passwordInput, '123');
      await user.click(button);
      expect(await screen.findByText(/At least 6 characters long/i)).toBeInTheDocument();
    });
    test('shows error message when passwords are empty', async () => {
      const { user, button } = basicSetup();
      await user.click(button);

      expect(await screen.findByText('Password is required')).toBeInTheDocument();
      expect(await screen.findByText(/Confirm Password is required/i)).toBeInTheDocument();
    });
    test('shows error message when confirm password is invalid', async () => {
      const { user, passwordInput, confirmPasswordInput, button } = basicSetup();
      await user.type(passwordInput, 'password123');
      await user.type(confirmPasswordInput, 'password');
      await user.click(button);
      expect(await screen.findByText(/Confirm Password must match Password/i)).toBeInTheDocument();
    });
  });
  describe('Api Interaction', () => {
    test('calls API with correct data on submit', async () => {
      const { user, passwordInput, confirmPasswordInput, button } = basicSetup();
      await user.type(passwordInput, 'password123');
      await user.type(confirmPasswordInput, 'password123');
      await user.click(button);
      expect(resetPasswordMock).toHaveBeenCalledWith({ password: 'password123', token: 'mocked-token' });
    });
    test('When Pending shows spinner, disable button and hide inputs', async () => {
      resetPasswordMock.mockReturnValue({ unwrap: () => new Promise(() => {}) });
      (useResetPasswordMutation as jest.Mock).mockReturnValue([resetPasswordMock, { isLoading: true }]);
      const { user, passwordInput, confirmPasswordInput, button } = basicSetup();
      await user.type(passwordInput, 'password123');
      await user.type(confirmPasswordInput, 'password123');
      await user.click(button);
      await waitFor(() => {
        expect(button).toBeDisabled();
        expect(screen.getByTestId('spinner')).toBeInTheDocument();
      });
    });
    test('When Success shows message and redirects to login', async () => {
      resetPasswordMock.mockReturnValue({ unwrap: () => Promise.resolve() });
      const { user, passwordInput, confirmPasswordInput, button } = basicSetup();
      await user.type(passwordInput, 'password123');
      await user.type(confirmPasswordInput, 'password123');
      await user.click(button);
      await waitFor(() => {
        expect(screen.getByText(/Password has been reset/i)).toBeInTheDocument();
        expect(button).toBeDisabled();
        expect(screen.queryByTestId('spinner')).not.toBeInTheDocument();
      });
      await waitFor(
        () => {
          expect(push).toHaveBeenCalledWith('/auth/login');
        },
        { timeout: 2000 },
      );
    });
    test('When Error shows message default', async () => {
      resetPasswordMock.mockReturnValue({ unwrap: () => Promise.reject({ data: {} }) });
      const { user, passwordInput, confirmPasswordInput, button } = basicSetup();
      await user.type(passwordInput, 'password123');
      await user.type(confirmPasswordInput, 'password123');
      await user.click(button);
      await waitFor(() => {
        expect(screen.getByText(/An unexpected error occurred/i)).toBeInTheDocument();
        expect(button).toBeDisabled();
        expect(screen.queryByTestId('spinner')).not.toBeInTheDocument();
      });
    });
    test('When Error shows message from API when availble', async () => {
      const customErrorMessage = 'Custom error message';
      resetPasswordMock.mockReturnValue({ unwrap: () => Promise.reject({ data: { message: customErrorMessage } }) });
      const { user, passwordInput, confirmPasswordInput, button } = basicSetup();
      await user.type(passwordInput, 'password123');
      await user.type(confirmPasswordInput, 'password123');
      await user.click(button);
      await waitFor(() => {
        expect(screen.getByText(customErrorMessage)).toBeInTheDocument();
        expect(button).toBeDisabled();
      });
    });
    test('When Error shows inputs again', async () => {
      resetPasswordMock.mockReturnValue({ unwrap: () => Promise.reject({ data: {} }) });
      const { user, passwordInput, confirmPasswordInput, button } = basicSetup();
      await user.type(passwordInput, 'password123');
      await user.type(confirmPasswordInput, 'password123');
      await user.click(button);
      await waitFor(() => {
        expect(screen.getByText(/An unexpected error occurred/i)).toBeInTheDocument();
        expect(button).toBeDisabled();
        expect(screen.queryByTestId('spinner')).not.toBeInTheDocument();
      });
      await waitFor(
        () => {
          expect(screen.getByLabelText(/New Password/i)).toBeInTheDocument();
          expect(screen.getByLabelText(/Confirm Password/i)).toBeInTheDocument();
          expect(button).not.toBeDisabled();
          expect(screen.queryByText(/An unexpected error occurred/i)).not.toBeInTheDocument();
        },
        { timeout: 2000 },
      );
    });
    test('When not token redirects to forgot password', async () => {
      get.mockReturnValue('');

      resetPasswordMock.mockReturnValue({ unwrap: () => Promise.resolve() });
      const { user, passwordInput, confirmPasswordInput, button } = basicSetup();
      await user.type(passwordInput, 'password123');
      await user.type(confirmPasswordInput, 'password123');
      await user.click(button);
      await waitFor(
        () => {
          expect(screen.getByText(/Invalid token, get new token from email/i)).toBeInTheDocument();
          expect(push).toHaveBeenCalledWith('/auth/forgot-password');
          expect(button).toBeDisabled();
        },
        { timeout: 2000 },
      );
    });
  });
});
