import { ChevronDown, ChevronUp } from 'lucide-react';
import ProgressVisuals from './ProgresVisuals';
import ChaptersList from './ChaptersList';
import { ChapterProgress, SectionProgress, UpdateProgressRequest } from '../../courseProgress';

export type UpdateProgressPayload = {
  courseId: string;
  request: UpdateProgressRequest;
};

type Props = {
  currentChapterId: string;
  index: number;
  sectionProgress: SectionProgress;
  expandedSections: string[];
  toggleSection: (sectionTitle: string) => void;
  handleChapterClick: (chapterId: string) => void;
  updateChapterProgress: (chapterProgressId: string, isCompleted: boolean) => void;
};

const SectionItem = ({ currentChapterId, index, sectionProgress, expandedSections, toggleSection, updateChapterProgress, handleChapterClick }: Props) => {
  const completedChapters = sectionProgress.chapters.filter((c: ChapterProgress) => c.completed).length || 0;
  const totalChapters = sectionProgress.chapters.length;
  const isExpanded = expandedSections.includes(sectionProgress.originalSection.title);

  return (
    <div className="min-w-[300px] max-w-[400px]">
      <div onClick={() => toggleSection(sectionProgress.originalSection.title)} className="cursor-pointer px-8 py-6 hover:bg-gray-700/50">
        <div className="flex justify-between items-center">
          <p className="text-gray-500 text-sm">Section 0{index + 1}</p>
          {isExpanded ? <ChevronUp className="text-white-50/70 w-4 h-4" /> : <ChevronDown className="text-white-50/70 w-4 h-4" />}
        </div>
        <h3 className="text-white-50/90 font-semibold">{sectionProgress.originalSection.title}</h3>
      </div>
      <hr className="border-gray-700" />

      {isExpanded && (
        <div className="pt-8 pb-8 bg-customgreys-primarybg/40">
          <ProgressVisuals sectionProgress={sectionProgress} completedChapters={completedChapters} totalChapters={totalChapters} />
          <ChaptersList
            sectionProgress={sectionProgress}
            currentChapterId={currentChapterId}
            handleChapterClick={handleChapterClick}
            updateChapterProgress={updateChapterProgress}
          />
        </div>
      )}
      <hr className="border-gray-700" />
    </div>
  );
};

export default SectionItem;
