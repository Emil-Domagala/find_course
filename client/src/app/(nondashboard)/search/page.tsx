'use client';

import CourseCardSearch, { CourseCardSearchSkeleton } from '@/components/NonDashboard/CourseCardSearch';
import { Button } from '@/components/ui/button';
import { useSearchFilters } from '@/hooks/useSearchFilters';
import { useGetCoursesPublicQuery } from '@/state/api';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@radix-ui/react-select';
import { ChevronLeft, ChevronRight } from 'lucide-react';

const SearchCoursesSkeleton = ({ size }: { size: number }) => {
  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      {[...Array(+size)].map((_, index) => (
        <CourseCardSearchSkeleton key={index} />
      ))}
    </div>
  );
};

const SearchPage = () => {
  const { size, setSize, page, setPage, sortField, setSortField, direction, setDirection, keyword, setKeyword } =
    useSearchFilters();

  const { data: coursesPage, isLoading } = useGetCoursesPublicQuery({ size, page, sortField, direction, keyword });
  const courses: CourseDto[] = coursesPage?.content ?? [];
  console.log(coursesPage);

  return (
    <div className="container">
      {/* Filter */}
      <></>
      {/* Courses */}
      <h1 className="font-normal text-2xl mt-14">List of available courses</h1>
      <h2 className="text-gray-500 mb-3">{coursesPage?.totalElements || 0} courses available</h2>
      {!isLoading ? (
        <div
          className={`grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 ${courses.length === 0 ? '!grid-cols-1' : ''}`}>
          {courses && courses.length > 0 ? (
            courses.map((course) => (
              <div key={course.id}>
                <CourseCardSearch course={course} />
              </div>
            ))
          ) : (
            <p className="mx-auto text-lg text-gray-400">There are no courses yet</p>
          )}
        </div>
      ) : (
        <SearchCoursesSkeleton size={size} />
      )}

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
        {/* Select page size */}
        <Select value={String(size)} onValueChange={(value) => setSize(Number(value))}>
          <SelectTrigger className="px-2 ml-5 min-w-10 bg-customgreys-secondarybg rounded-md">
            <SelectValue placeholder="Select page size">{String(size)}</SelectValue>
          </SelectTrigger>
          <SelectContent className="rounded-md overflow-hidden">
            {[12, 24, 48, 100].map((option) => (
              <SelectItem
                className="text-center bg-customgreys-secondarybg min-w-[100%] p-2 hover:bg-customgreys-darkGrey hover:!outline-none"
                key={option}
                value={String(option)}>
                {option}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>
      </div>
    </div>
  );
};

export default SearchPage;
