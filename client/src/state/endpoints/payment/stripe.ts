
import { StripeResponse } from '@/types/stripe';
import { api } from '../../api';

export const stripeApi = api.injectEndpoints({
  endpoints: (build) => ({
        createStripePaymentIntent: build.mutation<StripeResponse, void>({
          query: () => ({ url: 'transaction/stripe/create-payment-intent', method: 'POST' }),
        }),
 
  }),
});

export const { useCreateStripePaymentIntentMutation } = stripeApi;