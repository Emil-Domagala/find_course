import { v4 as uuid } from 'uuid';
import { ChapterDto, CourseDetailsPublicDto, SectionDetailsPublicDto } from '@/types/courses';
import { CourseCategory, CourseStatus, Level } from '@/types/courses-enum';
import { ChapterType } from '@/types/search-enums';

export function createChapter(overrides: Partial<ChapterDto> = {}): ChapterDto {
  return {
    id: uuid(),
    title: 'Sample Chapter',
    type: ChapterType.TEXT,
    ...overrides,
  };
}

export function createSection(overrides: Partial<SectionDetailsPublicDto> = {}): SectionDetailsPublicDto {
  return {
    id: uuid(),
    title: 'Sample Section',
    description: 'Section description',
    chapters: [createChapter(), createChapter({ type: ChapterType.VIDEO })],
    ...overrides,
  };
}

export function createCourse(overrides: Partial<CourseDetailsPublicDto> = {}): CourseDetailsPublicDto {
  return {
    id: uuid(),
    teacher: {
      id: uuid(),
      username: 'Jane',
      userLastname: 'Smith',
      email: 'jane@example.com',
      imageUrl: '/test-teacher.jpg',
    },
    title: 'Sample Course Title',
    description: 'A test course description',
    category: CourseCategory.GAME_DEVELOPMENT,
    imageUrl: '/test-course.jpg',
    price: 2499,
    level: Level.BEGINNER,
    status: CourseStatus.PUBLISHED,
    studentsCount: 100,
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString(),
    sections: [createSection(), createSection()],
    ...overrides,
  };
}
