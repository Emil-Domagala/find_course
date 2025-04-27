'use client';

import { usePathname, useRouter, useSearchParams } from 'next/navigation';
import { SetStateAction, useCallback, useEffect, useState } from 'react';

type UseSelectFilterProps<T = string | number> = {
  valueName: string;
  initialValue?: T;
  clearable?: boolean;
};

type UseSelectFilterReturn<T> = [T | undefined, React.Dispatch<SetStateAction<T | undefined>>];

export const useSelectFilter = <T extends string | number = string>({ valueName, initialValue, clearable }: UseSelectFilterProps<T>): UseSelectFilterReturn<T> => {
  const searchParams = useSearchParams();
  const pathname = usePathname();
  const router = useRouter();

  const getInitialState = useCallback(() => {
    const paramValue = searchParams.get(valueName);
    if (paramValue !== null) {
      if (typeof initialValue === 'number') {
        const num = Number(paramValue);
        return isNaN(num) ? initialValue : (num as T);
      }
      return paramValue as T;
    }
    if (!clearable) {
      return initialValue;
    }
    return;
  }, [searchParams, valueName, initialValue]);

  const [filterValue, setFilterValue] = useState<T | undefined>(getInitialState);

  useEffect(() => {
    setFilterValue(getInitialState());
  }, [getInitialState]);

  useEffect(() => {
    const newParams = new URLSearchParams(searchParams.toString());
    if (filterValue) {
      newParams.set(valueName, String(filterValue));
    } else {
      newParams.delete(valueName);
    }
    const queryString = newParams.toString();
    const newUrl = queryString ? `${pathname}?${queryString}` : pathname;
    router.replace(newUrl);
  }, [filterValue]);

  return [filterValue, setFilterValue];
};
