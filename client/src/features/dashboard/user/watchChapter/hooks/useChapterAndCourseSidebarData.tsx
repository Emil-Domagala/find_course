import { useParams } from 'next/navigation';
import { useEffect, useMemo, useState } from 'react';
import { ChapterDetailsProtectedDto } from '../coursesProtected';
import { ChapterProgress, CourseProgress, SectionProgress, UpdateProgressRequest } from '../courseProgress';
import { useGetUserCourseProgressQuery, useUpdateCourseChapterProgressMutation } from '../api/courseProgress';
import { useGetChapterEnrolledStudentQuery, usePrefetch } from '../api/chapter';

type UseChapterAndCourseSidebarDataReturn = {
  chapterData: ChapterDetailsProtectedDto | undefined;
  courseId: string | string[] | undefined;
  chapterId: string | string[] | undefined;
  courseProgressData: CourseProgress | undefined;
  courseProgressIsLoading: boolean;
  chapterIsLoading: boolean;
  currentSectionIndex: number;
  currentChapterIndex: number;
  currentProgressSection: SectionProgress | null;
  currentProgressChapter: ChapterProgress | null;
  nextChapterId: string | undefined;
  handleUpdateChapterProgress: (chapterProgressId: string, completed: boolean) => void;
};

export const useChapterAndCourseSidebarData = (): UseChapterAndCourseSidebarDataReturn => {
  const { courseId, chapterId } = useParams();
  const [updateChapterProgress] = useUpdateCourseChapterProgressMutation();
  const { data: chapterData, isLoading: chapterIsLoading } = useGetChapterEnrolledStudentQuery(
    { courseId: courseId as string, chapterId: chapterId as string },
    { skip: !courseId || !chapterId },
  );

  const prefetchChapter = usePrefetch('getChapterEnrolledStudent');
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

  const { currentSectionIndex, currentChapterIndex, currentSection, currentChapter } = useMemo(() => {
    if (!courseProgressData || !chapterId || !courseProgressData.sections)
      return { currentSectionIndex: -1, currentChapterIndex: -1, currentSection: null, currentChapter: null };

    let foundSectionIndex = -1;
    let foundChapterIndex = -1;
    let foundSection: SectionProgress | null = null;
    let foundChapter: ChapterProgress | null = null;

    foundSectionIndex = courseProgressData.sections.findIndex((section) => section.chapters?.some((chapter) => chapter.originalChapter?.id === chapterId));

    // If section found, find the chapter index and objects
    if (foundSectionIndex !== -1) {
      foundSection = courseProgressData.sections[foundSectionIndex];
      foundChapterIndex = foundSection.chapters?.findIndex((chapter) => chapter.originalChapter?.id === chapterId) ?? -1;
      if (foundChapterIndex !== -1) {
        foundChapter = foundSection.chapters[foundChapterIndex];
      }
    }

    return {
      currentSectionIndex: foundSectionIndex,
      currentChapterIndex: foundChapterIndex,
      currentSection: foundSection,
      currentChapter: foundChapter,
    };
  }, [courseProgressData, chapterId]);

  const handleUpdateChapterProgress = (chapterProgressId: string, completed: boolean) => {
    if (!courseId) return;
    const request: UpdateProgressRequest = { chapterProgressId, completed: !completed };
    updateChapterProgress({ courseId: courseId as string, request });
  };

  const [nextChapterId, setNextChapterId] = useState<string | undefined>();
  useEffect(() => {
    if (isSuccess && courseProgressData && currentSectionIndex !== -1 && currentChapterIndex !== -1 && currentSection?.chapters) {
      const chapters = currentSection.chapters;
      const totalChaptersInSection = chapters.length;

      if (currentChapterIndex === totalChaptersInSection - 1) {
        // Last chapter in current section?
        if (currentSectionIndex === courseProgressData.sections.length - 1) {
          // Last section overall?
          setNextChapterId(undefined); // No next chapter
        } else {
          // Go to first chapter of next section
          setNextChapterId(courseProgressData.sections[currentSectionIndex + 1]?.chapters?.[0]?.originalChapter?.id);
        }
      } else {
        // Go to next chapter in current section
        setNextChapterId(chapters[currentChapterIndex + 1]?.originalChapter?.id);
      }
    } else if (!courseProgressIsLoading) {
      setNextChapterId(undefined);
    }
  }, [isSuccess, courseProgressData, currentSectionIndex, currentChapterIndex, currentSection, courseProgressIsLoading]);

  useEffect(() => {
    if (nextChapterId && courseId) {
      prefetchChapter({ courseId: courseId as string, chapterId: nextChapterId }, { force: false });
    }
  }, [nextChapterId]);

  return {
    chapterData,
    courseId,
    chapterId,
    courseProgressData,
    courseProgressIsLoading,
    chapterIsLoading,
    currentSectionIndex,
    currentChapterIndex,
    currentProgressSection: currentSection,
    currentProgressChapter: currentChapter,
    nextChapterId,
    handleUpdateChapterProgress,
  };
};
