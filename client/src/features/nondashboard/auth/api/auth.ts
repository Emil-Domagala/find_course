import { UserLoginRequest, UserRegisterRequest } from '@/lib/validation/userAuth';

import { api } from '../../../../state/api';

export const authApi = api.injectEndpoints({
  endpoints: (build) => ({
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
    logout: build.mutation({
      query: () => ({ url: 'public/logout', method: 'POST' }),
    }),
    // Register
    register: build.mutation({
      query: (credentials: UserRegisterRequest) => ({ url: 'public/register', method: 'POST', body: credentials }),
    }),
  }),
  overrideExisting: false,
});
export const { useRefetchTokenMutation, useLoginMutation, useLogoutMutation, useRegisterMutation } = authApi;
