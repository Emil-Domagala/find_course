'use client';

import { transformKey, transformToFrontendFormat } from '@/lib/utils';
import { Input } from '@/components/ui/input';
import { SearchDirection, CourseDtoSortField } from '@/types/search-enums';
import { CourseCategory } from '@/types/courses-enum';
import CustomSelect from './CustomSelect';
import { SetStateAction } from 'react';
import ButtonWithSpinner from '../ButtonWithSpinner';

type Props = {
  category: CourseCategory | undefined;
  setCategory: React.Dispatch<SetStateAction<CourseCategory | undefined>>;
  keyword: string | undefined;
  setKeyword: React.Dispatch<SetStateAction<string | undefined>>;
  sortField: CourseDtoSortField;
  setSortField: React.Dispatch<SetStateAction<CourseDtoSortField | undefined>>;
  direction: SearchDirection;
  setDirection: React.Dispatch<SetStateAction<SearchDirection | undefined>>;
  size: number;
  setSize: React.Dispatch<SetStateAction<number | undefined>>;
  handleFetchCourses: () => void;
  isLoading: boolean;
};

const Filter = ({
  category,
  setCategory,
  keyword,
  setKeyword,
  sortField,
  setSortField,
  direction,
  setDirection,
  size,
  setSize,
  handleFetchCourses,
  isLoading,
}: Props) => {
  return (
    <div data-testid="filter-component" className="flex flex-col lg:flex-row  gap-5 justify-center items-end w-full">
      {/* category */}
      <div className={`w-full lg:w-fit`}>
        <CustomSelect
          label="Category"
          value={category}
          onChange={setCategory}
          options={Object.values(CourseCategory)}
          placeholder="Select Category"
          transformFn={transformToFrontendFormat}
          clearable={true}
          selectTriggerClasses={'lg:w-52 w-full'}
          selectContentClasses={'max-h-60'}
        />
      </div>

      {/* Keyword */}
      <div className="w-full">
        {/* TODO: check how it looks */}
        <label htmlFor="keyword" className="text-sm mb-1">
          Search by title
        </label>
        <Input
          id="keyword"
          value={keyword || ''}
          onChange={(e) => setKeyword(e.target.value)}
          className="bg-customgreys-primarybg h-12 text-white-50 !shadow-none border-none font-medium text-lg lg:text-lg selection:bg-primary-750"
        />
      </div>

      <div className={`flex flex-row gap-2 justify-between items-end w-full lg:gap-5 lg:justify-center lg:w-fit`}>
        <CustomSelect
          label="Order by"
          value={sortField}
          onChange={setSortField}
          options={Object.values(CourseDtoSortField)}
          placeholder="Select Category"
          transformFn={transformKey}
          selectTriggerClasses={'lg:w-52 w-full'}
          selectContentClasses={'max-h-60'}
        />

        <CustomSelect
          label="Direction"
          value={direction}
          onChange={setDirection}
          options={Object.values(SearchDirection)}
          placeholder="Select Direction"
          transformFn={(e) => e}
          selectTriggerClasses={'w-16'}
          selectContentClasses={'max-h-60'}
          selectWrapperClasses={'w-fit'}
        />

        <CustomSelect
          label="Size"
          value={size || 12}
          onChange={setSize}
          options={[12, 24, 48, 100]}
          placeholder="Select Size"
          transformFn={(e) => e}
          selectTriggerClasses={'w-14'}
          selectContentClasses={'max-h-60'}
          selectWrapperClasses={'w-fit'}
        />

        <div className="max-w-[15rem] md:m-0 mx-auto w-full">
          <ButtonWithSpinner onClick={handleFetchCourses} isLoading={isLoading}>
            Search
          </ButtonWithSpinner>
        </div>
      </div>
    </div>
  );
};

export default Filter;
