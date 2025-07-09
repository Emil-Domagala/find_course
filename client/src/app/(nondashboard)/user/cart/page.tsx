'use client';

import CartItem from '@/components/NonDashboard/cart/CartItem';
import { Button } from '@/components/ui/button';
import { centsToDollars } from '@/lib/utils';
import { useRouter } from 'next/navigation';
import LoadingSpinner from '@/components/Common/LoadingSpinner';
import { useGetCartQuery } from '@/state/endpoints/cart/cart';
import { toast } from 'sonner';
import { useEffect } from 'react';

const CartPage = () => {
  const router = useRouter();
  const { data: response, isLoading } = useGetCartQuery();

  useEffect(() => {
    if (response?.warnings?.length) {
      response.warnings.forEach((msg) => toast.warning(msg));
    }
  }, [response?.warnings]);

  if (isLoading) {
    return <LoadingSpinner />;
  }

  if (!response?.cart || !response.cart.courses || response.cart.courses.length === 0) {
    return <p className="text-center text-lg">Your cart is empty.</p>;
  }

  return (
    <>
      <div className="flex flex-col">
        <ul className="items">
          {response.cart.courses.map((course) => (
            <CartItem key={course.id} course={course} />
          ))}
        </ul>
      </div>
      <div className="flex flex-col gap-5 w-fit min-w-[15rem] mx-auto md:mx-0 md:ml-auto mt-5">
        <div className="flex gap-5 justify-between w-full">
          <div>
            <p className="text-lg font-semibold">Total</p>
            <p className="text-sm text-customgreys-dirtyGrey">Items: {response.cart.courses.length}</p>
          </div>
          <p className="text-primary-500 font-semibold text-xl">${centsToDollars(response.cart.totalPrice)}</p>
        </div>
        <Button variant="primary" onClick={() => router.push('/user/checkout')}>
          Checkout
        </Button>
      </div>
    </>
  );
};
export default CartPage;
