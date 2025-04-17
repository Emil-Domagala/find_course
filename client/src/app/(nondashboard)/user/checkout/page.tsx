import ItemsList from '@/components/NonDashboard/Checkout/payment/ItemsList';
import CustomPaymentElement from '@/components/NonDashboard/Checkout/payment/CustomPaymentElement';

const CheckoutPage = () => {
  return (
    <div className="flex flex-col md:flex-row gap-10 w-full">
      <ItemsList />
      <CustomPaymentElement />
    </div>
  );
};

export default CheckoutPage;
