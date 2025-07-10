'use client';

import { useEffect, useRef, useState } from 'react';
import SectionItem from './SectionItem';

import { useSidebar } from '@/components/ui/sidebar';
import { useRouter } from 'next/navigation';
import { useChapterAndCourseSidebarData } from '@/features/dashboard/user/watchChapter/hooks/useChapterAndCourseSidebarData';
import { SidebarLoading } from './SidebarLoading';

const ProgresSidebar = () => {
  const router = useRouter();
  const { setOpen } = useSidebar();
  const [expandedSections, setExpandedSections] = useState<string[]>([]);

  const { courseId, chapterId, courseProgressData, courseProgressIsLoading, handleUpdateChapterProgress } = useChapterAndCourseSidebarData();

  const sidebarRef = useRef<HTMLDivElement>(null);
  useEffect(() => {
    setOpen(false);
  }, []); // eslint-disable-line react-hooks/exhaustive-deps

  const toggleSection = (sectionTitle: string) => {
    setExpandedSections((prevSections) =>
      prevSections.includes(sectionTitle) ? prevSections.filter((title) => title !== sectionTitle) : [...prevSections, sectionTitle],
    );
  };

  const handleChapterClick = (chapterId: string) => {
    router.push(`/user/course/${courseId}/chapter/${chapterId}`, {
      scroll: false,
    });
  };

  return (
    <div
      ref={sidebarRef}
      className="bg-customgreys-secondarybg border-x border-gray-700 overflow-y-auto transition-all duration-500 ease-in-out animate-in fade-in slide-in-from-left flex-shrink-0">
      {courseProgressIsLoading ? (
        <SidebarLoading />
      ) : (
        <div>
          <h2 className="text-lg font-bold pt-9 pb-6 px-8">{courseProgressData?.course.title}</h2>
          <hr className="border-gray-700" />
          {courseProgressData?.sections.map((section, index) => (
            <SectionItem
              currentChapterId={chapterId as string}
              key={section.id}
              sectionProgress={section}
              index={index}
              expandedSections={expandedSections}
              toggleSection={toggleSection}
              handleChapterClick={handleChapterClick}
              updateChapterProgress={handleUpdateChapterProgress}
            />
          ))}
        </div>
      )}
    </div>
  );
};

export default ProgresSidebar;
