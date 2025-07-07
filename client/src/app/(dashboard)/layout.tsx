import AppSidebar from '@/components/Dashboard/AppSidebar/AppSidebar';
import { SidebarProvider } from '@/components/ui/sidebar';
import { cookies } from 'next/headers';

export default async function DashboardLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  const cookieStore = await cookies();
  const defaultOpen = cookieStore.get('sidebar:state')?.value === 'true';

  const authToken = cookieStore.get(process.env.ACCESS_COOKIE_NAME!)?.value;

  return (
    <SidebarProvider defaultOpen={defaultOpen}>
      <div className="min-h-screen w-full bg-customgreys-primarybg flex">
        <AppSidebar authToken={authToken} />
        <div className="flex flex-1 overflow-hidden">{children}</div>
      </div>
    </SidebarProvider>
  );
}
