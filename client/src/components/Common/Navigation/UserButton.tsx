'use client';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';

import { Popover } from '@/components/ui/popover';
import { AuthToken } from '@/types/auth';
import { PopoverContent, PopoverTrigger } from '@radix-ui/react-popover';
import LogoutButton from './LogoutButton';
import Link from 'next/link';
import { cn } from '@/lib/utils';
import { useEffect } from 'react';
import { useRefetchTokenMutation } from '@/state/api';
import { Skeleton } from '@/components/ui/skeleton';

type Props = {
  authToken?: AuthToken;
  className?: string;
  classNamePopover?: string;
};

const UserButton = ({ authToken, className, classNamePopover }: Props) => {
  const [refetchToken] = useRefetchTokenMutation();

  useEffect(() => {
    if (!!authToken) {
      console.log('Refetching token... in UserButton.tsx');
      refetchToken({});
    }
  }, []);

  return (
    <Popover>
      <PopoverTrigger
        className={cn(
          'pl-4 rounded-full bg-customgreys-secondarybg flex items-center gap-2 hover:bg-customgreys-secondarybg/50 transition-colors duration-300 group',
          classNamePopover,
        )}>
        <p className="text-white-50 font-semibold">{authToken?.sub || 'User'}</p>
        <Avatar>
          <AvatarImage
            src={authToken?.picture || '/placeholder.png'}
            className="group-hover:opacity-50 transition-opacity duration-300"
          />
          <AvatarFallback>
            <Skeleton className="h-10 w-10 rounded-full bg-customgreys-darkerGrey"></Skeleton>
          </AvatarFallback>
        </Avatar>
      </PopoverTrigger>
      <PopoverContent className={cn('bg-customgreys-secondarybg min-w-32 rounded-lg overflow-hidden', className)}>
        <Link
          href={'user/profile'}
          className={cn(
            'text-md py-2 px-4 justify-center items-center flex rounded-none text-white-50 font-semibold  duration-300 transition-colors hover:bg-primary-600',
            className,
          )}>
          Profile
        </Link>
        <LogoutButton className={className} />
      </PopoverContent>
    </Popover>
  );
};

export default UserButton;
