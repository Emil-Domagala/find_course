declare global{

type PagingResult<T> = {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  page: number;
  empty: boolean;
};
}

export {}