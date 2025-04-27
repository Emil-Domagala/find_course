import { CourseCategory, Level } from '@/types/courses-enum';
import * as z from 'zod';

// Course Editor Schemas
export const courseSchema = z.object({
  title: z.string().min(3, 'Title must be at least 3 characters').max(100, 'Title must be less than 100 characters'),
  description: z.string().min(3, 'Description must be at least 3 characters').max(1000, 'Description must be less than 1000 characters'),
  category: z.nativeEnum(CourseCategory),
  level: z.nativeEnum(Level),
  status: z.string(),
  price: z.number().positive('Price must be positive'),
  image: z.union([z.string(), z.instanceof(File)]).optional(),
});

export type CourseFormData = z.infer<typeof courseSchema>;

// Chapter Schemas
export const chapterSchema = z.object({
  title: z.string().min(3, 'Title must be at least 3 characters').max(50, 'Title must be less than 50 characters'),
  content: z.string().min(3, 'Content must be at least 3 characters').max(2000, 'Content must be less than 2000 characters'),
  videoUrl: z.union([z.string(), z.instanceof(File)]).optional(),
});

export type ChapterFormData = z.infer<typeof chapterSchema>;

// Section Schemas
export const sectionSchema = z.object({
  title: z.string().min(3, 'Title must be at least 3 characters').max(50, 'Title must be less than 50 characters'),
  description: z.string().min(3, 'Description must be at least 10 characters').max(500, 'Description must be less than 200 characters'),
  chapters: z.array(chapterSchema).optional(),
});

export type SectionFormData = z.infer<typeof sectionSchema>;
