import { UserRegisterRequest } from '@/lib/validation/userAuth';

import { api } from '@/state/api';

export const registerUserApi = api.injectEndpoints({
  endpoints: (build) => ({
    // Register
    register: build.mutation({
      query: (credentials: UserRegisterRequest) => ({ url: 'public/register', method: 'POST', body: credentials }),
    }),
  }),
  overrideExisting: false,
});
export const { useRegisterMutation } = registerUserApi;
