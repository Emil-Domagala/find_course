import { CourseDetailsPublicDto } from '@/types/courses';
import { api } from '../../api';
import { CourseCategory } from '@/types/courses-enum';
import { SearchDirection, CourseDtoSortField } from '@/types/search-enums';

type PaginationProps = {
  page?: number;
  size?: number;
  sortField?: CourseDtoSortField | '';
  direction?: SearchDirection;
  keyword?: string;
  category?: CourseCategory | '';
};

export const courseTeacherApi = api.injectEndpoints({
  endpoints: (build) => ({
    // GET LIST OF TEACHER DTO COURSES
    getCoursesTeacher: build.query<Page<CourseDto>, PaginationProps>({
      query: ({ page, size, sortField, direction, keyword = '', category }) => ({
        url: 'teacher/courses',
        params: { page, size, sortField, direction, keyword, category },
      }),
      serializeQueryArgs: ({ endpointName, queryArgs }) => {
        const { size, sortField, direction, keyword, category } = queryArgs;
        return `${endpointName}-${JSON.stringify({ size, sortField, direction, keyword, category })}-page-${queryArgs.page}`;
      },

      providesTags: (result, _error, arg) =>
        result
          ? [
              ...result?.content.map((course) => ({ type: 'TeachedCourseDtos' as const, id: course.id })),
              { type: 'TeachedCourseDtos' as const, id: `LIST-${arg.page}` },
            ]
          : [{ type: 'TeachedCourseDtos' as const, id: `LIST-${arg.page}` }],
    }),

    // create mock course
    createCourse: build.mutation<CourseDto, void>({
      query: () => ({ url: 'teacher/courses', method: 'POST' }),
    }),
    // DELETE COURSE
    deleteCourse: build.mutation<void, { courseId: string }>({
      query: ({ courseId }) => ({
        url: `teacher/courses/${courseId}`,
        method: 'DELETE',
      }),
      async onQueryStarted({ courseId }, { dispatch, queryFulfilled, getState }) {
        const patches: { undo: () => void }[] = [];
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        const state = getState() as any;
        const subscriptions = state.api?.subscriptions;
        const queries = state.api?.queries;

        if (!subscriptions || !queries) return;
        for (const key in queries) {
          if (
            !key.startsWith('getCoursesTeacher-{') ||
            queries[key]?.status !== 'fulfilled' ||
            !subscriptions[key] ||
            Object.keys(subscriptions[key]).length === 0
          )
            return;

          const cacheEntry = queries[key];
          const queryArgs = cacheEntry.originalArgs;
          if (!queryArgs) continue;
          try {
            const patchResult = dispatch(
              // @ts-expect-error redux error code fine
              api.util.updateQueryData('getCoursesTeacher', queryArgs, (draft: Page<CourseDto>) => {
                if (!draft || !draft.content || !Array.isArray(draft.content)) return;

                const index = draft.content.findIndex((course) => course.id === courseId);

                if (index !== -1) {
                  draft.content.splice(index, 1);
                  if (typeof draft.totalElements === 'number') {
                    draft.totalElements--;
                  }
                }
              }),
            );
            patches.push(patchResult);
          } catch (error) {
            console.error(`Error dispatching updateQueryData for key ${key} with args ${JSON.stringify(queryArgs)}:`, error);
          }
        }

        try {
          await queryFulfilled;
        } catch (err) {
          console.log(err);
          patches.forEach((patch) => patch.undo());
        }
      },

      invalidatesTags: (_result, error, { courseId }) =>
        error
          ? []
          : [
              { type: 'TeachedCourseDtos', id: courseId },
              { type: 'TeachedCourseDtos', id: 'LIST' },
            ],
    }),

    getTeacherCourseById: build.query<CourseDetailsPublicDto, string>({
      query: (courseId) => ({ url: `teacher/courses/${courseId}` }),
    }),

    updateCourse: build.mutation<void, { courseId: string; courseData: FormData }>({
      query: ({ courseData, courseId }) => ({ url: `teacher/courses/${courseId}`, method: 'PATCH', body: courseData }),
      invalidatesTags: ['TeachedCourseDtos'],
    }),
  }),
});

export const { useLazyGetCoursesTeacherQuery, useCreateCourseMutation, useDeleteCourseMutation, useGetTeacherCourseByIdQuery, useUpdateCourseMutation } =
  courseTeacherApi;
