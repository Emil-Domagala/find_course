import Footer from '@/components/Common/Footer/Footer';
import NonDashboardNav from '@/features/nondashboard/navigation/NonDashboardNav';

export default function NondashboardLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <div className="flex flex-col min-h-screen">
      <NonDashboardNav />
      <div className="flex-1 flex flex-col">{children}</div>
      <Footer />
    </div>
  );
}
