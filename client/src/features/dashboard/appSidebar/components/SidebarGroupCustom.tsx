'use client';
import { SidebarGroup, SidebarGroupContent, SidebarGroupLabel, SidebarMenu, SidebarMenuButton, SidebarMenuItem } from '@/components/ui/sidebar';
import { useGetNewTeacherApplicationNumberQuery } from '@/features/dashboard/admin/teacher-request/api';

import { LucideIcon } from 'lucide-react';

import Link from 'next/link';
import { usePathname } from 'next/navigation';

type Props = {
  links: { icon: LucideIcon; label: string; href: string; notification?: boolean }[];
  groupName?: string;
};

const SidebarGroupCustom = ({ links, groupName }: Props) => {
  const pathname = usePathname();

  const shouldFetchNotifications = groupName === 'Admin' && links.some((link) => link.notification);

  const {
    data: notificationData,
    isLoading,
    isError,
  } = useGetNewTeacherApplicationNumberQuery(undefined, {
    skip: !shouldFetchNotifications,
    refetchOnReconnect: true,
  });

  const notificationCount = notificationData?.newRequests ?? 0;

  return (
    <SidebarGroup className="p-0">
      {groupName && (
        <SidebarGroupLabel className="group-data-[collapsible=icon]:hidden">
          <p className="font-semibold !w-full !text-md text-white-50  pl-5 group-data-[collapsible=icon]:hidden">{groupName}</p>
        </SidebarGroupLabel>
      )}
      <SidebarGroupContent>
        <SidebarMenu className=" gap-0">
          {links.map((link) => {
            const isActive = pathname.startsWith(link.href);
            const showNotification = link.notification && notificationCount > 0 && !isLoading && !isError;
            return (
              <SidebarMenuItem
                key={link.label}
                className={`group-data-[collapsible=icon]:flex group-data-[collapsible=icon]:justify-center  ${isActive ? 'bg-gray-800' : ''}`}>
                <SidebarMenuButton
                  asChild
                  size={'lg'}
                  className={`gap-4 p-3 rounded-none hover:bg-customgreys-secondarybg group-data-[collapsible=icon]:flex group-data-[collapsible=icon]:justify-center transition-colors duration-300 ${
                    !isActive ? 'text-customgreys-dirtyGrey' : ''
                  }`}>
                  <Link href={link.href} className="flex items-center">
                    <link.icon className={!isActive ? 'text-white-50' : 'text-gray-500'} />
                    <span className={`font-medium text-md ml-4 group-data-[collapsible=icon]:hidden ${!isActive ? 'text-white-50' : ''}`}>{link.label}</span>
                  </Link>
                </SidebarMenuButton>
                {showNotification && (
                  <div className="absolute right-0 top-0 mr-4 h-full flex items-center justify-center ">
                    <p className="flex items-center justify-center px-1 min-w-7 min-h-7 rounded-full bg-primary-750 group-data-[collapsible=icon]:hidden text-white-50 ">
                      {notificationCount}
                    </p>
                  </div>
                )}

                {isActive && <div className="absolute right-0 top-0 h-full w-[4px] bg-primary-750" />}
              </SidebarMenuItem>
            );
          })}
        </SidebarMenu>
      </SidebarGroupContent>
    </SidebarGroup>
  );
};

export default SidebarGroupCustom;
