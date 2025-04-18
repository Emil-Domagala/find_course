export type TransactionDto = {
  id: string;
  paymentIntentId: string;
  amount: number;
  createdAt: string;
  courses: CourseDto[];
};
