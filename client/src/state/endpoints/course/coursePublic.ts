import { SearchDirection, SearchField } from '@/types/enums';
import { api } from '../../api';
import { CourseCategory } from '@/types/courses-enum';

type PaginationProps = {
  page?: number;
  size?: number;
  sortField?: SearchField | '';
  direction?: SearchDirection;
  keyword?: string;
  category?: CourseCategory | '';
};

export const coursePublicApi = api.injectEndpoints({
  endpoints: (build) => ({
    // GET LIST OF PUBLIC DTO COURSES
    getCoursesPublic: build.query<Page<CourseDto>, PaginationProps>({
      query: ({ page, size, sortField, direction, keyword = '', category }) => ({
        url: 'public/courses',
        params: { page, size, sortField, direction, keyword, category },
      }),
      serializeQueryArgs: ({ endpointName, queryArgs }) => {
        const { size, sortField, direction, keyword, category } = queryArgs;
        return `${endpointName}-${JSON.stringify({ size, sortField, direction, keyword, category })}-page-${queryArgs.page}`;
      },

      providesTags: (result, _error, arg) =>
        result
          ? [...result?.content.map((course) => ({ type: 'CourseDtos' as const, id: course.id })), { type: 'CourseDtos' as const, id: `LIST-${arg.page}` }]
          : [{ type: 'CourseDtos' as const, id: `LIST-${arg.page}` }],
    }),
  }),
});
export const { useGetCoursesPublicQuery, useLazyGetCoursesPublicQuery } = coursePublicApi;
