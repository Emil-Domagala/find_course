'use client';

import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { cn } from '@/lib/utils';
import { ReactNode, SetStateAction } from 'react';

type SelectOptionValue = string | number;

type CustomSelectProps<T extends SelectOptionValue> = {
  label?: string;
  value: T | undefined;
  onChange: React.Dispatch<SetStateAction<T | undefined>>;
  options: ReadonlyArray<T>;
  placeholder: string;
  transformFn: (item: T | undefined) => ReactNode;
  selectTriggerClasses?: string;
  selectContentClasses?: string;
  selectItemClasses?: string;
  clearable?: boolean;
  selectWrapperClasses?: string;
};

const CustomSelect = <T extends SelectOptionValue>({
  selectTriggerClasses,
  selectContentClasses,
  options,
  transformFn,
  onChange,
  value,
  label,
  placeholder,
  clearable,
  selectWrapperClasses,
}: CustomSelectProps<T>) => {
  const handleValueChange = (selectedValue: string) => {
    if (clearable && selectedValue === '__clear__') {
      onChange(undefined);
    } else {
      const originalOption = options.find((opt) => String(opt) === selectedValue);
      if (originalOption !== undefined) {
        onChange(originalOption);
      }
    }
  };

  return (
    <div className={cn('w-full', selectWrapperClasses)}>
      {label && <p className="text-sm mb-1">{label}</p>}
      <Select value={String(value)} onValueChange={handleValueChange}>
        <SelectTrigger className={cn('border-none bg-customgreys-primarybg rounded-md overflow-hidden text-sm px-2 !h-12', selectTriggerClasses)}>
          <SelectValue placeholder={placeholder}>{transformFn(value) || placeholder}</SelectValue>
        </SelectTrigger>
        <SelectContent className={cn('border-none mt-1 py-2 bg-customgreys-darkGrey rounded-md', selectContentClasses)} position="popper">
          {clearable && (
            <SelectItem value="__clear__" className="text-center cursor-pointer bg-customgreys-darkGrey min-w-[100%] p-2 hover:bg-customgreys-darkerGrey hover:!outline-none">
              All
            </SelectItem>
          )}
          {options.map((option) => (
            <SelectItem
              value={String(option)}
              key={option}
              className="text-center cursor-pointer bg-customgreys-darkGrey min-w-[100%] p-2 hover:bg-customgreys-darkerGrey hover:!outline-none">
              {transformFn(option)}
            </SelectItem>
          ))}
        </SelectContent>
      </Select>
    </div>
  );
};

export default CustomSelect;
