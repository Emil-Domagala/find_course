'use client';

import { Button } from '@/components/ui/button';
import { PaymentElement, useElements, useStripe } from '@stripe/react-stripe-js';
import { toast } from 'sonner';
import StripeProvider from './StripeProvider';
import { useRouter } from 'next/navigation';

const PaymentFormContent = () => {
  const router = useRouter();
  const stripe = useStripe();
  const elements = useElements();
  router.prefetch('/user/checkout/success');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!stripe || !elements) {
      toast.error('Stripe service is not available');
      return;
    }

    const baseUrl = process.env.NEXT_PUBLIC_DOMAIN_NAME;

    const result = await stripe.confirmPayment({
      elements,
      confirmParams: {
        return_url: `${baseUrl}/user/checkout/success`,
      },
      redirect: 'if_required',
    });

    if (result.paymentIntent?.status === 'succeeded') {
      router.replace('/user/checkout/success');
    }
  };

  return (
    <>
      <p className="text-red-500 text-center mt-5 font-semibold">DO NOT PROVIDE YOUR CORRECT CARD DATA.</p>
      <p className="text-red-500 text-center ">Use 4242 4242 4242 4242 as the card number</p>
      <form onSubmit={handleSubmit} className="mb-5">
        <div className="px-4 py-5">
          <PaymentElement />
        </div>
        <div className="w-[50%] m-auto">
          <Button variant="primary" type="submit" className="w-full">
            Pay
          </Button>
        </div>
      </form>
    </>
  );
};

const PaymentForm = () => {
  return (
    <StripeProvider>
      <PaymentFormContent />
    </StripeProvider>
  );
};

export default PaymentForm;
