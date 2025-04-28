import { ChevronDown, ChevronUp } from 'lucide-react';
import ProgressVisuals from './ProgresVisuals';
import ChaptersList from './ChaptersList';

type Props = {
  section: any;
  index: number;
  sectionProgress: any;
  chapterId: string;
  expandedSections: string[];
  toggleSection: (sectionTitle: string) => void;
  handleChapterClick: (sectionId: string, chapterId: string) => void;
  updateChapterProgress: (sectionId: string, chapterId: string, completed: boolean) => void;
};

const SectionItem = ({ section, index, sectionProgress, chapterId, expandedSections, toggleSection, handleChapterClick, updateChapterProgress }: Props) => {
  const completedChapters = sectionProgress?.chapters.filter((c: any) => c.completed).length || 0;
  const totalChapters = section.chapters.length;
  const isExpanded = expandedSections.includes(section.sectionTitle);

  return (
    <div className="min-w-[300px]">
      <div onClick={() => toggleSection(section.sectionTitle)} className="cursor-pointer px-8 py-6 hover:bg-gray-700/50">
        <div className="flex justify-between items-center">
          <p className="text-gray-500 text-sm">Section 0{index + 1}</p>
          {isExpanded ? <ChevronUp className="text-white-50/70 w-4 h-4" /> : <ChevronDown className="text-white-50/70 w-4 h-4" />}
        </div>
        <h3 className="text-white-50/90 font-semibold">{section.sectionTitle}</h3>
      </div>
      <hr className="border-gray-700" />

      {isExpanded && (
        <div className="pt-8 pb-8 bg-customgreys-primarybg/40">
          <ProgressVisuals section={section} sectionProgress={sectionProgress} completedChapters={completedChapters} totalChapters={totalChapters} />
          <ChaptersList
            section={section}
            sectionProgress={sectionProgress}
            chapterId={chapterId}
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
