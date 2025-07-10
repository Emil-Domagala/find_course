import ForgotPassword from '.';
import { render, screen, waitFor } from '@testing-library/react';

import userEvent from '@testing-library/user-event';
import { useSendResetPasswordEmailMutation } from '../api/resetPassword';

const sendResetPasswordMock = jest.fn();

jest.mock('../api/resetPassword', () => ({
  useSendResetPasswordEmailMutation: jest.fn(),
}));

beforeEach(() => {
  (useSendResetPasswordEmailMutation as jest.Mock).mockReturnValue([sendResetPasswordMock, { isLoading: false }]);
  sendResetPasswordMock.mockReset();
});

const basicSetup = () => {
  const user = userEvent.setup();
  render(<ForgotPassword />);
  const emailInput = screen.getByLabelText(/Email adress/i);
  const button = screen.getByRole('button', { name: /Continue/i });
  return { user, emailInput, button };
};

const correctEmail = 'test@email.com';

describe('ForgotPasswordPage', () => {
  describe('Renders', () => {
    test('renders all input fields and the continue button', () => {
      render(<ForgotPassword />);
      expect(screen.getByLabelText(/Email adress/i)).toBeInTheDocument();
      expect(screen.getByRole('button', { name: /Continue/i })).toBeInTheDocument();
    });
  });
  describe('Validation', () => {
    test('shows error message when email is invalid', async () => {
      const { user, emailInput, button } = basicSetup();
      await user.type(emailInput, 'invalid@email');
      await user.click(button);
      expect(await screen.findByText(/Invalid email format/i)).toBeInTheDocument();
    });

    test('shows error message when email is empty', async () => {
      const { user, button } = basicSetup();
      await user.click(button);
      expect(await screen.findByText(/Email is required/i)).toBeInTheDocument();
    });
  });
  describe('Api Interaction', () => {
    test('calls API with correct email on submit', async () => {
      const { user, emailInput, button } = basicSetup();
      await user.type(emailInput, correctEmail);
      await user.click(button);
      expect(sendResetPasswordMock).toHaveBeenCalledWith({ email: correctEmail });
    });
    test('When Pending shows spinner, disable button and hide inputs', async () => {
      sendResetPasswordMock.mockReturnValue({ unwrap: () => new Promise(() => {}) });
      (useSendResetPasswordEmailMutation as jest.Mock).mockReturnValue([sendResetPasswordMock, { isLoading: true }]);
      const { user, emailInput, button } = basicSetup();
      await user.type(emailInput, correctEmail);
      await user.click(button);
      await waitFor(() => {
        expect(button).toBeDisabled();
        expect(screen.getByTestId('spinner')).toBeInTheDocument();
        expect(screen.queryByTestId('email')).not.toBeInTheDocument();
      });
    });
    test('When Success shows message', async () => {
      sendResetPasswordMock.mockReturnValue({ unwrap: () => Promise.resolve() });
      const { user, emailInput, button } = basicSetup();
      await user.type(emailInput, correctEmail);
      await user.click(button);
      await waitFor(() => {
        expect(screen.getByText(/Reset password email has been send/i)).toBeInTheDocument();
        expect(button).toBeDisabled();
        expect(screen.queryByTestId('email')).not.toBeInTheDocument();
      });
    });
    test('When Error shows message default', async () => {
      sendResetPasswordMock.mockReturnValue({ unwrap: () => Promise.reject({ data: {} }) });
      const { user, emailInput, button } = basicSetup();
      await user.type(emailInput, correctEmail);
      await user.click(button);
      await waitFor(() => {
        expect(screen.getByText(/An unexpected error occurred/i)).toBeInTheDocument();
        expect(button).toBeDisabled();
        expect(screen.queryByTestId('email')).not.toBeInTheDocument();
      });
    });
    test('When Error shows message from API when availble', async () => {
      const customErrorMessage = 'Custom error message';
      sendResetPasswordMock.mockReturnValue({ unwrap: () => Promise.reject({ data: { message: customErrorMessage } }) });
      const { user, emailInput, button } = basicSetup();
      await user.type(emailInput, correctEmail);
      await user.click(button);
      await waitFor(() => {
        expect(screen.getByText(customErrorMessage)).toBeInTheDocument();
        expect(button).toBeDisabled();
        expect(screen.queryByTestId('email')).not.toBeInTheDocument();
      });
    });
    test('When Error shows inputs again after 1.5s', async () => {
      sendResetPasswordMock.mockReturnValue({ unwrap: () => Promise.reject({ data: {} }) });
      const { user, emailInput, button } = basicSetup();
      await user.type(emailInput, correctEmail);
      await user.click(button);
      await waitFor(() => {
        expect(screen.getByText(/An unexpected error occurred/i)).toBeInTheDocument();
        expect(button).toBeDisabled();
        expect(screen.queryByTestId('email')).not.toBeInTheDocument();
      });

      await waitFor(
        () => {
          expect(screen.getByLabelText(/Email adress/i)).toBeInTheDocument();
          expect(button).not.toBeDisabled();
          expect(screen.queryByText(/An unexpected error occurred/i)).not.toBeInTheDocument();
        },
        { timeout: 2000 },
      );
    });
  });
});
