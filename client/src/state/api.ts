import { SearchDirection, SearchField } from '@/hooks/useSearchFilters';
import { UserRegisterRequest } from '@/lib/validation/userAuth';
import { UserLoginRequest } from '@/types/auth';
import { CourseCategory } from '@/types/courses-enum';
import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';
import { get } from 'http';

const baseQuery = fetchBaseQuery({
  baseUrl: process.env.NEXT_PUBLIC_API_BASE_URL,
  credentials: 'include',
});

// eslint-disable-next-line @typescript-eslint/no-explicit-any
const baseQueryWithReauth: typeof baseQuery = async (args: any, api: any, extraOptions: any) => {
  let result = await baseQuery(args, api, extraOptions);

  if (result?.error?.status === 403) {
    console.log('SENDED REFRESH');
    const refreshResult = await baseQuery(
      {
        url: '/public/refresh-token',
        method: 'POST',
      },
      api,
      extraOptions,
    );
    if (refreshResult.data) {
      result = await baseQuery(args, api, extraOptions);
    } else {
      await baseQuery(
        {
          url: 'public/logout',
          method: 'POST',
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
  tagTypes: ['CourseDtos'],
  endpoints: (build) => ({
    // *******************
    // -------AUTH--------
    // *******************

    // LOGIN
    login: build.mutation({
      query: (credentials: UserLoginRequest) => ({
        url: 'public/login',
        method: 'POST',
        body: credentials,
      }),
    }),
    // Logout
    logout: build.mutation({
      query: () => ({
        url: 'public/logout',
        method: 'POST',
      }),
    }),
    // Register
    register: build.mutation({
      query: (credentials: UserRegisterRequest) => ({
        url: 'public/register',
        method: 'POST',
        body: credentials,
      }),
    }),
    // Confirm Email
    confirmEmail: build.mutation({
      query: (token: string) => ({
        url: `public/confirm-email`,
        method: 'POST',
        body: { token },
      }),
    }),
    // Resend Confirm Email Token
    resendConfirmEmailToken: build.mutation({
      query: () => ({
        url: `public/confirm-email/resend`,
        method: 'POST',
      }),
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
      providesTags: (result) => result?.content.map((course) => ({ type: 'CourseDtos', courseId: course.id })) || [],
    }),

    // NOT NEEDED PROB
    //  GET PUBLIC DTO COURSE DETAIL
    getCourseDetailPublic: build.query<CourseDetailsPublicDto, { courseId: string }>({
      query: ({ courseId }) => `public/courses${courseId}`,
      providesTags: (_result, _error, courseId) => [{ type: 'CourseDtos', courseId }],
    }),

    // *******************
    // --Private Courses--
    // *******************

    // *****************
    // -----Teacher-----
    // *****************
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
      providesTags: (result) => result?.content.map((course) => ({ type: 'CourseDtos', courseId: course.id })) || [],
    }),
  }),
});

export const {
  useGetCoursesPublicQuery,
  useLazyGetCoursesPublicQuery,
  useGetCourseDetailPublicQuery,
  useLoginMutation,
  useLogoutMutation,
  useRegisterMutation,
  useConfirmEmailMutation,
  useResendConfirmEmailTokenMutation,
  useLazyGetCoursesTeacherQuery,
} = api;
