'use client';

import { Skeleton } from '@/components/ui/skeleton';
import { centsToDollars } from '@/lib/utils';
import { useGetCartQuery } from '@/features/nondashboard/user/cart/api';
import { useEffect } from 'react';
import { toast } from 'sonner';

export const ItemsListSkeleton = () => {
  return <Skeleton className="basic-1/2 flex-1" />;
};

const ItemsList = () => {
  const { data: res, isLoading } = useGetCartQuery();

  useEffect(() => {
    if (res?.warnings?.length) {
      res.warnings.forEach((msg) => toast.warning(msg));
    }
  }, [res?.warnings]);

  if (isLoading) return <ItemsListSkeleton />;

  if (res?.warnings) {
    toast.warning(res.warnings);
  }

  if (!res?.cart || !res.cart.courses || res.cart.courses.length === 0) {
    return <p className="text-center text-lg">Your cart is empty.</p>;
  }

  return (
    <div className="basic-1/2 flex-1">
      <div className="w-full bg-customgreys-secondarybg py-10 px-6 flex flex-col gap-5 rounded-lg">
        <h3 className="text-lg font-semibold mb-4">
          Price Details ({res.cart.courses.length} {res.cart.courses.length === 1 ? 'item' : 'items'})
        </h3>
        {res.cart.courses.map((course) => (
          <div key={course.id} className="flex justify-between mb-4 text-customgreys-dirtyGrey text-base">
            <span className="font-bold">{course.title}</span>
            <span className="font-bold">${centsToDollars(course.price)}</span>
          </div>
        ))}
        <div className="flex justify-between border-t border-customgreys-dirtyGrey pt-4">
          <span className="font-bold text-lg">Total Amount</span>
          <span className="font-bold text-lg">${centsToDollars(res.cart.totalPrice)}</span>
        </div>
      </div>
    </div>
  );
};

export default ItemsList;
