import { api } from '../../api';
import { TransactionDto } from '@/types/payments';
import { SearchDirection } from '@/types/enums';

type PaginationProps = {
  page?: number;
  size?: number;
  sortField?: string | '';
  direction?: SearchDirection;
};

export const transactionApi = api.injectEndpoints({
  endpoints: (build) => ({
    getTransactions: build.query<Page<TransactionDto>, PaginationProps>({
      query: ({ page, size, sortField, direction }) => ({
        url: 'transaction',
        params: { page, size, sortField, direction },
        method: 'GET',
      }),
    }),
  }),
});

export const { useLazyGetTransactionsQuery } = transactionApi;
