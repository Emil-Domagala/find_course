export type CourseDetailsProtectedDto = CourseDto & {
  sections?: SectionDetailsProtectedDto[];
};

export type SectionDetailsProtectedDto = SectionDto & {
  chapters?: ChapterDetailsProtectedDto[];
};

export type ChapterDetailsProtectedDto = ChapterDto & {
  content?: string;
  videoUrl?: string;
};
