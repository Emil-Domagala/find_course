import { v4 as uuid } from 'uuid';
import {
  ChapterDto,
  CourseDetailsPublicDto,
  SectionDetailsPublicDto,
  SectionDto,
} from '@/types/courses';
import { CourseCategory, CourseStatus, Level } from '@/types/courses-enum';
import { ChapterType } from '@/types/search-enums';
import { createUserDto } from './userFactory';
import { ChapterDetailsProtectedDto } from '@/features/dashboard/user/watchChapter/coursesProtected';
import { CourseDtoWithFirstChapter } from '@/features/dashboard/user/courses/courseWithFirstChapter';

// CHAPTERS

export function createChapterDto(overrides: Partial<ChapterDto> = {}): ChapterDto {
  const genUUID = uuid();
  return {
    id: genUUID,
    title: genUUID.substring(0, 10),
    type: ChapterType.TEXT,
    ...overrides,
  };
}

export function createChapterProtectedDto({
  chapterDto = createChapterDto(),
  videoUrl,
  content,
}: { chapterDto?: ChapterDto; videoUrl?: string; content?: string } = {}): ChapterDetailsProtectedDto {
  if (!videoUrl && chapterDto.type === ChapterType.TEXT) {
    return {
      ...chapterDto,
      content: content ?? 'Some random content',
    };
  }

  return {
    ...chapterDto,
    type: ChapterType.VIDEO,
    videoUrl: videoUrl ?? 'https://www.youtube.com/watch?v=dQw4w9WgXcQ',
    ...(content && { content }),
  };
}

// Sections

export function createSectionDto(overrides: Partial<SectionDto> = {}): SectionDto {
  const genUUID = uuid();

  return {
    id: genUUID,
    title: genUUID.substring(0, 10),
    description: genUUID.substring(0, 20),
    ...overrides,
  };
}

export function createPublicDtoSection({
  sectionDto = createSectionDto(),
  chapters = [createChapterDto(), createChapterDto({ type: ChapterType.VIDEO })],
}: {
  sectionDto?: SectionDto;
  chapters?: ChapterDto[];
} = {}): SectionDetailsPublicDto {
  return {
    ...sectionDto,
    chapters,
  };
}

export function createPrivateDtoSection({
  sectionDto = createSectionDto(),
  chapters = [createChapterProtectedDto(), createChapterProtectedDto({ chapterDto: createChapterDto({ type: ChapterType.VIDEO }) })],
}: {
  sectionDto?: SectionDto;
  chapters?: ChapterDetailsProtectedDto[];
} = {}): SectionDetailsPublicDto {
  return {
    ...sectionDto,
    chapters,
  };
}

//  COURSES
export function createCourseDto(overrides: Partial<CourseDto> = {}): CourseDto {
  const genUUID = uuid();

  return {
    id: genUUID,
    teacher: createUserDto(),
    title: genUUID.substring(0, 10),
    description: genUUID.substring(0, 20),
    category: CourseCategory.GAME_DEVELOPMENT,
    imageUrl: '/test-course.jpg',
    price: 2499,
    level: Level.BEGINNER,
    status: CourseStatus.PUBLISHED,
    studentsCount: 100,
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString(),
    ...overrides,
  };
}

export function createCoursePublicDto({
  courseDto = createCourseDto(),
  sections = [createPublicDtoSection(), createPublicDtoSection()],
}: {
  courseDto?: CourseDto;
  sections?: SectionDetailsPublicDto[];
} = {}): CourseDetailsPublicDto {
  return {
    ...courseDto,
    sections,
  };
}

export function createCoursesWithFirstChapter({
  courseDto = createCourseDto(),
  firstChapter = 'First-Chapter',
}: {
  courseDto?: CourseDto;
  firstChapter?: string;
} = {}): CourseDtoWithFirstChapter {
  return {
    ...courseDto,
    firstChapter,
  };
}
