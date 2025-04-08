'use client';

import { useLogoutMutation } from '@/state/api';
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  useSidebar,
} from '@/components/ui/sidebar';
import { BookOpen, LogOut, NotebookPen, PanelLeft, Settings, User, UserRoundPlus } from 'lucide-react';
import Image from 'next/image';
import SidebarGroupCustom from './SidebarGroupCustom';
import { jwtDecode } from 'jwt-decode';
import { AuthToken } from '@/types/auth';
import { useRouter } from 'next/navigation';

type Props = { authToken?: string };

const navLinks = {
  user: [
    { icon: BookOpen, label: 'Courses', href: '/dashboard/user/courses' },
    { icon: User, label: 'Profile', href: '/dashboard/user/profile' },
    { icon: Settings, label: 'Settings', href: '/dashboard/user/settings' },
    { icon: BookOpen, label: 'Become Teacher', href: '/dashboard/user/become-teacher' },
  ],
  teacher: [{ icon: NotebookPen, label: 'My courses', href: '/dashboard/teacher/courses' }],
  admin: [{ icon: UserRoundPlus, label: 'New Teachers', href: '/dashboard/admin/teacher-requests' }],
};

const AppSidebar = ({ authToken }: Props) => {
  const router = useRouter();
  const [logoutUser, { isLoading }] = useLogoutMutation();
  const { toggleSidebar } = useSidebar();

  const handleLogout = async () => {
    await logoutUser({});
    router.push('/');
    router.refresh();
  };

  if (!authToken) {
    handleLogout();
    return null;
  }

  const decoded = jwtDecode(authToken) as AuthToken;

  return (
    <Sidebar
      collapsible="icon"
      side="left"
      variant="sidebar"
      className="bg-customgreys-primarybg border-none shadow-lg">
      <SidebarHeader className="p-0">
        <SidebarMenu className="mt-5 mb-7">
          <SidebarMenuItem>
            <SidebarMenuButton
              onClick={() => toggleSidebar()}
              className="group hover:bg-customgreys-secondarybg h-auto">
              <div className="flex justify-between items-center gap-5 py-2 pl-3 pr-1 min-h-10 w-full group-data-[collapsible=icon]:ml-1 group-data-[collapsible=icon]:w-10 group-data-[collapsible=icon]:px-0">
                <div className=" flex items-center gap-5">
                  <Image
                    src={'/logo.svg'}
                    alt="Logo"
                    width={25}
                    height={20}
                    className="transition duration-200 group-data-[collapsible=icon]:group-hover:brightness-75 w-auto"
                  />
                  <p className="text-lg font-extrabold text-nowrap overflow-hidden group-data-[collapsible=icon]:hidden">
                    Find Course
                  </p>
                </div>
                <PanelLeft className="text-gray-400 w-5 h-5 group-data-[collapsible=icon]:hidden" />
              </div>
            </SidebarMenuButton>
          </SidebarMenuItem>
        </SidebarMenu>
      </SidebarHeader>

      <SidebarContent>
        <SidebarGroupCustom links={navLinks.user} />
        {decoded.roles.includes('USER') && <SidebarGroupCustom links={navLinks.teacher} label="Teacher" />}
        {decoded.roles.includes('USER') && <SidebarGroupCustom links={navLinks.admin} label="Admin" />}
      </SidebarContent>

      <SidebarFooter className="p-0">
        <SidebarMenu>
          <SidebarMenuItem>
            <button
              onClick={() => handleLogout()}
              className="text-primary-700 flex w-full items-center justify-center px-4 py-4 pb-6 group-data-[collapsible=icon]:px-0 hover:bg-customgreys-secondarybg ">
              <LogOut className="mr-2 size-6 group-data-[collapsible=icon]:mr-0" />
              <span className="group-data-[collapsible=icon]:hidden">Logout</span>
            </button>
          </SidebarMenuItem>
        </SidebarMenu>
      </SidebarFooter>
    </Sidebar>
  );
};

export default AppSidebar;
