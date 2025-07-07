import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import TeacherRequestAdminPage from './page';
import { useAdminUpdateTeacherRequestsMutation, useLazyGetAdminBecomeUserRequestsQuery } from '@/state/endpoints/teacherApplication/teacherApplicationAdmin';
import { toast } from 'sonner';
import { createTeacherRequest } from '@/__test__/factories/teacherRequestFactory';
import { TeacherRequest } from '@/types/teacherRequest';
import { createPageResponse } from '@/__test__/factories/pageFactory';

const fetchBecomeTeacherRequest = jest.fn();
const adminUpdateTeacherRequests = jest.fn();

const replace = jest.fn();
const get = jest.fn();

jest.mock('@/state/endpoints/teacherApplication/teacherApplicationAdmin', () => ({
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
  (useLazyGetAdminBecomeUserRequestsQuery as jest.Mock).mockReturnValue([fetchBecomeTeacherRequest, { data: [], isLoading: false }]);
  (useAdminUpdateTeacherRequestsMutation as jest.Mock).mockReturnValue([adminUpdateTeacherRequests, { isLoading: false }]);
});

afterEach(() => {
  jest.clearAllMocks();
});

const setupTeacherApplications = () => {
  const teacherRequest = createTeacherRequest();
  const teacherRequest2 = createTeacherRequest();
  const page = createPageResponse<TeacherRequest>({ content: [teacherRequest, teacherRequest2] });
  (useLazyGetAdminBecomeUserRequestsQuery as jest.Mock).mockReturnValue([fetchBecomeTeacherRequest, { data: page, isLoading: false }]);
  render(<TeacherRequestAdminPage />);
  return { teacherRequest, teacherRequest2 };
};

describe('TeacherRequestAdminPage', () => {
  describe('Renders', () => {
    test('renders loading spinner', async () => {
      (useLazyGetAdminBecomeUserRequestsQuery as jest.Mock).mockReturnValue([fetchBecomeTeacherRequest, { data: [], isLoading: true }]);
      render(<TeacherRequestAdminPage />);
      await waitFor(() => screen.getByTestId('loading-spinner'));
    });
    test('renders no requests found message', async () => {
      (useLazyGetAdminBecomeUserRequestsQuery as jest.Mock).mockReturnValue([fetchBecomeTeacherRequest, { data: { content: [] }, isLoading: false }]);
      render(<TeacherRequestAdminPage />);
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
    test('changed values are reflected', async () => {});
    test('no item duplications', async () => {});
    test('getting rid of seenByAdmin false or pending', async () => {});
  });
  describe('Api Interaction', () => {
    describe('adminUpdateTeacherRequests', () => {
      test('is being called with proper data', async () => {});
      test('shows success toast', async () => {});
      test('shows loading state', async () => {});
      test('shows error message if availble', async () => {});
      test('shows default error message if not availble', async () => {});
    });
    describe('fetchBecomeTeacherRequest', () => {
      test('calls with default values', async () => {});
      test('calls with changed values', async () => {});
      test('calls on page change', async () => {});
    });
  });
});
