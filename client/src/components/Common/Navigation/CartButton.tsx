'use client';
import { cn } from '@/lib/utils';
import { ShoppingCart } from 'lucide-react';
import Link from 'next/link';
import { usePathname } from 'next/navigation';

const CartButton = ({ className, authToken }: { className?: string; authToken?: string }) => {
  const pathname = usePathname();

  const viewCoursePageClass = pathname.toString().match('^/user/course/([0-9a-fA-F-]{36})/chapter/([0-9a-fA-F-]{36})$');

  const isSecondary = `${viewCoursePageClass && '!bg-customgreys-secondarybg'}`;

  return (
    <>
      {authToken && (
        <Link
          href={'/user/cart'}
          className={cn(
            'relative  bg-customgreys-secondarybg h-10 min-w-10 px-3 rounded-full flex justify-center items-center cursor-pointer text-customgreys-dirtyGrey hover:text-white-50 transition-colors duration-300',
            className,
            isSecondary,
          )}>
          {/* <p className="pr-1">10</p> */}
          <ShoppingCart className="size-4 " />
        </Link>
      )}
    </>
  );
};

export default CartButton;
