import { render, screen, waitFor } from '@testing-library/react';
import userEvent, { UserEvent } from '@testing-library/user-event';
import TeacherRequestAdmin from '.';
import { useAdminUpdateTeacherRequestsMutation, useLazyGetAdminBecomeUserRequestsQuery } from './api/teacherApplicationAdmin';
import { toast } from 'sonner';
import { createTeacherRequest } from '@/__test__/factories/teacherRequestFactory';
import { TeacherRequest } from '@/types/teacherRequest';
import { createPageResponse } from '@/__test__/factories/pageFactory';
import { SearchDirection, TeacherRequestStatus } from '@/types/search-enums';

const fetchRequests = jest.fn();
const adminUpdateTeacherRequests = jest.fn();

const replace = jest.fn();
const get = jest.fn();

jest.mock('./api/teacherApplicationAdmin', () => ({
  useAdminUpdateTeacherRequestsMutation: jest.fn(),
  useLazyGetAdminBecomeUserRequestsQuery: jest.fn(),
}));

jest.mock('sonner', () => ({
  toast: {
    success: jest.fn(),
    error: jest.fn(),
  },
}));
jest.mock('next/navigation', () => ({
  useRouter: () => ({ replace }),
  useSearchParams: () => ({ get }),
  usePathname: jest.fn(),
}));

beforeEach(() => {
  (useLazyGetAdminBecomeUserRequestsQuery as jest.Mock).mockReturnValue([fetchRequests, { data: { content: [] }, isLoading: false }]);
  (useAdminUpdateTeacherRequestsMutation as jest.Mock).mockReturnValue([adminUpdateTeacherRequests, { isLoading: false }]);
});

afterEach(() => {
  jest.clearAllMocks();
});

const setupTeacherApplications = () => {
  const teacherRequest = createTeacherRequest();
  const teacherRequest2 = createTeacherRequest();
  const page = createPageResponse<TeacherRequest>({ content: [teacherRequest, teacherRequest2] });
  (useLazyGetAdminBecomeUserRequestsQuery as jest.Mock).mockReturnValue([fetchRequests, { data: page, isLoading: false }]);
  render(<TeacherRequestAdmin />);
  return { teacherRequest, teacherRequest2 };
};

const getAllInputs = () => {
  render(<TeacherRequestAdmin />);
  const requestStatusSelect = screen.getByTestId('request-status');
  const seenByAdminSelect = screen.getByTestId('seen');
  const directionSelect = screen.getByTestId('direction');
  const sizeSelect = screen.getByTestId('size');
  return { requestStatusSelect, seenByAdminSelect, directionSelect, sizeSelect };
};

const pickFromAllInputs = async () => {
  const user = userEvent.setup();
  const { requestStatusSelect, seenByAdminSelect, directionSelect, sizeSelect } = getAllInputs();

  await user.click(requestStatusSelect);
  const optionStatus = screen.getByRole('option', { name: /denied/i });
  await user.click(optionStatus);

  await user.click(seenByAdminSelect);
  const optionSeen = screen.getByRole('option', { name: /true/i });
  await user.click(optionSeen);

  await user.click(directionSelect);
  const optionDir = screen.getByRole('option', { name: /Oldest first/i });
  await user.click(optionDir);

  await user.click(sizeSelect);
  const optionSize = screen.getByRole('option', { name: /24/i });
  await user.click(optionSize);

  const button = screen.getByText('Search');
  await user.click(button);
};

type Status = 'denied' | 'pending' | 'accepted';

const statusRegex: Record<Status, RegExp> = {
  denied: /denied/i,
  pending: /pending/i,
  accepted: /accepted/i,
};

const changeStatus = async (request: TeacherRequest, user: UserEvent, status: Status = 'denied') => {
  const requestStatusSelect = screen.getByLabelText(`change-status-for-${request.user.email}`);
  await user.click(requestStatusSelect);

  const option = screen.getByRole('option', { name: statusRegex[status] });
  await user.click(option);
};

describe('TeacherRequestAdminPage', () => {
  describe('Renders', () => {
    test('renders loading spinner', async () => {
      (useLazyGetAdminBecomeUserRequestsQuery as jest.Mock).mockReturnValue([fetchRequests, { data: [], isLoading: true }]);
      render(<TeacherRequestAdmin />);
      await waitFor(() => screen.getByTestId('loading-spinner'));
    });
    test('renders no requests found message', async () => {
      (useLazyGetAdminBecomeUserRequestsQuery as jest.Mock).mockReturnValue([fetchRequests, { data: { content: [] }, isLoading: false }]);
      render(<TeacherRequestAdmin />);
      await waitFor(() => screen.getByText('No Requests Found'));
    });
    test('renders teacher request items', async () => {
      const { teacherRequest, teacherRequest2 } = setupTeacherApplications();

      await waitFor(() => {
        screen.getByText(teacherRequest.user.username + ' ' + teacherRequest.user.userLastname);
        screen.getByText(teacherRequest2.user.username + ' ' + teacherRequest2.user.userLastname);
      });
    });
    test('renders pagination, button, filter', async () => {
      setupTeacherApplications();
      await waitFor(() => {
        expect(screen.getByTestId('pagination-component')).toBeInTheDocument();
        expect(screen.getByTestId('filter')).toBeInTheDocument();
        expect(screen.getByText('Save Changes')).toBeInTheDocument();
        expect(screen.getByText('Search')).toBeInTheDocument();
      });
    });
  });
  describe('Interactions', () => {
    test('changed values are reflected', async () => {
      const { teacherRequest, teacherRequest2 } = setupTeacherApplications();
      const user = userEvent.setup();
      await changeStatus(teacherRequest, user, 'accepted');
      await changeStatus(teacherRequest2, user, 'denied');
      await waitFor(() => {
        expect(screen.getByText('Accepted')).toBeInTheDocument();
        expect(screen.getByText('Denied')).toBeInTheDocument();
        expect(screen.queryByText('Pending')).not.toBeInTheDocument();
        expect((screen.getByLabelText(`seen-by-admin-for-${teacherRequest.user.email}`) as HTMLInputElement).checked).toBe(true);
        expect((screen.getByLabelText(`seen-by-admin-for-${teacherRequest2.user.email}`) as HTMLInputElement).checked).toBe(true);
      });
    });
  });
  describe('Api Interaction', () => {
    describe('adminUpdateTeacherRequests', () => {
      test('is being called with proper data (seenByAdmin false or pending) are not sent', async () => {
        const teacherRequest = createTeacherRequest();
        const teacherRequest2 = createTeacherRequest();
        const teacherRequest3 = createTeacherRequest();

        const page = createPageResponse<TeacherRequest>({ content: [teacherRequest, teacherRequest2, teacherRequest3] });
        (useLazyGetAdminBecomeUserRequestsQuery as jest.Mock).mockReturnValue([fetchRequests, { data: page, isLoading: false }]);
        adminUpdateTeacherRequests.mockReturnValue({ unwrap: () => Promise.resolve() });
        render(<TeacherRequestAdmin />);
        const user = userEvent.setup();
        await changeStatus(teacherRequest, user, 'accepted');
        await changeStatus(teacherRequest, user, 'pending');

        await changeStatus(teacherRequest2, user, 'denied');
        const seenByAdminSelect = screen.getByLabelText(`seen-by-admin-for-${teacherRequest2.user.email}`);
        await user.click(seenByAdminSelect);

        await changeStatus(teacherRequest3, user, 'accepted');

        await user.click(screen.getByText('Save Changes'));

        await waitFor(() => {
          expect(adminUpdateTeacherRequests).toHaveBeenCalledWith([{ id: teacherRequest3.id, status: 'ACCEPTED', seenByAdmin: true }]);
        });
      });

      test('When two changes to one item only one is  sent', async () => {
        adminUpdateTeacherRequests.mockReturnValue({ unwrap: () => Promise.resolve() });
        const { teacherRequest } = setupTeacherApplications();
        const user = userEvent.setup();
        await changeStatus(teacherRequest, user, 'accepted');
        await changeStatus(teacherRequest, user, 'denied');
        await user.click(screen.getByText('Save Changes'));
        await waitFor(() => {
          expect(adminUpdateTeacherRequests).toHaveBeenCalledWith([{ id: teacherRequest.id, status: 'DENIED', seenByAdmin: true }]);
        });
      });

      test('is being called with proper data and shows success message', async () => {
        adminUpdateTeacherRequests.mockReturnValue({ unwrap: () => Promise.resolve() });
        const { teacherRequest: request } = setupTeacherApplications();

        const user = userEvent.setup();
        await changeStatus(request, user);
        await user.click(screen.getByText('Save Changes'));
        await waitFor(() => {
          expect(adminUpdateTeacherRequests).toHaveBeenCalledWith([{ id: request.id, status: 'DENIED', seenByAdmin: true }]);
          expect(toast.success).toHaveBeenCalledWith('Data Updated');
        });
      });
      test('shows loading state', async () => {
        const request = createTeacherRequest();
        const page = createPageResponse<TeacherRequest>({ content: [request] });
        (useLazyGetAdminBecomeUserRequestsQuery as jest.Mock).mockReturnValue([fetchRequests, { data: page, isLoading: false }]);
        (useAdminUpdateTeacherRequestsMutation as jest.Mock).mockReturnValue([adminUpdateTeacherRequests, { isLoading: true }]);
        render(<TeacherRequestAdmin />);

        const user = userEvent.setup();
        await changeStatus(request, user);
        await user.click(screen.getByText('Save Changes'));

        await waitFor(() => {
          expect(screen.getByTestId('spinner')).toBeInTheDocument();
        });
      });
      test('shows error message if availble', async () => {
        adminUpdateTeacherRequests.mockReturnValue({ unwrap: () => Promise.reject({ data: { message: 'Error message' } }) });
        const { teacherRequest: request } = setupTeacherApplications();

        const user = userEvent.setup();
        await changeStatus(request, user);
        await user.click(screen.getByText('Save Changes'));

        await waitFor(() => {
          expect(toast.error).toHaveBeenCalledWith('Error message');
        });
      });
      test('shows default error message if not availble', async () => {
        adminUpdateTeacherRequests.mockReturnValue({ unwrap: () => Promise.reject() });
        const { teacherRequest: request } = setupTeacherApplications();

        const user = userEvent.setup();
        await changeStatus(request, user);
        await user.click(screen.getByText('Save Changes'));

        await waitFor(() => {
          expect(toast.error).toHaveBeenCalledWith('Something went wrong, try again later');
        });
      });
    });
    describe('fetchBecomeTeacherRequest', () => {
      test('calls with default values on page load', async () => {
        render(<TeacherRequestAdmin />);
        await waitFor(() => {
          expect(fetchRequests).toHaveBeenCalledWith({
            page: 0,
            size: 12,
            direction: SearchDirection.ASC,
            status: TeacherRequestStatus.PENDING,
            seenByAdmin: 'false',
          });
          expect(fetchRequests).toHaveBeenCalledTimes(1);
        });
      });
      test('calls with changed values', async () => {
        await pickFromAllInputs();
        await waitFor(() => {
          expect(fetchRequests).toHaveBeenCalledWith({
            page: 0,
            size: 24,
            direction: SearchDirection.DESC,
            status: TeacherRequestStatus.DENIED,
            seenByAdmin: 'true',
          });
          expect(fetchRequests).toHaveBeenCalledTimes(2);
        });
      });
      test('calls on page change', async () => {
        const page = createPageResponse<TeacherRequest>({
          size: 2,
          totalPages: 5,
          totalElements: 10,
          content: [createTeacherRequest(), createTeacherRequest()],
        });
        (useLazyGetAdminBecomeUserRequestsQuery as jest.Mock).mockReturnValue([fetchRequests, { data: page, isLoading: false }]);
        render(<TeacherRequestAdmin />);
        expect(fetchRequests).toHaveBeenCalledTimes(1);
        const pageNextButton = screen.getByLabelText(/next/i);
        await userEvent.click(pageNextButton);
        await waitFor(() => {
          expect(fetchRequests).toHaveBeenCalledTimes(2);
        });
      });
    });
  });
});
