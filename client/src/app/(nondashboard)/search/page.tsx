'use client';

import DisplayCourses from '@/components/NonDashboard/Search/DisplayCourses';
import Pagination from '@/components/Common/Filter/Pagination';

import { useEffect } from 'react';
import Filter from '@/components/Common/Filter/Filter';
import { useSelectFilter } from '@/hooks/useSelectFilter';
import { CourseCategory } from '@/types/courses-enum';
import { SearchDirection, CourseDtoSortField } from '@/types/search-enums';
import { useLazyGetCoursesPublicQuery } from '@/state/endpoints/course/coursePublic';

const SearchPage = () => {
  const [category, setCategory] = useSelectFilter<CourseCategory>({ valueName: 'category' });
  const [keyword, setKeyword] = useSelectFilter<string>({ valueName: 'keyword' });
  const [sortField, setSortField] = useSelectFilter<CourseDtoSortField>({
    valueName: 'sortField',
    initialValue: CourseDtoSortField.CreatedAt,
  });
  const [direction, setDirection] = useSelectFilter<SearchDirection>({
    valueName: 'direction',
    initialValue: SearchDirection.ASC,
  });
  const [size, setSize] = useSelectFilter<number>({ valueName: 'size', initialValue: 12 });
  const [page, setPage] = useSelectFilter<number>({ valueName: 'page', initialValue: 0 });

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
            sortField={sortField || CourseDtoSortField.CreatedAt}
            setSortField={setSortField}
            direction={direction || SearchDirection.ASC}
            setDirection={setDirection}
            size={size || 12}
            setSize={setSize}
            handleFetchCourses={handleFetchCourses}
            isLoading={isLoading}
          />
        </div>
      </div>

      <div className="container flex-1">
        <DisplayCourses coursesPage={coursesPage} isLoading={isLoading} />
      </div>

      <Pagination setPage={setPage} currentPage={page || 0} totalPages={coursesPage?.totalPages} />
    </>
  );
};

export default SearchPage;
