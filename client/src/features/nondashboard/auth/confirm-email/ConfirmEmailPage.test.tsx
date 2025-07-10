import ConfirmEmail from '.';
import { render, screen, waitFor } from '@testing-library/react';
import { useRouter } from 'next/navigation';
import { useConfirmEmailMutation, useResendConfirmEmailTokenMutation } from './api';
import userEvent from '@testing-library/user-event';

jest.mock('./api', () => ({
  useConfirmEmailMutation: jest.fn(),
  useResendConfirmEmailTokenMutation: jest.fn(),
}));

jest.mock('next/navigation', () => ({
  useRouter: jest.fn(),
}));

const push = jest.fn();
const refresh = jest.fn();
const confirmEmail = jest.fn();
const resendConfirmEmail = jest.fn();

beforeAll(() => {
  if (!document.elementFromPoint) {
    document.elementFromPoint = () => document.createElement('div');
  }
});

beforeEach(() => {
  (useRouter as jest.Mock).mockReturnValue({ push, refresh });
  (useConfirmEmailMutation as jest.Mock).mockReturnValue([confirmEmail]);
  (useResendConfirmEmailTokenMutation as jest.Mock).mockReturnValue([resendConfirmEmail, { isLoading: false }]);
  confirmEmail.mockReset();
  resendConfirmEmail.mockReset();
  push.mockReset();
  refresh.mockReset();
});

const getOTPInput = () => {
  try {
    const inputs = screen.getAllByRole('textbox');
    return inputs.find((input) => input.getAttribute('autocomplete') === 'one-time-code');
  } catch {
    return undefined;
  }
};

describe('ConfirmEmailPage', () => {
  describe('Rendering', () => {
    test('inputs and button', () => {
      render(<ConfirmEmail />);
      const inputs = screen.getAllByRole('textbox');
      const otpInput = inputs.find((input) => input.getAttribute('autocomplete') === 'one-time-code');
      expect(otpInput).toBeInTheDocument();
      const button = screen.getByRole('button', { name: /Resend/i });
      expect(button).toBeInTheDocument();
    });
  });

  describe('Confirm Email', () => {
    describe('Form submission & API', () => {
      test('on sucess redirect and hide ott', async () => {
        render(<ConfirmEmail />);

        const otpInput = getOTPInput();
        expect(otpInput).toBeInTheDocument();

        confirmEmail.mockReturnValue({ unwrap: () => Promise.resolve() });

        const user = userEvent.setup();
        await user.type(otpInput!, '123456');

        expect(confirmEmail).toHaveBeenCalledTimes(1);
        expect(otpInput).not.toBeInTheDocument();

        await waitFor(
          () => {
            expect(push).toHaveBeenCalledTimes(1);
            expect(push).toHaveBeenCalledWith('/user/courses');
          },
          { timeout: 2000 },
        );
      });

      test('When no message in API error response, shows default message', async () => {
        render(<ConfirmEmail />);

        const otpInput = getOTPInput();
        expect(otpInput).toBeInTheDocument();

        confirmEmail.mockReturnValue({ unwrap: () => Promise.reject({ data: {} }) });
        const user = userEvent.setup();
        await user.type(otpInput!, '123456');
        expect(confirmEmail).toHaveBeenCalledTimes(1);
        expect(otpInput).not.toBeInTheDocument();
        await waitFor(() => {
          expect(screen.getByText(/An unexpected error occurred./i)).toBeInTheDocument();
        });
      });

      test('Shows api error messsage', async () => {
        render(<ConfirmEmail />);

        const otpInput = getOTPInput();
        expect(otpInput).toBeInTheDocument();

        confirmEmail.mockReturnValue({ unwrap: () => Promise.reject({ data: { message: 'Invalid OTP' } }) });
        const user = userEvent.setup();
        await user.type(otpInput!, '123456');
        expect(confirmEmail).toHaveBeenCalledTimes(1);
        expect(otpInput).not.toBeInTheDocument();
        await waitFor(() => {
          expect(screen.getByText(/Invalid OTP/i)).toBeInTheDocument();
        });
      });

      test('OTP is showed again after 1.5 seconds after sending wrong OTP', async () => {
        render(<ConfirmEmail />);

        const otpInput = getOTPInput();
        expect(otpInput).toBeInTheDocument();

        confirmEmail.mockReturnValue({ unwrap: () => Promise.reject({ data: { message: 'Invalid OTP' } }) });
        const user = userEvent.setup();
        await user.type(otpInput!, '123456');
        expect(confirmEmail).toHaveBeenCalledTimes(1);
        expect(otpInput).not.toBeInTheDocument();

        await waitFor(
          () => {
            const newOtpInput = getOTPInput();
            expect(newOtpInput).toBeInTheDocument();
          },
          { timeout: 2000 },
        );
      });
    });
  });

  describe('Resend Confirm Email', () => {
    describe('Form submission & API', () => {
      test('Resend Emaail is called', async () => {
        render(<ConfirmEmail />);
        const button = screen.getByRole('button', { name: /Resend/i });

        resendConfirmEmail.mockReturnValue({ unwrap: () => new Promise(() => {}) });
        await userEvent.click(button);
        expect(resendConfirmEmail).toHaveBeenCalledTimes(1);
        await waitFor(() => {
          const otpInput = getOTPInput();
          expect(otpInput).toBeUndefined();
          expect(screen.getByText(/Sending request.../i)).toBeInTheDocument();
        });
      });

      test('display confirmation that email was send', async () => {
        render(<ConfirmEmail />);
        const button = screen.getByRole('button', { name: /Resend/i });
        resendConfirmEmail.mockReturnValue({ unwrap: () => Promise.resolve() });
        await userEvent.click(button);
        expect(resendConfirmEmail).toHaveBeenCalledTimes(1);
        await waitFor(() => {
          const otpInput = getOTPInput();
          expect(otpInput).toBeUndefined();
          expect(screen.getByText(/New confirmation email sent!/i)).toBeInTheDocument();
        });
      });

      test('button is disabled and shows loading', () => {
        (useResendConfirmEmailTokenMutation as jest.Mock).mockReturnValue([resendConfirmEmail, { isLoading: true }]);

        render(<ConfirmEmail />);

        const button = screen.getByRole('button', { name: /Resend/i });
        expect(button).toBeDisabled();
      });

      test('Shows api error messsage', async () => {
        render(<ConfirmEmail />);
        const user = userEvent.setup();
        const button = screen.getByRole('button', { name: /Resend/i });
        resendConfirmEmail.mockReturnValue({ unwrap: () => Promise.reject({ data: { message: 'Invalid OTP' } }) });
        await user.click(button);
        expect(resendConfirmEmail).toHaveBeenCalledTimes(1);

        await waitFor(() => {
          expect(screen.getByText(/Invalid OTP/i)).toBeInTheDocument();
        });
      });

      test('shows default error message', async () => {
        render(<ConfirmEmail />);
        const user = userEvent.setup();
        const button = screen.getByRole('button', { name: /Resend/i });
        resendConfirmEmail.mockReturnValue({ unwrap: () => Promise.reject({}) });
        await user.click(button);
        expect(resendConfirmEmail).toHaveBeenCalledTimes(1);

        await waitFor(() => {
          expect(screen.getByText(/An unexpected error occurred./i)).toBeInTheDocument();
        });
      });

      test('shows input after 1.5 seconds', async () => {
        render(<ConfirmEmail />);
        const user = userEvent.setup();
        const button = screen.getByRole('button', { name: /Resend/i });
        resendConfirmEmail.mockReturnValue({ unwrap: () => Promise.resolve() });
        await user.click(button);
        expect(resendConfirmEmail).toHaveBeenCalledTimes(1);
        await waitFor(
          () => {
            const otpInput = getOTPInput();
            expect(otpInput).toBeInTheDocument();
          },
          { timeout: 2000 },
        );
      });
    });
  });
});
