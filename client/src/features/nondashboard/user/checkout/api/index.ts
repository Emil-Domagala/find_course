import { StripeResponse } from '@/features/nondashboard/user/checkout/stripe';
import { api } from '@/state/api';

export const stripeApi = api.injectEndpoints({
  endpoints: (build) => ({
    createStripePaymentIntent: build.mutation<StripeResponse, void>({
      query: () => ({ url: 'transaction/stripe/create-payment-intent', method: 'POST' }),
    }),
  }),
});

export const { useCreateStripePaymentIntentMutation } = stripeApi;
