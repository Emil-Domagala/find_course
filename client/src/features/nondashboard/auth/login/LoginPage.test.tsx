import Login from '.';
import { render, screen, waitFor } from '@testing-library/react';
import { useRouter, useSearchParams } from 'next/navigation';
import userEvent from '@testing-library/user-event';
import { assert } from 'node:console';
import { useLoginMutation } from './api';

jest.mock('next/navigation', () => ({
  useRouter: jest.fn(),
  useSearchParams: jest.fn(),
}));

jest.mock('./api', () => ({
  useLoginMutation: jest.fn(),
}));

const push = jest.fn();
const refresh = jest.fn();
const loginUserMock = jest.fn();

let user: ReturnType<typeof userEvent.setup>;

beforeEach(() => {
  (useRouter as jest.Mock).mockReturnValue({ push, refresh });
  (useSearchParams as jest.Mock).mockReturnValue(new URLSearchParams('redirect=/dashboard'));
  (useLoginMutation as jest.Mock).mockReturnValue([loginUserMock]);

  loginUserMock.mockReset();
  push.mockReset();
  refresh.mockReset();

  user = userEvent.setup();
});

describe('LoginPage', () => {
  describe('Rendering', () => {
    test('renders email and password inputs and submit button', () => {
      render(<Login />);
      expect(screen.getByLabelText(/Email/i)).toBeInTheDocument();
      expect(screen.getByLabelText(/Password/i)).toBeInTheDocument();
      expect(screen.getByRole('button', { name: /continue/i })).toBeInTheDocument();
    });
  });

  describe('Validation', () => {
    test('shows required errors when form is submitted empty', async () => {
      render(<Login />);
      await user.click(screen.getByRole('button', { name: /continue/i }));

      expect(await screen.findByText(/Email is required/i)).toBeInTheDocument();
      expect(screen.getByText(/Password is required/i)).toBeInTheDocument();
    });

    test('shows validation error for invalid email format', async () => {
      render(<Login />);
      await user.type(screen.getByLabelText(/Email/i), 'invalid@email');
      await user.click(screen.getByRole('button', { name: /continue/i }));

      expect(await screen.findByText(/Invalid email format/i)).toBeInTheDocument();
    });

    test.each([
      { description: 'too short password', password: 'pa', error: /At least 6 characters long/i },
      {
        description: 'too long password',
        password: '0123456789_01234567890_01234567890_0123456789',
        error: /At most 30 characters long/i,
      },
    ])('shows validation error for $description', async ({ password, error }) => {
      render(<Login />);
      await user.clear(screen.getByLabelText(/Password/i));
      await user.type(screen.getByLabelText(/Password/i), password);
      await user.click(screen.getByRole('button', { name: /continue/i }));

      expect(await screen.findByText(error)).toBeInTheDocument();
    });

    test('clears validation errors after correcting inputs', async () => {
      render(<Login />);

      await user.type(screen.getByLabelText(/Email/i), 'invalid@email');
      await user.type(screen.getByLabelText(/Password/i), '123');
      await user.click(screen.getByRole('button', { name: /continue/i }));

      expect(await screen.findByText(/Invalid email format/i)).toBeInTheDocument();
      expect(screen.getByText(/At least 6 characters long/i)).toBeInTheDocument();

      await user.type(screen.getByLabelText(/Email/i), '.com');
      await user.type(screen.getByLabelText(/Password/i), 'password');
      await user.click(screen.getByRole('button', { name: /continue/i }));

      await waitFor(() => {
        expect(screen.queryByText(/Invalid email format/i)).not.toBeInTheDocument();
        expect(screen.queryByText(/At least 6 characters long/i)).not.toBeInTheDocument();
      });
    });
  });

  describe('Form submission & API', () => {
    test('displays loading spinner and disables button while submitting', async () => {
      loginUserMock.mockReturnValue({ unwrap: () => new Promise(() => {}) });

      render(<Login />);

      await user.type(screen.getByLabelText(/Email/i), 'test@example.com');
      await user.type(screen.getByLabelText(/Password/i), 'password123');
      await user.click(screen.getByRole('button', { name: /continue/i }));

      expect(await screen.findByTestId('spinner')).toBeInTheDocument();
      expect(screen.getByRole('button', { name: /continue/i })).toBeDisabled();
    });

    test('triggers API call with correct data', async () => {
      render(<Login />);
      await user.type(screen.getByLabelText(/Email/i), 'test@example.com');
      await user.type(screen.getByLabelText(/Password/i), 'password123');
      await user.click(screen.getByRole('button', { name: /continue/i }));

      assert(loginUserMock.mock.calls.length === 1);
    });

    test('redirects on successful login and calls router.refresh', async () => {
      loginUserMock.mockReturnValue({ unwrap: () => Promise.resolve() });

      render(<Login />);
      await user.type(screen.getByLabelText(/Email/i), 'test@example.com');
      await user.type(screen.getByLabelText(/Password/i), 'password123');
      await user.click(screen.getByRole('button', { name: /continue/i }));

      await waitFor(() => {
        expect(push).toHaveBeenCalledWith('/dashboard');
        expect(refresh).toHaveBeenCalled();
      });
    });

    test('shows default error message if API error has no message', async () => {
      loginUserMock.mockReturnValue({ unwrap: () => Promise.reject({ data: {} }) });

      render(<Login />);
      await user.type(screen.getByLabelText(/Email/i), 'test@example.com');
      await user.type(screen.getByLabelText(/Password/i), 'password123');
      await user.click(screen.getByRole('button', { name: /continue/i }));

      expect(await screen.findByText(/An unexpected error occurred./i)).toBeInTheDocument();
      expect(screen.queryByTestId('spinner')).not.toBeInTheDocument();
    });

    test('displays server error message from API', async () => {
      const apiErrorMessage = 'Invalid credentials provided.';
      loginUserMock.mockReturnValue({ unwrap: () => Promise.reject({ data: { message: apiErrorMessage } }) });

      render(<Login />);
      await user.type(screen.getByLabelText(/Email/i), 'test@example.com');
      await user.type(screen.getByLabelText(/Password/i), 'password123');
      await user.click(screen.getByRole('button', { name: /continue/i }));

      expect(await screen.findByText(apiErrorMessage)).toBeInTheDocument();
      expect(loginUserMock).toHaveBeenCalledTimes(1);
      expect(screen.queryByTestId('spinner')).not.toBeInTheDocument();
    });
  });
});
