import DashboardNav from '@/components/Dashboard/Navigation/DashboardNav';
import ProgresSidebar from '../../../../components/Dashboard/User/ProgresSidebar/ProgresSidebar';

export default async function UserLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <>
      <ProgresSidebar />
      <div className={'flex flex-grow flex-col min-h-screen transition-all duration-500 ease-in-out overflow-y-auto bg-customgreys-secondarybg'}>
        <DashboardNav />
        <main className={`px-8 py-4 flex-1`}>{children}</main>
      </div>
    </>
  );
}
