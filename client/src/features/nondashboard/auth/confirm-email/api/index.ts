import { api } from '@/state/api';

export const confirmEmailApi = api.injectEndpoints({
  endpoints: (build) => ({
    // Confirm Email
    confirmEmail: build.mutation({
      query: (token: string) => ({ url: `confirm-email`, method: 'POST', body: { token } }),
    }),
    // Resend Confirm Email Token
    resendConfirmEmailToken: build.mutation({
      query: () => ({ url: `confirm-email/resend`, method: 'POST' }),
    }),
  }),
});

export const { useConfirmEmailMutation, useResendConfirmEmailTokenMutation } = confirmEmailApi;