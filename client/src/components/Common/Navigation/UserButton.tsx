'use client';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Popover } from '@/components/ui/popover';
import { AuthToken } from '@/types/accessToken';
import { PopoverContent, PopoverTrigger } from '@radix-ui/react-popover';
import LogoutButton from './LogoutButton';
import Link from 'next/link';
import { cn } from '@/lib/utils';
import { useEffect } from 'react';
import { Skeleton } from '@/components/ui/skeleton';
import { usePathname } from 'next/navigation';
import { useLogoutMutation, useRefetchTokenMutation } from '@/state/api';


type Props = {
  authToken?: AuthToken;
  className?: string;
  classNamePopover?: string;
};

const UserButton = ({ authToken, className, classNamePopover }: Props) => {
  const pathname = usePathname();
  const [refetchToken] = useRefetchTokenMutation();
  const [logoutUser] = useLogoutMutation();

  useEffect(() => {
    let isMounted = true; // Prevent execution on unmount

    const fetchToken = async () => {
      if (!authToken && isMounted) {
        try {
          await refetchToken({});
        } catch (e) {
          console.log(e);
          logoutUser({});
        }
      }
    };

    fetchToken();

    return () => {
      isMounted = false; // Cleanup
    };
  }, [authToken]);

  const viewCoursePageClass = pathname.toString().match('^/user/course/([0-9a-fA-F-]{36})/chapter/([0-9a-fA-F-]{36})$');

  const isSecondary = `${viewCoursePageClass && '!bg-customgreys-secondarybg'}`;

  return (
    <Popover>
      <PopoverTrigger
        className={cn(
          ' sm:pl-4 rounded-full bg-customgreys-secondarybg flex items-center gap-2 hover:bg-customgreys-darkerGrey transition-colors duration-300 group',
          classNamePopover,
          isSecondary,
        )}>
        <p className="text-customgreys-dirtyGrey group-hover:text-white-50 font-semibold hidden sm:block transition-colors duration-300">
          {authToken?.sub || 'User'}
        </p>
        <Avatar>
          <AvatarImage src={authToken?.picture || '/Profile_avatar_placeholder.png'} />
          <AvatarFallback>
            <Skeleton className="h-10 w-10 rounded-full bg-customgreys-darkerGrey"></Skeleton>
          </AvatarFallback>
        </Avatar>
      </PopoverTrigger>
      <PopoverContent className={cn('bg-customgreys-secondarybg min-w-32 rounded-lg overflow-hidden z-50', className)}>
        <Link
          href={'/user/courses'}
          className={cn(
            'text-md py-2 px-4 justify-center items-center flex rounded-none text-white-50 font-semibold  duration-300 transition-colors hover:bg-primary-600',
            className,
            isSecondary,
          )}>
          Courses
        </Link>
        <Link
          href={'/user/profile'}
          className={cn(
            'text-md py-2 px-4 justify-center items-center flex rounded-none text-white-50 font-semibold  duration-300 transition-colors hover:bg-primary-600',
            className,
            isSecondary,
          )}>
          Profile
        </Link>
        <Link
          href={'/user/cart'}
          className={cn(
            'text-md py-2 px-4 justify-center items-center flex rounded-none text-white-50 font-semibold  duration-300 transition-colors hover:bg-primary-600',
            className,
            isSecondary,
          )}>
          Cart
        </Link>
        <LogoutButton className={className} />
      </PopoverContent>
    </Popover>
  );
};

export default UserButton;
