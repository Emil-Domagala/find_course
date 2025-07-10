import { api } from '../../../../../state/api';
import { TransactionDto } from '@/features/dashboard/user/billing/transaction';
import { SearchDirection } from '@/types/search-enums';
import { TransactionDtoSortField } from '../transactionDtoSortField';

type PaginationProps = {
  page?: number;
  size?: number;
  sortField?: TransactionDtoSortField | '';
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
