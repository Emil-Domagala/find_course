export type UpdateProgressRequest = {
  chapterProgressId: string;
  completed: boolean;
};

export type CourseProgress = {
  id: string;
  course: {
    id: string;
    title: string;
  };
  createdAt: string;
  updatedAt: string;
  overallProgress: number;
  sections: SectionProgress[];
};

export type SectionProgress = {
  id: string;
  originalSection: {
    id: string;
    title: string;
  };
  chapters: ChapterProgress[];
};

export type ChapterProgress = {
  id: string;
  originalChapter: {
    id: string;
    title: string;
    type: ChapterType;
  };
  completed: boolean;
};
