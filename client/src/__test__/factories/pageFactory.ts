export function createPageResponse<T>(args: Partial<Page<T>>): Page<T> {
  const content = args.content ?? [];
  const totalElements = args.totalElements ?? content.length;
  const size = args.size ?? 10;
  const totalPages = args.totalPages ?? Math.ceil(totalElements / size);
  const empty = content.length === 0;

  return { content, totalPages, totalElements, size, page: args.page ?? 1, empty, ...args };
}
