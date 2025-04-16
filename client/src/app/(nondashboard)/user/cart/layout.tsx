import CheckoutStepper from '@/components/NonDashboard/Cart/CheckoutStepper';

export default function CartLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <div className="w-full px-4 h-full flex flex-col items-center py-12">
      <CheckoutStepper />
      <div className="w-full max-w-screen-lg flex flex-col items-center mt-10">{children}</div>
    </div>
  );
}
