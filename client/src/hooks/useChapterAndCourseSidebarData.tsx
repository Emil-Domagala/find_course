import { useGetUserCourseProgressQuery, useUpdateCourseChapterProgressMutation } from '@/state/api';
import { useParams } from 'next/navigation';
import { useEffect, useState } from 'react';

export const useChapterAndCourseSidebarData = () => {
  const [nextChapterId, setNextChapterId] = useState<string | undefined>();
  const { courseId, chapterId } = useParams();
  const [updateChapterProgress] = useUpdateCourseChapterProgressMutation();
  const chapterData = 'Some chapter Data';

  const {
    data: courseProgressData,
    isLoading: courseProgressIsLoading,
    isSuccess,
  } = useGetUserCourseProgressQuery(
    { courseId: courseId as string },
    {
      skip: !courseId,
    },
  );

  const handleUpdateChapterProgress = (chapterProgressId: string, isCompleted: boolean) => {
    const request = { chapterProgressId, completed: !isCompleted };
    updateChapterProgress({ courseId: courseId as string, request });
  };

  useEffect(() => {
    const getNextChapterId = () => {
      if (!courseProgressData) return;
      const currentSectionIndex = courseProgressData.sections.findIndex((section) => section.chapters.some((chapter) => chapter.originalChapter.id === chapterId));
      const currentChapterIndex = courseProgressData.sections[currentSectionIndex].chapters.findIndex((chapter) => chapter.originalChapter.id === chapterId);

      const currentSectionChaptersLength = courseProgressData.sections[currentSectionIndex].chapters.length;
      if (currentChapterIndex === currentSectionChaptersLength - 1) {
        if (currentSectionIndex === courseProgressData.sections.length - 1) {
          setNextChapterId(undefined);
        } else {
          setNextChapterId(courseProgressData.sections[currentSectionIndex + 1].chapters[0].originalChapter.id);
        }
      } else {
        setNextChapterId(courseProgressData.sections[currentSectionIndex].chapters[currentChapterIndex + 1].originalChapter.id);
      }
    };
    getNextChapterId();
  }, [chapterId, isSuccess]);

  return {
    courseId,
    chapterId,
    chapterData,
    courseProgressData,
    courseProgressIsLoading,
    handleUpdateChapterProgress,
  };
};
