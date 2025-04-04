import Footer from '@/components/NonDashboard/Footer/Footer';
import NonDashboardNav from '@/components/NonDashboard/Navigation/NonDashboardNav';

export default function NondashboardLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {

  
  return (
    <div className="flex flex-col min-h-screen">
      <NonDashboardNav />
      <div className="flex-1">{children}</div>
      <Footer />
    </div>
  );
}
