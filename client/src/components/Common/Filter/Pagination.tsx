import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';
import { ChevronLeft, ChevronRight } from 'lucide-react';
import { SetStateAction } from 'react';

type Props = {
  setPage: React.Dispatch<SetStateAction<number | undefined>>;
  currentPage: number;
  totalPages: number;
  className?: string;
};

const Pagination = ({ setPage, currentPage, totalPages = 0, className }: Props) => {
  return (
    <div data-testid="pagination-component" className={cn('flex flex-row gap-2 justify-center py-4', className)}>
      {currentPage != 0 && (
        <Button aria-label="Previous Page" size="sm" className="p-3 font-semibold" onClick={() => setPage((prev) => (prev || 1) - 1)}>
          <ChevronLeft className="h-4 w-4" />
          Previous
        </Button>
      )}
      {currentPage > 1 && (
        <>
          <Button aria-label="First Page" size="sm" className="p-3 font-semibold" onClick={() => setPage(0)}>
            First Page
          </Button>
          ...
        </>
      )}
      {/* Current Page */}
      <Button aria-label="Current Page" size="sm" className="p-3 font-semibold" variant="outline">
        {currentPage + 1}
      </Button>

      {currentPage < totalPages - 1 && (
        <>
          <span>...</span>
          <Button aria-label="Last Page" size="sm" className="p-3 font-semibold" onClick={() => setPage(totalPages - 1)}>
            {totalPages}
          </Button>
          <Button aria-label="Next Page" size="sm" className="p-3 font-semibold" onClick={() => setPage((prev) => (prev || 0) + 1)}>
            Next
            <ChevronRight className="h-4 w-4" />
          </Button>
        </>
      )}
    </div>
  );
};

export default Pagination;
