'use client';

import { transformKey, transformToFrontendFormat } from '@/lib/utils';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { SearchDirection, SearchField } from '@/types/enums';
import { CourseCategory } from '@/types/courses-enum';
import { Loader } from 'lucide-react';
import CustomSelect from './CustomSelect';
import { SetStateAction } from 'react';

type Props = {
  category: CourseCategory | undefined;
  setCategory: React.Dispatch<SetStateAction<CourseCategory | undefined>>;
  keyword: string | undefined;
  setKeyword: React.Dispatch<SetStateAction<string | undefined>>;
  sortField: SearchField;
  setSortField: React.Dispatch<SetStateAction<SearchField | undefined>>;
  direction: SearchDirection;
  setDirection: React.Dispatch<SetStateAction<SearchDirection | undefined>>;
  size: number;
  setSize: React.Dispatch<SetStateAction<number | undefined>>;
  handleFetchCourses: () => void;
  isLoading?: boolean;
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
    <div className="flex flex-col md:flex-row  gap-5 justify-center items-end w-full">
      {/* category */}
      <div className={`w-full md:w-fit`}>
        <CustomSelect
          label="Category"
          value={category}
          onChange={setCategory}
          options={Object.values(CourseCategory)}
          placeholder="Select Category"
          transformFn={transformToFrontendFormat}
          clearable={true}
          selectTriggerClasses={'md:w-52 w-full'}
          selectContentClasses={'max-h-60'}
        />
      </div>

      {/* Keyword */}
      <div className="w-full">
        <p className="text-sm mb-1">Search by title</p>
        <Input
          value={keyword || ''}
          onChange={(e) => setKeyword(e.target.value)}
          className="bg-customgreys-primarybg h-12 text-white-50 !shadow-none border-none font-medium text-md md:text-lg selection:bg-primary-750"
        />
      </div>

      <div className={`flex flex-row gap-2 justify-between items-end w-full md:gap-5 md:justify-center md:w-fit`}>
        <CustomSelect
          label="Order by"
          value={sortField}
          onChange={setSortField}
          options={Object.values(SearchField)}
          placeholder="Select Category"
          transformFn={transformKey}
          selectTriggerClasses={'md:w-52 w-full'}
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
        />

        <Button variant="primary" className="h-12 text-md" onClick={handleFetchCourses}>
          Search {isLoading && <Loader size={20} className="animate-[spin_2s_linear_infinite]" />}
        </Button>
      </div>
    </div>
  );
};

export default Filter;
