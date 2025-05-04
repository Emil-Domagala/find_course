import { cn } from '@/lib/utils';
import { ChapterProgress } from '@/types/courses';
import { ChapterType } from '@/types/enums';
import { CheckCircle, FileText, Video } from 'lucide-react';

type Props = {
  currentChapterId: string;
  chapter: ChapterProgress;
  index: number;
  updateChapterProgress: (chapterProgressId: string, isCompleted: boolean) => void;
  handleChapterClick: (chapterId: string) => void;
};

const ChapterItem = ({ currentChapterId, chapter, index, updateChapterProgress, handleChapterClick }: Props) => {
  const isCompleted = chapter.completed;
  const isCurrentChapter = currentChapterId === chapter.originalChapter.id;

  const handleToggleComplete = (e: React.MouseEvent) => {
    e.stopPropagation();
    updateChapterProgress(chapter.id, isCompleted);
  };

  return (
    <li
      className={cn('flex gap-3 items-center px-7 py-4 text-gray-300 cursor-pointer hover:bg-gray-700/20', {
        'bg-gray-700/50': isCurrentChapter,
      })}
      onClick={() => handleChapterClick(chapter.originalChapter.id)}>
      {isCompleted ? (
        <div className="bg-secondary-700 rounded-full p-1" onClick={handleToggleComplete} title="Toggle completion status">
          <CheckCircle className="text-white-100 w-4 h-4" />
        </div>
      ) : (
        <div
          onClick={handleToggleComplete}
          title="Toggle completion status"
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
        {chapter.originalChapter.title}
      </span>
      {chapter.originalChapter.type === ChapterType.TEXT ? <FileText className="text-gray-500 ml-2 w-4 h-4" /> : <Video className="text-gray-500 ml-2 w-4 h-4" />}
    </li>
  );
};

export default ChapterItem;
