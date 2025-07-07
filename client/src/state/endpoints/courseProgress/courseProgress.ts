import { CourseProgress, UpdateProgressRequest } from '@/types/courses';
import { api } from '../../api';

export const courseProgressApi = api.injectEndpoints({
  endpoints: (build) => ({
    // Fetch user course progress with course structure
    getUserCourseProgress: build.query<CourseProgress, { courseId: string }>({
      query: ({ courseId }) => ({
        url: `/progress/${courseId}`,
        method: 'GET',
      }),
      providesTags: ['UserCourseProgress'],
    }),

    // Update course progress
    updateCourseChapterProgress: build.mutation<void, { courseId: string; request: UpdateProgressRequest }>({
      query: ({ courseId, request }) => ({
        url: `/progress/${courseId}`,
        method: 'PATCH',
        body: request,
      }),
      async onQueryStarted({ courseId, request }, { dispatch, queryFulfilled }) {
        const chapterIdToUpdate = request.chapterProgressId;
        const newCompletedStatus = request.completed;

        const patchResult = dispatch(
          // @ts-expect-error code is fine redux error
          api.util.updateQueryData('getUserCourseProgress', { courseId }, (draft: CourseProgress) => {
            if (!draft || !draft.sections) return;

            let found = false;

            for (const section of draft.sections) {
              if (section.chapters) {
                const chapterIndex = section.chapters.findIndex((chapter) => chapter.id === chapterIdToUpdate);
                if (chapterIndex !== -1) {
                  section.chapters[chapterIndex].completed = newCompletedStatus;
                  found = true;
                  break;
                }
              }
            }

            if (!found) {
              console.warn(`Optimistic update: Chapter with ID ${chapterIdToUpdate} not found in draft data.`);
            }
          }),
        );

        try {
          await queryFulfilled;
        } catch (e) {
          console.log(e);
          patchResult.undo();
        }
      },
    }),
  }),
});

export const { useGetUserCourseProgressQuery, useUpdateCourseChapterProgressMutation } = courseProgressApi;
