import DashboardNav from '@/features/dashboard/navigation/DashboardNav';
import ProgresSidebar from '@/features/dashboard/user/watchChapter/components/progres-sidebar/ProgresSidebar';

export default async function UserLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <>
      <ProgresSidebar />
      <div
        className={
          'flex flex-grow flex-col min-h-screen transition-all duration-500 ease-in-out overflow-y-auto bg-gradient-to-b from-customgreys-darkGrey via-customgreys-darkGrey to-customgreys-secondary'
        }>
        <DashboardNav />
        <main className={`px-8 py-4 flex-1`}>{children}</main>
      </div>
    </>
  );
}
