import { CourseCategory, CourseStatus, Level } from './courses-enum';

export type CartDto = {
  id: string;
  courses: CourseDto[];
  totalPrice: number;
};

export type ChapterDto = {
  id: string;
  title: string;
};

export type SectionDto = {
  id: string;
  title: string;
  description: string;
  chapter?: ChapterDto[];
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

  type CourseDetailsPublicDto = {
    courseDto: CourseDto;
    sections?: SectionDto[];
  };
}

export {};
