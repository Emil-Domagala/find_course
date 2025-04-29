import { SectionProgress } from '@/types/courses';
import ChapterItem from './ChapterItem';

type Props = {
  sectionProgress: SectionProgress;
  currentChapterId: string;
  updateChapterProgress: (chapterProgressId: string, isCompleted: boolean) => void;
  handleChapterClick: (chapterId: string) => void;
};

const ChaptersList = ({ currentChapterId, sectionProgress, updateChapterProgress, handleChapterClick }: Props) => {
  return (
    <ul>
      {sectionProgress.chapters.map((chapter, index: number) => (
        <ChapterItem
          key={chapter.id}
          index={index}
          chapter={chapter}
          currentChapterId={currentChapterId}
          updateChapterProgress={updateChapterProgress}
          handleChapterClick={handleChapterClick}
        />
      ))}
    </ul>
  );
};

export default ChaptersList;
