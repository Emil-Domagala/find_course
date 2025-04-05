import { Button } from '@/components/ui/button';
import { ChevronLeft, ChevronRight } from 'lucide-react';
import { SetStateAction } from 'react';

type Props = { setPage: React.Dispatch<SetStateAction<number>>; page: number; coursesPage?: Page<CourseDto> };

const Pagination = ({ setPage, page, coursesPage }: Props) => {
  return (
    <div className="flex flex-row gap-2 justify-center">
      {page != 0 && (
        <Button onClick={() => setPage((prev) => prev - 1)}>
          <ChevronLeft className="h-4 w-4" />
          Previous
        </Button>
      )}
      {page > 1 && (
        <>
          <Button onClick={() => setPage(0)}>First Page</Button>
          ...
        </>
      )}
      {/* Current Page */}
      <Button variant="outline">{page + 1}</Button>
      {coursesPage?.totalPages && page < coursesPage?.totalPages - 1 && (
        <>
          <span>...</span>
          <Button onClick={() => setPage(coursesPage?.totalPages - 1)}>{coursesPage?.totalPages}</Button>
          <Button onClick={() => setPage((prev) => prev + 1)}>
            Next
            <ChevronRight className="h-4 w-4" />
          </Button>
        </>
      )}
    </div>
  );
};

export default Pagination;
