import { UpdateTeacherRequest } from '@/app/(dashboard)/admin/teacher-requests/page';
import { api } from '../../api';
import { BecomeTeacherRequest } from '@/types/user';
import { BecomeTeacherRequestStatus, SearchDirection } from '@/types/search-enums';

type PaginationProps = {
  page?: number;
  size?: number;
  direction?: SearchDirection;
  seenByAdmin?: 'true' | 'false';
  status?: BecomeTeacherRequestStatus;
};

export const teacherApplicationAdminApi = api.injectEndpoints({
  endpoints: (build) => ({
    getNewTeacherApplicationNumber: build.query<{ newRequests: number }, void>({
      query: () => ({ url: 'admin/teacher-application/notifications', method: 'GET' }),
      providesTags: ['TeacherApplicationNumber'],
    }),

    getAdminBecomeUserRequests: build.query<Page<BecomeTeacherRequest>, PaginationProps>({
      query: ({ page, size, direction, seenByAdmin, status }) => ({
        url: 'admin/teacher-application',
        method: 'GET',
        params: { page, size, direction, seenByAdmin, status },
      }),
      keepUnusedDataFor: 0,
    }),

    adminUpdateTeacherRequests: build.mutation<void, UpdateTeacherRequest[]>({
      query: (requests) => ({
        url: 'admin/teacher-application',
        method: 'PATCH',
        body: requests,
      }),
      async onQueryStarted(requests, { dispatch, queryFulfilled }) {
        const patchItemsNumber = requests.length;
        const patchResult = dispatch(
          // @ts-expect-error its redux error, code is correct
          api.util.updateQueryData('getNewTeacherApplicationNumber', undefined, (draft: { newRequests: number }) => {
            draft.newRequests -= patchItemsNumber;
          }),
        );
        try {
          await queryFulfilled;
        } catch (err) {
          patchResult.undo();
          console.error('Error removing notifications, reverting optimistic update:', err);
        }
      },
      invalidatesTags: ['TeacherApplicationNumber'],
    }),
  }),
});

export const { useGetNewTeacherApplicationNumberQuery, useLazyGetAdminBecomeUserRequestsQuery, useAdminUpdateTeacherRequestsMutation } =
  teacherApplicationAdminApi;
