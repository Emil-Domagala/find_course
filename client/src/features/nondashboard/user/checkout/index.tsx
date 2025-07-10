import ItemsList from '@/features/nondashboard/user/checkout/components/ItemsList';
import CustomPaymentElement from '@/features/nondashboard/user/checkout/components/CustomPaymentElement';

const Checkout = () => {
  return (
    <div className="flex flex-col md:flex-row gap-10 w-full">
      <ItemsList />
      <CustomPaymentElement />
    </div>
  );
};

export default Checkout;
