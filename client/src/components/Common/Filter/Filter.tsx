'use client';

import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { transformKey, transformToFrontendFormat } from '@/lib/utils';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { SearchDirection, SearchField } from '@/hooks/useSearchFilters';
import { CourseCategory } from '@/types/courses-enum';
import { Loader } from 'lucide-react';

type Props = {
  category: CourseCategory | '';
  setCategory: (value: CourseCategory | '') => void;
  keyword: string;
  setKeyword: (value: string) => void;
  sortField: SearchField | '';
  setSortField: (value: SearchField) => void;
  direction: SearchDirection;
  setDirection: (value: SearchDirection) => void;
  size: number;
  setSize: (value: number) => void;
  handleFetchCourses: () => void;
  isLoading?: boolean;
};

const label = 'text-sm mb-1';
const itemContainer = 'w-fit';
const selectItemStyle =
  'text-center cursor-pointer bg-customgreys-darkGrey min-w-[100%] p-2 hover:bg-customgreys-darkerGrey hover:!outline-none';
const selectTriggerStyleBasic = ' border-none bg-customgreys-primarybg rounded-md overflow-hidden text-sm px-2 !h-12';
const selectContentStyleBasic = ' border-none mt-1 py-2 bg-customgreys-darkGrey rounded-md';
const filterGroupStyles = 'flex flex-row gap-2 justify-between items-end w-full md:gap-5 md:justify-center';

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
        <p className={label}>Category</p>
        <Select
          value={category}
          onValueChange={(value) => setCategory(value === '__clear__' ? '' : (value as CourseCategory))}>
          <SelectTrigger className={`${selectTriggerStyleBasic} md:w-52 w-full `}>
            <SelectValue placeholder="Category">{transformToFrontendFormat(category)}</SelectValue>
          </SelectTrigger>
          <SelectContent className={`max-h-60 ${selectContentStyleBasic}`} position="popper">
            {/* <SelectViewport className="max-h-60 overflow-auto "> */}
            <SelectItem value="__clear__" className={selectItemStyle}>
              All
            </SelectItem>
            {Object.values(CourseCategory).map((cat) => (
              <SelectItem value={cat} key={cat} className={selectItemStyle}>
                {transformToFrontendFormat(cat)}
              </SelectItem>
            ))}
            {/* </SelectViewport> */}
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

      <div className={`${filterGroupStyles}  md:w-fit`}>
        {/* Filter */}
        <div className={itemContainer}>
          <p className={label}>Order by</p>
          <Select value={sortField} onValueChange={(value) => setSortField(value as SearchField)}>
            <SelectTrigger className={`${selectTriggerStyleBasic} w-28`}>
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
            <SelectTrigger className={`${selectTriggerStyleBasic} w-16`}>
              <SelectValue placeholder="Category">{direction}</SelectValue>
            </SelectTrigger>
            <SelectContent className={` ${selectContentStyleBasic}`} position="popper">
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
            <SelectContent className={` ${selectContentStyleBasic}`} position="popper">
              {[12, 24, 48, 100].map((option) => (
                <SelectItem className={selectItemStyle} key={option} value={String(option)}>
                  {option}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>

        {/* Apply */}
        <Button variant="primary" className="h-12 text-md" onClick={handleFetchCourses}>
          Search {isLoading && <Loader size={20} className="animate-[spin_2s_linear_infinite]" />}
        </Button>
      </div>
    </div>
  );
};

export default Filter;
