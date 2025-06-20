'use client';

import { Skeleton } from '@/components/ui/skeleton';
import { centsToDollars } from '@/lib/utils';
import { useGetCartQuery } from '@/state/endpoints/cart/cart';
import { toast } from 'sonner';

export const ItemsListSkeleton = () => {
  return <Skeleton className="basic-1/2 flex-1" />;
};

const ItemsList = () => {
  const { data: res } = useGetCartQuery();

  if (!res) return <ItemsListSkeleton />;
  if(res.warnings){
    toast.warning(res.warnings)
  }

  return (
    <div className="basic-1/2 flex-1">
      <div className="w-full bg-customgreys-secondarybg py-10 px-6 flex flex-col gap-5 rounded-lg">
        <h3 className="text-lg font-semibold mb-4">
          Price Details ({res.cart.courses.length} {res.cart.courses.length === 1 ? 'item' : 'items'})
        </h3>
        {res.cart.courses.map((course) => (
          <div key={course.id} className="flex justify-between mb-4 text-customgreys-dirtyGrey text-base">
            <span className="font-bold">1x {course.title}</span>
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
