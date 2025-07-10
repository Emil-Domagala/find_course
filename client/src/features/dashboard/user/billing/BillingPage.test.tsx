import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import Billing from '.';
import { useLazyGetTransactionsQuery } from './api';
import { createTransaction } from '@/__test__/factories/transactionFactory';
import { createPageResponse } from '@/__test__/factories/pageFactory';
import { TransactionDto } from '@/features/dashboard/user/billing/transaction';
import { createCourseDto } from '@/__test__/factories/courseFactory';

const fetchBillings = jest.fn();

jest.mock('./api', () => ({
  useLazyGetTransactionsQuery: jest.fn(),
}));

beforeEach(() => {
  (useLazyGetTransactionsQuery as jest.Mock).mockReturnValue([fetchBillings, { data: [], isLoading: false }]);
});

afterEach(() => {
  jest.clearAllMocks();
});

describe('BillingPage', () => {
  describe('Renders', () => {
    test('Renders transaction', () => {
      const tran = createTransaction();
      const tran2 = createTransaction({ courses: [createCourseDto(), createCourseDto()] });
      const page = createPageResponse<TransactionDto>({ content: [tran, tran2] });
      (useLazyGetTransactionsQuery as jest.Mock).mockReturnValue([fetchBillings, { data: page, isLoading: false }]);
      render(<Billing />);
      expect(screen.getByText(tran.paymentIntentId)).toBeInTheDocument();
      expect(screen.getByText(tran.courses.map((course) => course.title).join(', '))).toBeInTheDocument();

      //
      expect(screen.getByText(tran2.paymentIntentId)).toBeInTheDocument();
      expect(screen.getByText(tran2.courses.map((course) => course.title).join(', '))).toBeInTheDocument();
    });
  });

  describe('Api Interaction', () => {
    test('On page load fetch courses', async () => {
      render(<Billing />);
      await waitFor(() => {
        expect(fetchBillings).toHaveBeenCalled();
      });
    });

    test('On sortField fetch transactions', async () => {
      const page = createPageResponse<TransactionDto>({ content: [createTransaction(), createTransaction()] });
      (useLazyGetTransactionsQuery as jest.Mock).mockReturnValue([fetchBillings, { data: page, isLoading: false }]);
      render(<Billing />);
      const user = userEvent.setup();
      const sortSelect = screen.getByLabelText('change sort order by date');
      await user.click(sortSelect);
      await waitFor(() => {
        expect(fetchBillings).toHaveBeenCalledWith({
          page: 0,
          size: 10,
          sortField: 'createdAt',
          direction: 'DESC',
        });
      });
      await user.click(sortSelect);
      await waitFor(() => {
        expect(fetchBillings).toHaveBeenCalledWith({
          page: 0,
          size: 10,
          sortField: 'createdAt',
          direction: 'ASC',
        });
      });
    });

    test('On  direction fetch transactions', async () => {
      const page = createPageResponse<TransactionDto>({ content: [createTransaction(), createTransaction()] });
      (useLazyGetTransactionsQuery as jest.Mock).mockReturnValue([fetchBillings, { data: page, isLoading: false }]);
      render(<Billing />);
      const user = userEvent.setup();
      const sortSelect = screen.getByLabelText('change sort order by amount');
      await user.click(sortSelect);
      await waitFor(() => {
        expect(fetchBillings).toHaveBeenCalledWith({
          page: 0,
          size: 10,
          sortField: 'amount',
          direction: 'DESC',
        });
      });
      await user.click(sortSelect);
      await waitFor(() => {
        expect(fetchBillings).toHaveBeenCalledWith({
          page: 0,
          size: 10,
          sortField: 'amount',
          direction: 'ASC',
        });
      });
    });

    test('On page change calls api again', async () => {
      const page = createPageResponse<TransactionDto>({
        totalPages: 5,
        size: 2,
        totalElements: 10,
        page: 1,
        content: [createTransaction(), createTransaction()],
      });
      (useLazyGetTransactionsQuery as jest.Mock).mockReturnValue([fetchBillings, { data: page, isLoading: false }]);
      render(<Billing />);
      const pageNextButton = screen.getByRole('button', { name: /Next/i });
      const user = userEvent.setup();
      await user.click(pageNextButton);
      await waitFor(() => {
        expect(fetchBillings).toHaveBeenCalledTimes(2);
      });
    });
  });
});
