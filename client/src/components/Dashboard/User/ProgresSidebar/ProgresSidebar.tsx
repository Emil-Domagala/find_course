'use client';

import { useEffect, useRef, useState } from 'react';
import SectionItem from './SectionItem';

import { useSidebar } from '@/components/ui/sidebar';
import { useRouter } from 'next/navigation';
import { useCourseProgressData } from '@/hooks/useCourseProgressData';

type Props = {};

const ProgresSidebar = ({}: Props) => {
  const router = useRouter();
  const { setOpen } = useSidebar();
  const [expandedSections, setExpandedSections] = useState<string[]>([]);

  const { course, userProgress, chapterId, courseId, isLoading, updateChapterProgress } = useCourseProgressData();

  const sidebarRef = useRef<HTMLDivElement>(null);
  useEffect(() => {
    setOpen(false);
  }, []); // eslint-disable-line react-hooks/exhaustive-deps

  if (isLoading) return <h1>Loading...</h1>;

  const toggleSection = (sectionTitle: string) => {
    setExpandedSections((prevSections) => (prevSections.includes(sectionTitle) ? prevSections.filter((title) => title !== sectionTitle) : [...prevSections, sectionTitle]));
  };

  const handleChapterClick = (sectionId: string, chapterId: string) => {
    router.push(`/user/course/${courseId}/chapters/${chapterId}`, {
      scroll: false,
    });
  };

  return (
    <div
      ref={sidebarRef}
      className="bg-customgreys-secondarybg border-x border-gray-700 overflow-y-auto transition-all duration-500 ease-in-out animate-in fade-in slide-in-from-left flex-shrink-0">
      <div>
        <h2 className="text-lg font-bold pt-9 pb-6 px-8">Title</h2>
        <hr className="border-gray-700" />
        {course.sections.map((section, index) => (
          <SectionItem
            key={section.sectionId}
            section={section}
            index={index}
            sectionProgress={userProgress.sections.find((s) => s.sectionId === section.sectionId)}
            chapterId={chapterId as string}
            expandedSections={expandedSections}
            toggleSection={toggleSection}
            handleChapterClick={handleChapterClick}
            updateChapterProgress={updateChapterProgress}
          />
        ))}
      </div>
    </div>
  );
};

export default ProgresSidebar;
