import { CourseCategory, CourseStatus, Level } from './courses-enum';
import { ChapterType } from './enums';

export type UpdateProgressRequest = {
  chapterProgressId: string;
  completed: boolean;
};

export type CourseProgress = {
  id: string;
  course: CourseStructure;
  createdAt: string;
  updatedAt: string;
  overallProgress: number;
  sections: SectionProgress[];
};

export type SectionProgress = {
  id: string;
  originalSection: SectionStructure;
  chapters: ChapterProgress[];
};

export type ChapterProgress = {
  id: string;
  originalChapter: ChapterStructure;
  completed: boolean;
};

export type CourseStructure = {
  id: string;
  title: string;
};

export type SectionStructure = {
  id: string;
  title: string;
};

export type ChapterStructure = {
  id: string;
  title: string;
  type: ChapterType;
};

export type ChapterDto = {
  id: string;
  title: string;
  type: ChapterType;
};

export type SectionDto = {
  id: string;
  title: string;
  description: string;
};

export type CourseDetailsPublicDto = CourseDto & {
  sections?: SectionDto[];
};
export type SectionDetailsPublicDto = SectionDto & {
  chapters?: ChapterDto[];
};

export type CourseDetailsProtectedDto = CourseDto & {
  sections?: SectionDetailsProtectedDto[];
};

export type SectionDetailsProtectedDto = SectionDto & {
  chapters?: ChapterDetailsProtectedDto[];
};

export type ChapterDetailsProtectedDto = ChapterDto & {
  content: string;
  videoUrl?: string;
};

export type CourseDtoWithFirstChapter = CourseDto & {
  firstChapter: string;
};

declare global {
  type CourseDto = {
    id: string;
    teacher: UserDto;
    title: string;
    description: string;
    category: CourseCategory;
    imageUrl: string;
    price: number;
    level: Level;
    status: CourseStatus;
    studentsCount: number;
    createdAt: string;
    updatedAt: string;
  };
}

export {};
