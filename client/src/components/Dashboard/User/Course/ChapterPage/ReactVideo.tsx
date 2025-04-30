import { ChapterDetailsProtectedDto, ChapterProgress } from '@/types/courses';
import { useRouter } from 'next/navigation';
import { useRef } from 'react';
import ReactPlayer from 'react-player';

type Props = {courseId:string,currentProgressChapter:ChapterProgress, chapterData: ChapterDetailsProtectedDto; chapterId?: string; nextChapterId?: string; handleUpdateChapterProgress: (chapterProgressId: string, completed: boolean) => void; };

const ReactVideo = ({courseId, currentProgressChapter,chapterData, chapterId, nextChapterId, handleUpdateChapterProgress }: Props) => {
  const router=useRouter();
  const playerRef = useRef<ReactPlayer>(null);


  const handleProgress = ({ played }: { played: number }) => {
    if (played >= 0.8 && chapterId) {
      handleUpdateChapterProgress(currentProgressChapter.id, false);
    }
    }
  

  const handleNextChapter = () => {
   if ( nextChapterId) {
      router.push(`/user/course/${courseId}/chapter/${nextChapterId}`, {scroll: false,})
    }}

  return (
    <ReactPlayer
      className="!bg-customgreys-darkGrey"
      ref={playerRef}
      url={chapterData.videoUrl}
      controls
      // light
      playing={true}
      width="100%"
      height="100%"
      onEnded={handleNextChapter}
      onProgress={handleProgress}
      config={{
        file: {
          attributes: {
            controlsList: 'nodownload',
          },
        },
      }}
    />
  );
};

export default ReactVideo;
