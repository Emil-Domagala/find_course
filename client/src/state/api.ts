import { UpdateTeacherRequest } from '@/app/(dashboard)/admin/teacher-requests/page';
import { ForgotPasswordRequest, UserRegisterRequest } from '@/lib/validation/userAuth';
import { UserLoginRequest } from '@/types/auth';
import { CartDto, ChapterDetailsProtectedDto, CourseDetailsPublicDto, CourseDtoWithFirstChapter, CourseProgress, UpdateProgressRequest } from '@/types/courses';
import { CourseCategory } from '@/types/courses-enum';
import { BecomeTeacherRequestStatus, SearchDirection, SearchField } from '@/types/enums';
import { TransactionDto } from '@/types/payments';
import { BecomeTeacherRequest } from '@/types/user';
import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';

const baseQuery = fetchBaseQuery({
  baseUrl: process.env.NEXT_PUBLIC_API_BASE_URL,
  credentials: 'include',
});
// eslint-disable-next-line @typescript-eslint/no-explicit-any
const baseQueryWithReauth: typeof baseQuery = async (args: any, api: any, extraOptions: any) => {
  let result = await baseQuery(args, api, extraOptions);
  // TODO: change to !== 440 
  if (result?.error?.status === 403) {
    const refreshResult = await baseQuery(
      {
        url: '/public/refresh-token',
        method: 'POST',
        credentials: 'include',
      },
      api,
      extraOptions,
    );
    if (!refreshResult.error) {
      result = await baseQuery(args, api, extraOptions);
    } else {
      await baseQuery(
        {
          url: 'public/logout',
          method: 'POST',
          credentials: 'include',
        },
        api,
        extraOptions,
      );
      window.location.href = '/auth/login';
    }
  }
  return result;
};

export const api = createApi({
  baseQuery: baseQueryWithReauth,
  tagTypes: ['CourseDtos', 'TeachedCourseDtos', 'User', 'Cart', 'AdminNotyfication', 'UserCourseProgress', 'Chapter'],
  endpoints: (build) => ({
    // *******************
    // -------AUTH--------
    // *******************
    refetchToken: build.mutation({
      query: () => ({
        url: 'public/refresh-token',
        method: 'POST',
      }),
    }),

    // LOGIN
    login: build.mutation({
      query: (credentials: UserLoginRequest) => ({ url: 'public/login', method: 'POST', body: credentials }),
    }),
    // Logout
    logout: build.mutation({ query: () => ({ url: 'public/logout', method: 'POST' }) }),
    // Register
    register: build.mutation({
      query: (credentials: UserRegisterRequest) => ({ url: 'public/register', method: 'POST', body: credentials }),
    }),
    // Confirm Email
    confirmEmail: build.mutation({
      query: (token: string) => ({ url: `confirm-email`, method: 'POST', body: { token } }),
    }),
    // Resend Confirm Email Token
    resendConfirmEmailToken: build.mutation({ query: () => ({ url: `confirm-email/resend`, method: 'POST' }) }),
    //send Reset password email
    sendResetPasswordEmail: build.mutation<string, ForgotPasswordRequest>({
      query: ({ email }) => ({ url: `public/forgot-password`, method: 'POST', body: { email } }),
    }),
    // Reset password
    resetPassword: build.mutation<void, { token: string; password: string }>({
      query: ({ token, password }) => ({
        url: `public/reset-password?token=${token}`,
        method: 'POST',
        body: { password },
      }),
    }),

    // ******************
    // -------USER-------
    // ******************
    getUserInfo: build.query<UserDto, void>({ query: () => ({ url: 'user' }), providesTags: ['User'] }),
    deleteUser: build.mutation<void, void>({
      query: () => ({ url: 'user', method: 'DELETE' }),
      invalidatesTags: ['User'],
    }),
    // Update User
    updateUserInfo: build.mutation<UserDto, FormData>({
      query: (user) => ({ url: 'user', method: 'PATCH', body: user }),
      async onQueryStarted(_arg, { dispatch, queryFulfilled }) {
        try {
          const { data: updatedUser } = await queryFulfilled;
          dispatch(
            api.util.updateQueryData('getUserInfo', undefined, (draft: UserDto) => {
              console.log(draft);
              return updatedUser;
            }),
          );
        } catch (err) {
          console.log(err);
        }
      },
    }),
    // Seend Become teacher request
    sendBecomeTeacherRequest: build.mutation<void, void>({
      query: () => ({ url: 'user/become-teacher', method: 'POST' }),
    }),
    // Get Become teacher request status
    getBecomeTeacherRequestStatus: build.query<BecomeTeacherRequest, void>({
      query: () => ({ url: 'user/become-teacher' }),
    }),

    // ******************
    // --Public Courses--
    // ******************

    // GET LIST OF PUBLIC DTO COURSES
    getCoursesPublic: build.query<
      Page<CourseDto>,
      {
        page?: number;
        size?: number;
        sortField?: SearchField | '';
        direction?: SearchDirection;
        keyword?: string;
        category?: CourseCategory | '';
      }
    >({
      query: ({ page, size, sortField, direction, keyword = '', category }) => ({
        url: 'public/courses',
        params: { page, size, sortField, direction, keyword, category },
      }),
      serializeQueryArgs: ({ endpointName, queryArgs }) => {
        const { size, sortField, direction, keyword, category } = queryArgs;
        return `${endpointName}-${JSON.stringify({ size, sortField, direction, keyword, category })}-page-${queryArgs.page}`;
      },

      providesTags: (result, error, arg) =>
        result
          ? [...result?.content.map((course) => ({ type: 'CourseDtos' as const, id: course.id })), { type: 'CourseDtos' as const, id: `LIST-${arg.page}` }]
          : [{ type: 'CourseDtos' as const, id: `LIST-${arg.page}` }],
    }),
    // *******************
    // --Private Courses--
    // *******************

    getEnrolledCourses: build.query<
      Page<CourseDtoWithFirstChapter>,
      {
        page?: number;
        size?: number;
        sortField?: SearchField | '';
        direction?: SearchDirection;
        keyword?: string;
        category?: CourseCategory | '';
      }
    >({
      query: ({ page, size, sortField, direction, keyword = '', category }) => ({
        url: 'user/courses',
        params: { page, size, sortField, direction, keyword, category },
      }),
    }),

    // *****************
    // -----Teacher-----
    // *****************
    // GET LIST OF TEACHER DTO COURSES
    getCoursesTeacher: build.query<
      Page<CourseDto>,
      {
        page?: number;
        size?: number;
        sortField?: SearchField | '';
        direction?: SearchDirection;
        keyword?: string;
        category?: CourseCategory | '';
      }
    >({
      query: ({ page, size, sortField, direction, keyword = '', category }) => ({
        url: 'teacher/courses',
        params: { page, size, sortField, direction, keyword, category },
      }),
      serializeQueryArgs: ({ endpointName, queryArgs }) => {
        const { size, sortField, direction, keyword, category } = queryArgs;
        return `${endpointName}-${JSON.stringify({ size, sortField, direction, keyword, category })}-page-${queryArgs.page}`;
      },

      providesTags: (result, error, arg) =>
        result
          ? [...result?.content.map((course) => ({ type: 'TeachedCourseDtos' as const, id: course.id })), { type: 'TeachedCourseDtos' as const, id: `LIST-${arg.page}` }]
          : [{ type: 'TeachedCourseDtos' as const, id: `LIST-${arg.page}` }],
    }),

    // create mock course
    createCourse: build.mutation<CourseDto, void>({
      query: () => ({ url: 'teacher/courses', method: 'POST' }),
    }),
    // DELETE COURSE
    deleteCourse: build.mutation<void, { courseId: string }>({
      query: ({ courseId }) => ({
        url: `teacher/courses/${courseId}`,
        method: 'DELETE',
      }),
      async onQueryStarted({ courseId }, { dispatch, queryFulfilled, getState }) {
        const patches: { undo: () => void }[] = [];
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        const state = getState() as any;
        const subscriptions = state.api?.subscriptions;
        const queries = state.api?.queries;

        if (!subscriptions || !queries) return;
        for (const key in queries) {
          if (!key.startsWith('getCoursesTeacher-{') || queries[key]?.status !== 'fulfilled' || !subscriptions[key] || Object.keys(subscriptions[key]).length === 0) return;

          const cacheEntry = queries[key];
          const queryArgs = cacheEntry.originalArgs;
          if (!queryArgs) continue;
          try {
            const patchResult = dispatch(
              api.util.updateQueryData('getCoursesTeacher', queryArgs, (draft: Page<CourseDto>) => {
                if (!draft || !draft.content || !Array.isArray(draft.content)) return;

                const index = draft.content.findIndex((course) => course.id === courseId);

                if (index !== -1) {
                  draft.content.splice(index, 1);
                  if (typeof draft.totalElements === 'number') {
                    draft.totalElements--;
                  }
                }
              }),
            );
            patches.push(patchResult);
          } catch (error) {
            console.error(`Error dispatching updateQueryData for key ${key} with args ${JSON.stringify(queryArgs)}:`, error);
          }
        }

        try {
          await queryFulfilled;
        } catch (err) {
          console.log(err);
          patches.forEach((patch) => patch.undo());
        }
      },

      invalidatesTags: (result, error, { courseId }) =>
        error
          ? []
          : [
              { type: 'TeachedCourseDtos', id: courseId },
              { type: 'TeachedCourseDtos', id: 'LIST' },
            ],
    }),

    getTeacherCourseById: build.query<CourseDetailsPublicDto, string>({ query: (courseId) => ({ url: `teacher/courses/${courseId}` }) }),

    updateCourse:build.mutation<void,{courseId:string,courseData:FormData}>({
      query:({courseData,courseId})=>({url:`teacher/courses/${courseId}`,method:'PATCH',body:courseData}),
      invalidatesTags:['TeachedCourseDtos']}),
    // ****************
    // ------Cart------
    // ****************
    getCart: build.query<CartDto, void>({
      query: () => ({ url: 'cart' }),
      keepUnusedDataFor: 0,
      providesTags: ['Cart'],
    }),
    addCourseToCart: build.mutation<CartDto, { courseId: string }>({
      query: ({ courseId }) => ({ url: `cart/${courseId}`, method: 'POST' }),
      invalidatesTags: ['Cart'],
    }),
    removeCourseFromCart: build.mutation<CartDto, { courseId: string }>({
      query: ({ courseId }) => ({ url: `cart/${courseId}`, method: 'DELETE' }),
      async onQueryStarted({ courseId }, { dispatch, queryFulfilled }) {
        const patchResult = dispatch(
          api.util.updateQueryData('getCart', undefined, (draft: CartDto) => {
            if (!draft || !draft.courses || !Array.isArray(draft.courses)) {
              console.warn('Draft cart data is not in the expected format for optimistic update.');
              return;
            }
            const courseIndex = draft.courses.findIndex((course) => course.id === courseId);
            if (courseIndex !== -1) {
              const courseToRemove = draft.courses[courseIndex];
              if (typeof courseToRemove.price === 'number') {
                draft.totalPrice -= courseToRemove.price;
              }
              draft.courses.splice(courseIndex, 1);
            }
          }),
        );
        try {
          await queryFulfilled;
        } catch (err) {
          patchResult.undo();
          console.error('Error removing course from cart, reverting optimistic update:', err);
        }
      },
      invalidatesTags: ['Cart'],
    }),
    // ****************
    // --TRANSACTIONS--
    // ****************
    createStripePaymentIntent: build.mutation<{ clientSecret: string }, void>({
      query: () => ({ url: 'transaction/stripe/create-payment-intent', method: 'POST' }),
    }),
    getTransactions: build.query<
      Page<TransactionDto>,
      {
        page?: number;
        size?: number;
        sortField?: string | '';
        direction?: SearchDirection;
      }
    >({
      query: ({ page, size, sortField, direction }) => ({
        url: 'transaction',
        params: { page, size, sortField, direction },
        method: 'GET',
      }),
    }),
    // ****************
    // -----ADMIN-----
    // ****************
    getAdminNotyfication: build.query<{ newRequests: number }, void>({
      query: () => ({ url: 'admin/notifications/teacher-requests', method: 'GET' }),
      providesTags: ['AdminNotyfication'],
    }),
    getAdminBecomeUserRequests: build.query<
      Page<BecomeTeacherRequest>,
      { page?: number; size?: number; direction?: SearchDirection; seenByAdmin?: 'true' | 'false'; status?: BecomeTeacherRequestStatus }
    >({
      query: ({ page, size, direction, seenByAdmin, status }) => ({
        url: 'admin/teacher-requests',
        method: 'GET',
        params: { page, size, direction, seenByAdmin, status },
      }),
      keepUnusedDataFor: 0,
    }),
    adminUpdateTeacherRequests: build.mutation<void, UpdateTeacherRequest[]>({
      query: (requests) => ({
        url: 'admin/teacher-requests',
        method: 'PATCH',
        body: requests,
      }),
      async onQueryStarted(requests, { dispatch, queryFulfilled }) {
        const patchItemsNumber = requests.length;
        const patchResult = dispatch(
          api.util.updateQueryData('getAdminNotyfication', undefined, (draft: { newRequests: number }) => {
            draft.newRequests -= patchItemsNumber;
          }),
        );
        try {
          await queryFulfilled;
        } catch (err) {
          patchResult.undo();
          console.error('Error removing notyfications, reverting optimistic update:', err);
        }
      },
      invalidatesTags: ['AdminNotyfication'],
    }),
    // ***************
    // --PROGRESSBAR--
    // ***************
    // Fetch user course progress with course structure
    getUserCourseProgress: build.query<CourseProgress, { courseId: string }>({
      query: ({ courseId }) => ({
        url: `/progress/${courseId}`,
        method: 'GET',
      }),
      providesTags: ['UserCourseProgress'],
    }),

    // Update course progress
    updateCourseChapterProgress: build.mutation<void, { courseId: string; request: UpdateProgressRequest }>({
      query: ({ courseId, request }) => ({
        url: `/progress/${courseId}`,
        method: 'PATCH',
        body: request,
      }),
      async onQueryStarted({ courseId, request }, { dispatch, queryFulfilled }) {
        const chapterIdToUpdate = request.chapterProgressId;
        const newCompletedStatus = request.completed;

        const patchResult = dispatch(
          api.util.updateQueryData('getUserCourseProgress', { courseId }, (draft: CourseProgress) => {
            if (!draft || !draft.sections) return;

            let found = false;

            for (const section of draft.sections) {
              if (section.chapters) {
                const chapterIndex = section.chapters.findIndex((chapter) => chapter.id === chapterIdToUpdate);
                if (chapterIndex !== -1) {
                  section.chapters[chapterIndex].completed = newCompletedStatus;
                  found = true;
                  break;
                }
              }
            }

            if (!found) {
              console.warn(`Optimistic update: Chapter with ID ${chapterIdToUpdate} not found in draft data.`);
            }
          }),
        );

        try {
          await queryFulfilled;
        } catch (e) {
          console.log(e);
          patchResult.undo();
        }
      },
    }),

    // Fetch Chapter whole data
    getChapterEnrolledStudent: build.query<ChapterDetailsProtectedDto, { courseId: string; chapterId: string }>({
      query: ({ courseId, chapterId }) => ({
        url: `/user/courses/${courseId}/chapters/${chapterId}`,
        method: 'GET',
      }),
      keepUnusedDataFor:600,
      providesTags: ['Chapter'],
    }),
  }),
});

export const {
  // Auth
  useResetPasswordMutation,
  useSendResetPasswordEmailMutation,
  useRefetchTokenMutation,
  useLoginMutation,
  useLogoutMutation,
  useRegisterMutation,
  useConfirmEmailMutation,
  useResendConfirmEmailTokenMutation,
  // USER
  useGetUserInfoQuery,
  useDeleteUserMutation,
  useUpdateUserInfoMutation,
  useSendBecomeTeacherRequestMutation,
  useGetBecomeTeacherRequestStatusQuery,
  // Teacher
  useLazyGetCoursesTeacherQuery,
  useCreateCourseMutation,
  useDeleteCourseMutation,
  useGetTeacherCourseByIdQuery,
  useUpdateCourseMutation,
  // Student
  useLazyGetEnrolledCoursesQuery,
  // Courses
  useGetCoursesPublicQuery,
  useLazyGetCoursesPublicQuery,
  // Cart
  useGetCartQuery,
  useAddCourseToCartMutation,
  useRemoveCourseFromCartMutation,
  // TRANSACTIONS
  useCreateStripePaymentIntentMutation,
  useLazyGetTransactionsQuery,
  // ADMIN
  useGetAdminNotyficationQuery,
  useLazyGetAdminBecomeUserRequestsQuery,
  useAdminUpdateTeacherRequestsMutation,
  // PROGRESSBAR
  useGetUserCourseProgressQuery,
  useUpdateCourseChapterProgressMutation,
  // Chapter
  useGetChapterEnrolledStudentQuery,
  usePrefetch
} = api;
