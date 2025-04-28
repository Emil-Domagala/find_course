import ChapterItem from './ChapterItem';

type Props = {
  section: any;
  sectionProgress: any;
  chapterId: string;
  handleChapterClick: (sectionId: string, chapterId: string) => void;
  updateChapterProgress: (sectionId: string, chapterId: string, completed: boolean) => void;
};

const ChaptersList = ({ section, sectionProgress, chapterId, handleChapterClick, updateChapterProgress }: Props) => {
  return (
    <ul>
      {section.chapters.map((chapter: any, index: number) => (
        <ChapterItem
          key={chapter.chapterId}
          chapter={chapter}
          index={index}
          sectionId={section.sectionId}
          sectionProgress={sectionProgress}
          chapterId={chapterId}
          handleChapterClick={handleChapterClick}
          updateChapterProgress={updateChapterProgress}
        />
      ))}
    </ul>
  );
};

export default ChaptersList;
