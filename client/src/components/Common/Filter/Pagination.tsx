import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';
import { ChevronLeft, ChevronRight } from 'lucide-react';
import { SetStateAction } from 'react';

type Props = {
  setPage: React.Dispatch<SetStateAction<number>>;
  currentPage: number;
  totalPages?: number;
  className?: string;
};

const Pagination = ({ setPage, currentPage, totalPages, className }: Props) => {
  return (
    <div className={cn('flex flex-row gap-2 justify-center py-4', className)}>
      {currentPage != 0 && (
        <Button onClick={() => setPage((prev) => prev - 1)}>
          <ChevronLeft className="h-4 w-4" />
          Previous
        </Button>
      )}
      {currentPage > 1 && (
        <>
          <Button onClick={() => setPage(0)}>First Page</Button>
          ...
        </>
      )}
      {/* Current Page */}
      <Button variant="outline">{currentPage + 1}</Button>
      {totalPages ? (
        currentPage < totalPages - 1 ? (
          <>
            <span>...</span>
            <Button onClick={() => setPage(totalPages - 1)}>{totalPages}</Button>
            <Button onClick={() => setPage((prev) => prev + 1)}>
              Next
              <ChevronRight className="h-4 w-4" />
            </Button>
          </>
        ) : (
          ''
        )
      ) : (
        ''
      )}
    </div>
  );
};

export default Pagination;
