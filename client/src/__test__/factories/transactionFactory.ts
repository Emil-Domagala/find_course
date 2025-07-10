import { TransactionDto } from '@/features/dashboard/user/billing/transaction';
import { createCourseDto } from './courseFactory';
import { v4 as uuid } from 'uuid';

export const createTransaction = (args: Partial<TransactionDto> = {}) => {
  const courses = args.courses ?? [createCourseDto()];
  const amount = args.amount ?? courses.reduce((acc, course) => acc + course.price, 0);

  return {
    id: args.id ?? uuid(),
    paymentIntentId: args.paymentIntentId ?? uuid().substring(0, 10),
    createdAt: args.createdAt ?? new Date().toISOString(),
    courses,
    amount,
  };
};
