import { cn } from '@/lib/utils';
import { CheckCircle, FileText } from 'lucide-react';

type Props = {
  chapter: any;
  index: number;
  sectionId: string;
  sectionProgress: any;
  chapterId: string;
  handleChapterClick: (sectionId: string, chapterId: string) => void;
  updateChapterProgress: (sectionId: string, chapterId: string, completed: boolean) => void;
};

const ChapterItem = ({ chapter, index, sectionId, sectionProgress, chapterId, handleChapterClick, updateChapterProgress }: Props) => {
  const chapterProgress = sectionProgress?.chapters.find((c: any) => c.chapterId === chapter.chapterId);
  const isCompleted = chapterProgress?.completed;
  const isCurrentChapter = chapterId === chapter.chapterId;

  const handleToggleComplete = (e: React.MouseEvent) => {
    e.stopPropagation();

    updateChapterProgress(sectionId, chapter.chapterId, !isCompleted);
  };

  return (
    <li
      className={cn('flex gap-3 items-center px-7 py-4 text-gray-300 cursor-pointer hover:bg-gray-700/20', {
        'bg-gray-700/50': isCurrentChapter,
      })}
      onClick={() => handleChapterClick(sectionId, chapter.chapterId)}>
      {isCompleted ? (
        <div className="bg-secondary-700 rounded-full p-1" onClick={handleToggleComplete} title="Toggle completion status">
          <CheckCircle className="text-white-100 w-4 h-4" />
        </div>
      ) : (
        <div
          className={cn('border border-gray-600 rounded-full w-6 h-6 flex items-center justify-center text-xs text-gray-400', {
            'bg-secondary-700 text-gray-800': isCurrentChapter,
          })}>
          {index + 1}
        </div>
      )}
      <span
        className={cn('flex-1 text-sm text-gray-500', {
          'text-gray-500 line-through': isCompleted,
          'text-secondary-700': isCurrentChapter,
        })}>
        {chapter.title}
      </span>
      {chapter.type === 'Text' && <FileText className="text-gray-500 ml-2 w-4 h-4" />}
    </li>
  );
};

export default ChapterItem;
