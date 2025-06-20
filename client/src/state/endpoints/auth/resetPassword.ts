import { ForgotPasswordRequest } from '@/lib/validation/userAuth';
import { api } from '../../api';

export const resetPasswordApi = api.injectEndpoints({
  endpoints: (build) => ({
    //send Reset password email
    sendResetPasswordEmail: build.mutation<string, ForgotPasswordRequest>({
      query: ({ email }) => ({ url: `public/forgot-password`, method: 'POST', body: { email } }),
    }),
    // Reset password
    resetPassword: build.mutation<void, { token: string; password: string }>({
      query: ({ token, password }) => ({
        url: `public/reset-password`,
        params: { token },
        method: 'POST',
        body: { password },
      }),
    }),
  }),
});

export const { useSendResetPasswordEmailMutation, useResetPasswordMutation } = resetPasswordApi;
