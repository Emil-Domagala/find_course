import { cn } from '@/lib/utils';
import { Trophy } from 'lucide-react';

type Props = { section: any; sectionProgress: any; completedChapters: number; totalChapters: number };

const ProgressVisuals = ({ section, sectionProgress, completedChapters, totalChapters }: Props) => {
  return (
    <>
      <div className="flex justify-between items-center gap-5 mb-2 px-7">
        <div className="flex-grow flex gap-1">
          {section.chapters.map((chapter: any) => {
            const isCompleted = sectionProgress?.chapters.find((c: any) => c.chapterId === chapter.chapterId)?.completed;
            return <div key={chapter.chapterId} className={cn('h-1 flex-grow rounded-full bg-gray-700', isCompleted && 'bg-secondary-700')}></div>;
          })}
        </div>
        <div className="bg-secondary-700 rounded-full p-3 flex items-center justify-center">
          <Trophy className="text-customgreys-secondarybg w-4 h-4" />
        </div>
      </div>
      <p className="text-gray-500 text-xs mt-3 mb-5 px-7">
        {completedChapters}/{totalChapters} COMPLETED
      </p>
    </>
  );
};

export default ProgressVisuals;
