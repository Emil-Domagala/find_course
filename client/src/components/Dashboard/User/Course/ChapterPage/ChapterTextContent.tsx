import { ChapterDetailsProtectedDto } from '@/types/courses';

type Props = { chapterData?: ChapterDetailsProtectedDto };

const ChapterTextContent = ({ chapterData }: Props) => {
  return (
    <div className="bg-customgreys-secondarybg w-full h-full p-6">
      <p>{chapterData?.content}</p>
    </div>
  );
};

export default ChapterTextContent;
