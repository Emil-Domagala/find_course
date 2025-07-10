import { BaseQueryApi, createApi, FetchArgs, fetchBaseQuery } from '@reduxjs/toolkit/query/react';

const baseQuery = fetchBaseQuery({
  baseUrl: process.env.NEXT_PUBLIC_API_BASE_URL,
  credentials: 'include',
});
const baseQueryWithReauth: typeof baseQuery = async (args: string | FetchArgs, api: BaseQueryApi, extraOptions: object) => {
  let result = await baseQuery(args, api, extraOptions);

  if (typeof result?.error?.status === 'number' && [498, 499].includes(result?.error?.status)) {
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
      // console.info('should logout');
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
  tagTypes: ['CourseDtos', 'TeachedCourseDtos', 'User', 'Cart', 'TeacherApplicationNumber', 'UserCourseProgress', 'Chapter'],
  endpoints: (build) => ({
    refetchToken: build.mutation({
      query: () => ({
        url: 'public/refresh-token',
        method: 'POST',
      }),
    }),
    logout: build.mutation({
      query: () => ({ url: 'public/logout', method: 'POST' }),
    }),
  }),
});

export const { usePrefetch } = api;
