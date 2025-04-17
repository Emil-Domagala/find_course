import CheckoutStepper from '@/components/NonDashboard/Checkout/CheckoutStepper';

export default function CartLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <div className="w-full px-4 h-full flex flex-col items-center mt-5 mb-10">
      <CheckoutStepper />
      <div className="w-full max-w-screen-lg">{children}</div>
    </div>
  );
}
