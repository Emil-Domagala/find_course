'use client';

import DisplayCourses from '@/components/NonDashboard/Search/DisplayCourses';
import Pagination from '@/components/Common/Filter/Pagination';
import { useSearchFilters } from '@/hooks/useSearchFilters';
import { useLazyGetCoursesPublicQuery } from '@/state/api';
import { useEffect } from 'react';
import Filter from '@/components/Common/Filter/Filter';

const SearchPage = () => {
  const {
    size,
    setSize,
    page,
    setPage,
    sortField,
    setSortField,
    direction,
    setDirection,
    keyword,
    setKeyword,
    category,
    setCategory,
  } = useSearchFilters();

  const [fetchCourses, { data: coursesPage, isLoading }] = useLazyGetCoursesPublicQuery();

  const handleFetchCourses = () => {
    return fetchCourses({ page, size, sortField, direction, keyword, category });
  };
  useEffect(() => {
    handleFetchCourses();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page]);

  return (
    <>
      <div className="w-full bg-customgreys-secondarybg py-5">
        <div className="container max-w-[85rem] ">
          <Filter
            category={category}
            setCategory={setCategory}
            keyword={keyword}
            setKeyword={setKeyword}
            sortField={sortField}
            setSortField={setSortField}
            direction={direction}
            setDirection={setDirection}
            size={size}
            setSize={setSize}
            handleFetchCourses={handleFetchCourses}
            isLoading={isLoading}
          />
        </div>
      </div>

      <div className="container flex-1">
        <DisplayCourses coursesPage={coursesPage} isLoading={isLoading} />
      </div>

      <Pagination setPage={setPage} currentPage={page} totalPages={coursesPage?.totalPages} />
    </>
  );
};

export default SearchPage;
