import { SearchDirection, SearchField } from '@/hooks/useSearchFilters';
import { UserRegisterRequest } from '@/lib/validation/userAuth';
import { UserLoginRequest } from '@/types/auth';
import { CourseCategory } from '@/types/courses-enum';
import { createApi, fetchBaseQuery, RootState } from '@reduxjs/toolkit/query/react';

const baseQuery = fetchBaseQuery({
  baseUrl: process.env.NEXT_PUBLIC_API_BASE_URL,
  credentials: 'include',
});

// eslint-disable-next-line @typescript-eslint/no-explicit-any
const baseQueryWithReauth: typeof baseQuery = async (args: any, api: any, extraOptions: any) => {
  let result = await baseQuery(args, api, extraOptions);

  if (result?.error?.status === 403) {
    console.log('Attempting token refresh due to 403');
    const refreshResult = await baseQuery(
      {
        url: '/public/refresh-token',
        method: 'POST',
        credentials: 'include',
      },
      api,
      extraOptions,
    );
    console.log(refreshResult);
    if (!refreshResult.error) {
      console.log('Token refresh successful (request succeeded), retrying original request.');

      result = await baseQuery(args, api, extraOptions);
    } else {
      console.error('Token refresh failed:', refreshResult.error);
      await baseQuery(
        {
          url: 'public/logout',
          method: 'POST',
          credentials: 'include',
        },
        api,
        extraOptions,
      );
    }
  }
  return result;
};

export const api = createApi({
  baseQuery: baseQueryWithReauth,
  tagTypes: ['CourseDtos', 'TeachedCourseDtos', 'User'],
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
      query: (token: string) => ({ url: `public/confirm-email`, method: 'POST', body: { token } }),
    }),
    // Resend Confirm Email Token
    resendConfirmEmailToken: build.mutation({ query: () => ({ url: `public/confirm-email/resend`, method: 'POST' }) }),
    // ******************
    // -------USER-------
    // ******************
    getUserInfo: build.query<UserDto, void>({ query: () => ({ url: 'user' }), providesTags: ['User'] }),

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
          ? [
              ...result?.content.map((course) => ({ type: 'CourseDtos' as const, id: course.id })),
              { type: 'CourseDtos' as const, id: `LIST-${arg.page}` },
            ]
          : [{ type: 'CourseDtos' as const, id: `LIST-${arg.page}` }],
    }),
    // *******************
    // --Private Courses--
    // *******************

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
          ? [
              ...result?.content.map((course) => ({ type: 'TeachedCourseDtos' as const, id: course.id })),
              { type: 'TeachedCourseDtos' as const, id: `LIST-${arg.page}` },
            ]
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
          if (
            !key.startsWith('getCoursesTeacher-{') ||
            queries[key]?.status !== 'fulfilled' ||
            !subscriptions[key] ||
            Object.keys(subscriptions[key]).length === 0
          )
            return;

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
            console.error(
              `Error dispatching updateQueryData for key ${key} with args ${JSON.stringify(queryArgs)}:`,
              error,
            );
          }
        }

        try {
          await queryFulfilled;
        } catch (err) {
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

    //
  }),
});

export const {
  useRefetchTokenMutation,
  useGetCoursesPublicQuery,
  useLazyGetCoursesPublicQuery,
  useLoginMutation,
  useLogoutMutation,
  useRegisterMutation,
  useConfirmEmailMutation,
  useResendConfirmEmailTokenMutation,
  useLazyGetCoursesTeacherQuery,
  useCreateCourseMutation,
  useDeleteCourseMutation,
  // USER
  useGetUserInfoQuery
} = api;
