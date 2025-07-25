'use client';

import { Sidebar, SidebarContent, SidebarFooter, SidebarHeader, SidebarMenu, SidebarMenuButton, SidebarMenuItem, useSidebar } from '@/components/ui/sidebar';
import { BookOpen, LogOut, NotebookPen, PanelLeft, Receipt, ShoppingCart, User, UserCheck } from 'lucide-react';
import Image from 'next/image';
import SidebarGroupCustom from './components/SidebarGroupCustom';
import { jwtDecode } from 'jwt-decode';
import { AuthToken } from '@/types/accessToken';
import { useRouter } from 'next/navigation';
import { useLogoutMutation } from '@/state/api';

type Props = { authToken?: string };

const navLinks = {
  user: [
    { icon: BookOpen, label: 'Courses', href: '/user/courses' },
    { icon: User, label: 'Profile', href: '/user/profile' },
    { icon: Receipt, label: 'Billing', href: '/user/billing' },
    { icon: ShoppingCart, label: 'Cart', href: '/user/cart' },
  ],
  teacher: [{ icon: NotebookPen, label: 'My courses', href: '/teacher/courses/my-courses' }],
  admin: [{ icon: UserCheck, label: 'New Teachers', href: '/admin/teacher-requests', notification: true }],
};

const AppSidebar = ({ authToken }: Props) => {
  const router = useRouter();
  const [logoutUser] = useLogoutMutation();
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
    <Sidebar collapsible="icon" side="left" variant="sidebar" className="bg-customgreys-primarybg border-none shadow-lg">
      <SidebarHeader className="p-0">
        <SidebarMenu className="mt-5 mb-7">
          <SidebarMenuItem>
            <SidebarMenuButton onClick={() => toggleSidebar()} className="group hover:bg-customgreys-secondarybg h-auto">
              <div className="flex justify-between items-center gap-5 py-2 pl-3 pr-1 min-h-10 w-full group-data-[collapsible=icon]:ml-1 group-data-[collapsible=icon]:w-10 group-data-[collapsible=icon]:px-0">
                <div className=" flex items-center gap-5">
                  <Image
                    src={'/logo.svg'}
                    alt="Logo"
                    width={25}
                    height={20}
                    className="transition duration-200 group-data-[collapsible=icon]:group-hover:brightness-75 w-auto"
                  />
                  <p className="text-lg font-extrabold text-nowrap overflow-hidden group-data-[collapsible=icon]:hidden">Find Course</p>
                </div>
                <PanelLeft className="text-gray-400 w-5 h-5 group-data-[collapsible=icon]:hidden" />
              </div>
            </SidebarMenuButton>
          </SidebarMenuItem>
        </SidebarMenu>
      </SidebarHeader>

      <SidebarContent>
        <SidebarGroupCustom links={navLinks.user} />
        {decoded.roles.includes('TEACHER') && <SidebarGroupCustom links={navLinks.teacher} groupName="Teacher" />}
        {decoded.roles.includes('ADMIN') && <SidebarGroupCustom links={navLinks.admin} groupName="Admin" />}
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
