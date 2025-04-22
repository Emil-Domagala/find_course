

declare global {
type Page<T> = {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  page: number;
  empty: boolean;
};
}

export {}