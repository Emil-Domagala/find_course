'use client';

import DisplayCourses from '@/components/NonDashboard/Search/DisplayCourses';
import PageSize from '@/components/NonDashboard/Search/PageSize';
import Pagination from '@/components/NonDashboard/Search/Pagination';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { SearchDirection, SearchField, useSearchFilters } from '@/hooks/useSearchFilters';
import { transformKey, transformToFrontendFormat } from '@/lib/utils';
import { useGetCoursesPublicQuery } from '@/state/api';
import { CourseCategory } from '@/types/courses-enum';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue, SelectViewport } from '@radix-ui/react-select';

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

  const {
    data: coursesPage,
    isLoading,
    refetch,
  } = useGetCoursesPublicQuery(
    {
      size,
      page,
      sortField,
      direction,
      keyword,
      category,
    },
    { skip: true },
  );

  const label = 'text-sm mb-1';
  const itemContainer = 'w-fit';
  const selectItemStyle =
    'text-center cursor-pointer bg-customgreys-darkGrey min-w-[100%] p-2 hover:bg-customgreys-darkerGrey hover:!outline-none';
  const selectTriggerStyleBasic = 'bg-customgreys-primarybg rounded-md overflow-hidden text-sm px-2 h-12';
  const selectContentStyleBasic = 'mt-1 py-2 bg-customgreys-darkGrey rounded-md';

  return (
    <div>
      <div className="w-full bg-customgreys-secondarybg py-5">
        <div className="container">
          {/* <form action=""> */}
          <div className="flex flex-row gap-5 justify-center items-end w-full ">
            {/* category */}
            <div className={itemContainer}>
              <p className={label}>Category</p>
              <Select
                value={category}
                onValueChange={(value) => setCategory(value === '__clear__' ? '' : (value as CourseCategory))}>
                <SelectTrigger className={`${selectTriggerStyleBasic} w-32`}>
                  <SelectValue placeholder="Category">{transformToFrontendFormat(category)}</SelectValue>
                </SelectTrigger>
                <SelectContent className={`max-h-60 ${selectContentStyleBasic}`} position="popper">
                  <SelectViewport className="max-h-60 overflow-auto ">
                    <SelectItem value="__clear__" className={selectItemStyle}>
                      All
                    </SelectItem>
                    {Object.values(CourseCategory).map((cat) => (
                      <SelectItem value={cat} key={cat} className={selectItemStyle}>
                        {transformToFrontendFormat(cat)}
                      </SelectItem>
                    ))}
                  </SelectViewport>
                </SelectContent>
              </Select>
            </div>

            {/* Keyword */}
            <div className={`${itemContainer} w-full`}>
              <p className={label}>Search by title</p>
              <Input
                onChange={(e) => setKeyword(e.target.value)}
                className="bg-customgreys-primarybg h-12 text-white-50 !shadow-none border-none font-medium text-md md:text-lg selection:bg-primary-750"
              />
            </div>

            {/* Filter */}
            <div className={itemContainer}>
              <p className={label}>Order by</p>
              <Select value={sortField} onValueChange={(value) => setSortField(value as SearchField)}>
                <SelectTrigger className={`${selectTriggerStyleBasic} w-24`}>
                  <SelectValue placeholder="Created At">{transformKey(sortField)}</SelectValue>
                </SelectTrigger>
                <SelectContent className={`w-24 ${selectContentStyleBasic}`} position="popper">
                  {Object.values(SearchField).map((field) => (
                    <SelectItem value={field} key={field} className={selectItemStyle}>
                      {transformKey(field)}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>

            {/* Direction */}
            <div className={itemContainer}>
              <p className={label}>Direction</p>
              <Select value={direction} onValueChange={(value) => setDirection(value as SearchDirection)}>
                <SelectTrigger className={`${selectTriggerStyleBasic} w-14`}>
                  <SelectValue placeholder="Category">{direction}</SelectValue>
                </SelectTrigger>
                <SelectContent className={`w-14 ${selectContentStyleBasic}`} position="popper">
                  {Object.values(SearchDirection).map((dir) => (
                    <SelectItem value={dir} key={dir} className={selectItemStyle}>
                      {dir}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>

            {/* Page size */}

            <div className={itemContainer}>
              <p className={label}>Elements</p>
              <Select value={String(size)} onValueChange={(value) => setSize(Number(value))}>
                <SelectTrigger className={`${selectTriggerStyleBasic} w-14`}>
                  <SelectValue placeholder="Select page size">{String(size)}</SelectValue>
                </SelectTrigger>
                <SelectContent className={`w-14 ${selectContentStyleBasic}`} position="popper">
                  {[12, 24, 48, 100].map((option) => (
                    <SelectItem className={selectItemStyle} key={option} value={String(option)}>
                      {option}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>

            {/* Apply */}
            <Button variant="primary" className="h-12 text-md" onClick={refetch}>
              Search
            </Button>
          </div>
          {/* </form> */}
        </div>
      </div>

      {/* Courses */}
      <div className="container">
        <DisplayCourses isLoading={isLoading} coursesPage={coursesPage} size={size} />
      </div>
      <Pagination setPage={setPage} page={page} coursesPage={coursesPage} />
      {/* Select page size */}
    </div>
  );
};

export default SearchPage;
