import { CourseCategory, CourseStatus, Level } from './courses-enum';
import { ChapterType } from './enums';

export type CartDto = {
  id: string;
  courses: CourseDto[];
  totalPrice: number;
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
