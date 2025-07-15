import SendTeacherApplication from './SendTeacherApplication';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { useGetTeacherApplicationInformationQuery, useSendTeacherApplicationMutation } from '../api/teacherApplicationUser';
import { createTeacherRequest } from '@/__test__/factories/teacherRequestFactory';
import { TeacherRequestStatus } from '@/features/dashboard/types/teacherRequestStatus';

const sendBecomeTeacherRequest = jest.fn();

jest.mock('../api/teacherApplicationUser', () => ({
  useSendTeacherApplicationMutation: jest.fn(),
  useGetTeacherApplicationInformationQuery: jest.fn(),
}));

let user: ReturnType<typeof userEvent.setup>;

beforeEach(() => {
  user = userEvent.setup();
  (useSendTeacherApplicationMutation as jest.Mock).mockReturnValue([sendBecomeTeacherRequest]);
});

afterEach(() => {
  jest.clearAllMocks();
});

const setup = async () => {
  render(<SendTeacherApplication />);
  const accTrigger = screen.getByRole('button', { name: 'Teacher Application Status' });
  await user.click(accTrigger);
};

const sendRequest = async () => {
  const btn = screen.getByRole('button', { name: 'Become a Teacher' });
  await user.click(btn);
  return { button: btn };
};

describe('SendTeacherApplication', () => {
  describe('useSendTeacherApplicationMutation', () => {
    beforeEach(() => {
      (useGetTeacherApplicationInformationQuery as jest.Mock).mockReturnValue({ data: undefined, isLoading: false });
    });
    afterEach(() => {
      jest.clearAllMocks();
    });

    test('Button Is displayed', async () => {
      await setup();
      const button = screen.getByLabelText('Become a Teacher');
      expect(button).toBeInTheDocument();
    });
    test('While sending request button is not displayed and "Sending request..." is displayed', async () => {
      sendBecomeTeacherRequest.mockReturnValue({ unwrap: () => new Promise(() => {}) });
      await setup();
      const { button } = await sendRequest();
      await waitFor(() => {
        expect(sendBecomeTeacherRequest).toHaveBeenCalled();
        expect(button).not.toBeInTheDocument();
        expect(screen.getByText('Sending request...')).toBeInTheDocument();
      });
    });
    test('If request send sucessfully "Request sent" is displayed', async () => {
      sendBecomeTeacherRequest.mockReturnValue({ unwrap: () => Promise.resolve() });
      await setup();
      await sendRequest();
      await waitFor(() => {
        expect(sendBecomeTeacherRequest).toHaveBeenCalled();
        expect(screen.getByText('Request sent')).toBeInTheDocument();
      });
    });
    test('If error and msg is availble it is displayed', async () => {
      sendBecomeTeacherRequest.mockReturnValue({ unwrap: () => Promise.reject({ data: { message: 'error msg' } }) });
      await setup();
      await sendRequest();
      await waitFor(() => {
        expect(sendBecomeTeacherRequest).toHaveBeenCalled();
        expect(screen.getByText('error msg')).toBeInTheDocument();
      });
    });
    test('If error but no msg then default msg is displayed', async () => {
      sendBecomeTeacherRequest.mockReturnValue({ unwrap: () => Promise.reject({}) });
      await setup();
      await sendRequest();
      await waitFor(() => {
        expect(sendBecomeTeacherRequest).toHaveBeenCalled();
        expect(screen.getByText('Something went wrong')).toBeInTheDocument();
      });
    });
  });
  describe('useGetTeacherApplicationInformationQuery', () => {
    test('If loading loading state is displayed', async () => {
      (useGetTeacherApplicationInformationQuery as jest.Mock).mockReturnValue({ data: [], isLoading: true });
      render(<SendTeacherApplication />);
      expect(screen.getByTestId('SendTeacherApplicationLoading')).toBeInTheDocument();
    });
    test('If no request found  "Teacher Application Status" is displayed', async () => {
      (useGetTeacherApplicationInformationQuery as jest.Mock).mockReturnValue({ data: undefined, isLoading: false });
      render(<SendTeacherApplication />);
      expect(screen.getByText('Teacher Application Status')).toBeInTheDocument();
    });
    test('If request is found its status is displayed', async () => {
      const request = createTeacherRequest({ status: TeacherRequestStatus.PENDING, createdAt: new Date().toISOString() });
      (useGetTeacherApplicationInformationQuery as jest.Mock).mockReturnValue({ data: request, isLoading: false });
      await setup();

      expect(screen.getByText(/Your request was send/)).toBeInTheDocument();
    });
  });
});
