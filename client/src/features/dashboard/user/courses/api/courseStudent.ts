import { CourseDtoWithFirstChapter } from '@/types/courses';
import { api } from '../../../../../state/api';
import { SearchDirection, CourseDtoSortField } from '@/types/search-enums';
import { CourseCategory } from '@/types/courses-enum';

type PaginationProps = {
  page?: number;
  size?: number;
  sortField?: CourseDtoSortField | '';
  direction?: SearchDirection;
  keyword?: string;
  category?: CourseCategory | '';
};

export const courseStudentApi = api.injectEndpoints({
  endpoints: (build) => ({
    getEnrolledCourses: build.query<Page<CourseDtoWithFirstChapter>, PaginationProps>({
      query: ({ page, size, sortField, direction, keyword = '', category }) => ({
        url: 'student/courses',
        params: { page, size, sortField, direction, keyword, category },
      }),
    }),
  }),
});

export const { useLazyGetEnrolledCoursesQuery } = courseStudentApi;
