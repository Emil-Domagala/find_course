import AppSidebar from '@/components/Dashboard/AppSidebar/AppSidebar';
import DashboardNav from '@/components/Dashboard/Navigation/DashboardNav';
import { SidebarProvider } from '@/components/ui/sidebar';
import { cookies } from 'next/headers';
import ProgresSidebar from '../../components/Dashboard/User/Course/ProgresSidebar/ProgresSidebar';

export default async function DashboardLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  const cookieStore = await cookies();
  const defaultOpen = cookieStore.get('sidebar:state')?.value === 'true';

  const authToken = cookieStore.get(process.env.AUTH_COOKIE_NAME!)?.value;

  return (
    <SidebarProvider defaultOpen={defaultOpen}>
      <div className="min-h-screen w-full bg-customgreys-primarybg flex">
        <AppSidebar authToken={authToken} />
        <div className="flex flex-1 overflow-hidden">{children}</div>
      </div>
    </SidebarProvider>
  );
}
