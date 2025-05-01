'use client';

import { usePathname, useRouter, useSearchParams } from 'next/navigation';
import { SetStateAction, useCallback, useEffect, useState } from 'react';

// INITIAL VALUE IS USED TO ENSURE THAT VALUE WILL NOT BE UNDEFINED

type UseSelectFilterProps<T = string | number> = {
  valueName: string;
  initialValue?: T;
};

type UseSelectFilterReturn<T> = [T | undefined, React.Dispatch<SetStateAction<T | undefined>>];

export const useSelectFilter = <T extends string | number = string>({ valueName, initialValue }: UseSelectFilterProps<T>): UseSelectFilterReturn<T> => {
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

    return initialValue;
  }, [initialValue]);

  const [filterValue, setFilterValue] = useState<T | undefined>(getInitialState);

  useEffect(() => {
    setFilterValue(getInitialState());
  }, []);

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
  }, [filterValue, valueName, pathname, router, searchParams]);

  return [filterValue, setFilterValue];
};
