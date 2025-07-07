const BillingPageLayout = ({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) => {
  return (
    <div className="space-y-8">
      <div className="space-y-6 bg-customgreys-secondarybg">
        <h2 className="text-2xl font-semibold">Billing History</h2>
        {children}
      </div>
    </div>
  );
};

export default BillingPageLayout;
