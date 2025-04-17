export type Transaction = {
  userId: string;
  transactionId: string;
  dateTime: string;
  courseId: string;
  paymentProvider: 'stripe';
  paymentMethodId?: string;
  amount: number; // Stored in cents
  savePaymentMethod?: boolean;
};
