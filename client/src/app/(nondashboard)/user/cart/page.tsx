'use client';

import { useGetCartQuery } from '@/state/api';
import CartItem from './CartItem';
import { Button } from '@/components/ui/button';
import { centsToDollars } from '@/lib/utils';
import { useRouter } from 'next/navigation';
import LoadingSpinner from '@/components/Common/LoadingSpinner';

const CartPage = () => {
  const router = useRouter();
  const { data: cart, isLoading } = useGetCartQuery();

  return (
    <>
      {isLoading ? (
        <LoadingSpinner />
      ) : !cart || cart?.courses?.length === 0 ? (
        <p className="text-center text-lg">Your cart is empty.</p>
      ) : (
        <>
          <div className="flex flex-col">
            <ul className="items">{cart?.courses?.map((course) => <CartItem key={course.id} course={course} />)}</ul>
          </div>
          {/* Checkout */}
          <div className="flex flex-col gap-5 w-fit min-w-[15rem] mx-auto md:mx-0 md:ml-auto mt-5">
            <div className="flex gap-5 justify-between w-full">
              <div className="">
                <p className="text-lg font-semibold">Total</p>
                <p className="text-sm text-customgreys-dirtyGrey">Items: {cart?.courses.length}</p>
              </div>
              <p className="text-primary-500 font-semibold text-xl">${centsToDollars(cart?.totalPrice)}</p>
            </div>
            <Button variant="primary" onClick={() => router.push('/user/checkout')}>
              Checkout
            </Button>
          </div>
        </>
      )}
    </>
  );
};

export default CartPage;
