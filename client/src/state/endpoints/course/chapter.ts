import { ChapterDetailsProtectedDto } from '@/types/courses';
import { api } from '../../api';

export const chapterApi = api.injectEndpoints({
  endpoints: (build) => ({
    // Fetch Chapter whole data
    getChapterEnrolledStudent: build.query<ChapterDetailsProtectedDto, { courseId: string; chapterId: string }>({
      query: ({ courseId, chapterId }) => ({
        url: `/student/courses/${courseId}/chapters/${chapterId}`,
        method: 'GET',
      }),
      keepUnusedDataFor: 600,
      providesTags: ['Chapter'],
    }),
  }),
});

export const { useGetChapterEnrolledStudentQuery, usePrefetch } = chapterApi;
