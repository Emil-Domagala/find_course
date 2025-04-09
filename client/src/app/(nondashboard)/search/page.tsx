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
  }, []);

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
          />
        </div>
      </div>

      <div className="container flex-1">
        <DisplayCourses coursesPage={coursesPage} size={size} />
      </div>

      <Pagination setPage={setPage} page={page} coursesPage={coursesPage} />
    </>
  );
};

export default SearchPage;
