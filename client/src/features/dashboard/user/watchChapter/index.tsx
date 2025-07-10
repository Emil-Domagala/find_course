'use client';

import { useChapterAndCourseSidebarData } from '@/hooks/useChapterAndCourseSidebarData';
import WatchCourseLoading from '@/app/(dashboard)/user/(ProgresSidebar)/course/[courseId]/chapter/[chapterId]/loading';
import { ChapterType } from '@/types/search-enums';
import ReactVideo from '@/features/dashboard/user/watchChapter/components/ReactVideo';
import ChapterTextContent from '@/features/dashboard/user/watchChapter/components/ChapterTextContent';
import ChapterTabs from '@/features/dashboard/user/watchChapter/components/ChapterTabs';

const WatchCourse = () => {
  const { chapterIsLoading, currentProgressChapter, courseId, chapterId, chapterData, courseProgressData, nextChapterId, handleUpdateChapterProgress } =
    useChapterAndCourseSidebarData();

  if (!courseProgressData || chapterIsLoading || !chapterData || !currentProgressChapter) return <WatchCourseLoading />;

  return (
    <div className="flex h-full">
      <div className="flex-grow mx-auto">
        <h2 className="text-2xl text-white-50 font-semibold my-4">{chapterData.title || 'No Chapter Title'}</h2>

        <div className="mb-6 !border-none">
          <div className="h-[50vh] flex justify-center items-center p-0 m-0">
            {chapterData.type === ChapterType.VIDEO ? (
              <ReactVideo
                courseId={courseId as string}
                nextChapterId={nextChapterId}
                chapterId={chapterId as string}
                chapterData={chapterData}
                handleUpdateChapterProgress={handleUpdateChapterProgress}
                currentProgressChapter={currentProgressChapter}
              />
            ) : (
              <ChapterTextContent chapterData={chapterData} />
            )}
          </div>
        </div>

        {chapterData.type === ChapterType.VIDEO && <ChapterTabs chapterData={chapterData} />}
      </div>
    </div>
  );
};

export default WatchCourse;
