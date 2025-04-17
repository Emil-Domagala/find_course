'use client';

import { useCreateStripePaymentIntentMutation } from '@/state/api';
import { Appearance, loadStripe, StripeElementsOptions } from '@stripe/stripe-js';
import { Elements } from '@stripe/react-stripe-js';
import { useEffect, useState } from 'react';
import { Skeleton } from '@/components/ui/skeleton';
import { ApiErrorResponse } from '@/types/apiError';

if (!process.env.NEXT_PUBLIC_STRIPE_PUBLIC_KEY) {
  throw new Error('NEXT_PUBLIC_STRIPE_PUBLIC_KEY is not set');
}

const stripePromise = loadStripe(process.env.NEXT_PUBLIC_STRIPE_PUBLIC_KEY);

const appearance: Appearance = {
  theme: 'stripe',
  variables: {
    colorPrimary: '#0570de',
    colorBackground: '#18181b',
    colorText: '#d2d2d2',
    colorDanger: '#df1b41',
    colorTextPlaceholder: '#6e6e6e',
    fontFamily: 'Inter, system-ui, sans-serif',
    spacingUnit: '3px',
    borderRadius: '10px',
    fontSizeBase: '14px',
  },
};

const StripeProvider = ({ children }: { children: React.ReactNode }) => {
  const [message, setMessage] = useState('');
  const [isError, setIsError] = useState(false);
  const [clientSecret, setClientSecret] = useState<string | ''>('');
  const [createStripePaymentIntent] = useCreateStripePaymentIntentMutation();

  useEffect(() => {
    const fetchPaymentIntent = async () => {
      try {
        const result = await createStripePaymentIntent().unwrap();
        setClientSecret(result.clientSecret);
      } catch (e) {
        setIsError(true);
        const errorFull = e as ApiErrorResponse;
        const error = errorFull.data;
        if (!error.message) {
          setMessage('An unexpected error occurred.');
          return;
        }
        setMessage(error.message);
      }
    };

    fetchPaymentIntent();
  }, [createStripePaymentIntent]);

  const options: StripeElementsOptions = {
    clientSecret,
    appearance,
  };

  if (isError) return <p className="text-center py-4 font-semibold text-lg text-red-500">{message}</p>;
  if (!clientSecret) return <Skeleton className="m-4 w-[100% - 2rem] h-10" />;

  return (
    <Elements stripe={stripePromise} options={options} key={clientSecret}>
      {children}
    </Elements>
  );
};

export default StripeProvider;
