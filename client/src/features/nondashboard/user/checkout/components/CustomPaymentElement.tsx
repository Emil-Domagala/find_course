import { CreditCard } from 'lucide-react';
import PaymentForm from './PaymentForm';

const CustomPaymentElement = () => {
  return (
    <div className="basis-1/2">
      <div className="flex flex-col gap-4 bg-customgreys-secondarybg px-6 py-10 rounded-lg">
        <div id="payment-form" className="space-y-4">
          <h1 className="text-2xl font-bold">Checkout</h1>
          <p className="text-sm text-gray-400">Fill out the payment details below to complete your purchase.</p>
          <div className="flex flex-col gap-2 w-full mt-6">
            <h3 className="text-md">Payment Method</h3>
            <div className="flex flex-col border-[2px] border-white-100/5 rounded-lg">
              <div className="flex items-center gap-2 bg-white-50/5 py-2 px-2">
                <CreditCard size={24} />
                <span>Credit/Debit Card</span>
              </div>
              <PaymentForm />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default CustomPaymentElement;
