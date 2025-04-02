import { useState, useEffect } from 'react';
import { useSearchParams, useRouter } from 'next/navigation';

export enum SearchField {
  CreatedAt = 'createdAt',
  UpdatedAt = 'updatedAt',
  Title = 'title',
  Price = 'price',
  None = '',
}

export enum SearchDirection {
  ASC = 'ASC',
  DESC = 'DESC',
}

export const useSearchFilters = () => {
  const searchParams = useSearchParams();
  const router = useRouter();

  const [size, setSize] = useState(Number(searchParams.get('size')) || 12);
  const [page, setPage] = useState(Number(searchParams.get('page')) || 0);
  const [sortField, setSortField] = useState<SearchField>(
    (searchParams.get('sortField') as SearchField) || SearchField.None,
  );
  const [direction, setDirection] = useState<SearchDirection>(
    (searchParams.get('direction') as SearchDirection) || SearchDirection.ASC,
  );
  const [keyword, setKeyword] = useState(searchParams.get('keyword') || '');

  useEffect(() => {
    const params = new URLSearchParams();

    if (size) params.set('size', size.toString());
    if (page) params.set('page', page.toString());
    if (sortField) params.set('sortField', sortField);
    if (direction) params.set('direction', direction);
    if (keyword) params.set('keyword', keyword);

    router.replace(`?${params.toString()}`, { scroll: false });
  }, [size, page, sortField, direction, keyword, router]);

  return {
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
  };
};
