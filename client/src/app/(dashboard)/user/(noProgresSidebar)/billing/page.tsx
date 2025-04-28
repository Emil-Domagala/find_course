'use client';
import Pagination from '@/components/Common/Filter/Pagination';
import { Skeleton } from '@/components/ui/skeleton';
import { Table, TableBody, TableCell, TableFooter, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import { SearchDirection } from '@/types/enums';
import { centsToDollars } from '@/lib/utils';
import { useLazyGetTransactionsQuery } from '@/state/api';
import { useCallback, useEffect, useState } from 'react';

const BillingPage = ({}) => {
  const [page, setPage] = useState<number | undefined>(0);
  const [size, setSize] = useState(10);
  const [sortField, setSortField] = useState<string>('createdAt');
  const [direction, setDirection] = useState(SearchDirection.DESC);

  const handleChangeSortField = (field: string) => {
    if (sortField === field) {
      setDirection(direction === SearchDirection.ASC ? SearchDirection.DESC : SearchDirection.ASC);
    } else {
      setSortField(field);
      setDirection(SearchDirection.DESC);
    }
  };

  const [fetchCourses, { data: transactions, isLoading: isLoadingTransactions }] = useLazyGetTransactionsQuery();

  const handleFetchCourses = useCallback(
    () =>
      fetchCourses({
        page,
        size,
        sortField,
        direction,
      }),
    [fetchCourses, page, size, sortField, direction],
  );

  useEffect(() => {
    handleFetchCourses();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page, sortField, direction]);

  return (
    <div className="space-y-8">
      <div className="space-y-6 bg-customgreys-secondarybg">
        <h2 className="text-2xl font-semibold">Billing History</h2>

        {/* TABLE */}
        <div className="h-[400px] w-full">
          {isLoadingTransactions ? (
            <Skeleton className="h-[400px] w-full" />
          ) : (
            <Table className="text-customgreys-dirtyGrey min-h-[200px]">
              <TableHeader className="bg-customgreys-darkGrey">
                <TableRow className="border-none text-white-50">
                  <TableHead className="border-none p-4">Transaction Number</TableHead>
                  <TableHead className="border-none p-4 hover:bg-customgreys-secondarybg cursor-pointer" onClick={() => handleChangeSortField('createdAt')}>
                    Date
                  </TableHead>
                  <TableHead className="border-none p-4 hover:bg-customgreys-secondarybg cursor-pointer" onClick={() => handleChangeSortField('amount')}>
                    Amount
                  </TableHead>
                  <TableHead className="border-none p-4">Courses</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody className="bg-customgreys-primarybg min-h-[200px]">
                {transactions?.content?.length !== 0 ? (
                  transactions?.content?.map((transaction) => (
                    <TableRow key={transaction.id} className="border-none">
                      <TableCell className="border-none p-4">{transaction.paymentIntentId}</TableCell>
                      <TableCell className="border-none p-4">{new Date(transaction.createdAt).toLocaleDateString()}</TableCell>
                      <TableCell className="border-none p-4 font-medium">${centsToDollars(transaction.amount)}</TableCell>
                      <TableCell className="border-none p-4">{transaction.courses?.map((course) => course.title).join(', ')}</TableCell>
                    </TableRow>
                  ))
                ) : (
                  <TableRow>
                    <TableCell className="border-none p-4 text-center" colSpan={4}>
                      No Transactions Found
                    </TableCell>
                  </TableRow>
                )}
              </TableBody>

              <TableFooter className="bg-customgreys-primarybg border-customgreys-secondarybg">
                <TableRow>
                  <TableCell colSpan={4} className="p-0 m-0 border-none">
                    <Pagination className="p-3" setPage={setPage} currentPage={page || 0} totalPages={transactions?.totalPages} /> {/* Added padding back for aesthetics */}
                  </TableCell>
                </TableRow>
              </TableFooter>
            </Table>
          )}
        </div>
      </div>
    </div>
  );
};

export default BillingPage;
