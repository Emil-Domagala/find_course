import BillingPage from './page';

const BillingPageLayout = () => {
  return (
    <div className="space-y-8">
      <div className="space-y-6 bg-customgreys-secondarybg">
        <h2 className="text-2xl font-semibold">Billing History</h2>
        <BillingPage />
      </div>
    </div>
  );
};

export default BillingPageLayout;
