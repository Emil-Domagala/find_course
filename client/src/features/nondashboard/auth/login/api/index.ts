import { UserLoginRequest } from '@/lib/validation/userAuth';

import { api } from '@/state/api';

export const loginApi = api.injectEndpoints({
  endpoints: (build) => ({
    // LOGIN
    login: build.mutation({
      query: (credentials: UserLoginRequest) => ({ url: 'public/login', method: 'POST', body: credentials }),
    }),
  }),
  overrideExisting: false,
});
export const { useLoginMutation } = loginApi;
