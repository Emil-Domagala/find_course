import { CourseCategory, CourseStatus, Level } from './courses-enum';
import { ChapterType } from './search-enums';

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
