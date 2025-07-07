import { BaseQueryApi, createApi, FetchArgs, fetchBaseQuery } from '@reduxjs/toolkit/query/react';

const baseQuery = fetchBaseQuery({
  baseUrl: process.env.NEXT_PUBLIC_API_BASE_URL,
  credentials: 'include',
});
const baseQueryWithReauth: typeof baseQuery = async (args: string | FetchArgs, api: BaseQueryApi, extraOptions: unknown) => {
  let result = await baseQuery(args, api, extraOptions);
  console.info('baseQueryWithReauth');
  console.info(result);
  if (typeof result?.error?.status === 'number' && [498, 499].includes(result?.error?.status)) {
    console.info('baseQueryWithReauth inside if');
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
  endpoints: () => ({}),
});

export const { usePrefetch } = api;
