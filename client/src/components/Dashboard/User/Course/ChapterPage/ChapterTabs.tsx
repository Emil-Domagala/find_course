import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { ChapterDetailsProtectedDto } from '@/types/courses';

type Props = { chapterData?: ChapterDetailsProtectedDto };

const TabsTriggerClass = `relative text-md rounded-none transition-colors duration-300 
             data-[state=active]:text-primary-750 
             before:content-[''] before:absolute before:bottom-0 before:left-1/2 before:-translate-x-1/2 
             before:w-full before:h-[2px] before:bg-primary-750 
             before:origin-center before:scale-x-0 
             before:transition-transform before:duration-300 
             data-[state=active]:before:scale-x-100
             !w-fit
             `;

const ChapterTabs = ({ chapterData }: Props) => {
  return (
    <div className="flex gap-4 mt-12">
      <Tabs defaultValue="Notes" className="w-full md:w-2/3">
        <TabsList className="flex justify-start gap-10">
          <TabsTrigger className={TabsTriggerClass} value="Notes">
            Notes
          </TabsTrigger>
          <TabsTrigger className={TabsTriggerClass} value="chapterContent">
            Chapter Content
          </TabsTrigger>
        </TabsList>

        <TabsContent className="mt-5" value="Notes">
          <div className="!border-none shadow-none">
            <div className="p-2">
              <h3>Notes Content</h3>
            </div>
            <div className="p-2">In the future i will add possibility of adding notes here</div>
          </div>
        </TabsContent>

        <TabsContent className="mt-5" value="chapterContent">
          <div className="!border-none shadow-none">
            <div className="p-2">
              <div>Chapter Content</div>
            </div>
            <div className="p-2">{chapterData?.content}</div>
          </div>
        </TabsContent>
      </Tabs>
    </div>
  );
};

export default ChapterTabs;
