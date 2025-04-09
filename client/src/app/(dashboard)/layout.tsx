import AppSidebar from '@/components/Dashboard/AppSidebar/AppSidebar';
import DashboardNav from '@/components/Dashboard/Navigation/DashboardNav';
import { SidebarInset, SidebarProvider, SidebarTrigger } from '@/components/ui/sidebar';
import { cookies } from 'next/headers';

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
        <div className="flex flex-1 overflow-hidden">
          <div
            className={
              ' flex-grow flex-col min-h-screen transition-all duration-500 ease-in-out overflow-y-auto bg-customgreys-secondarybg'
            }>
            <DashboardNav />
            <main className={`px-8 py-4`}>{children}</main>
          </div>
        </div>
      </div>
    </SidebarProvider>
  );
}
