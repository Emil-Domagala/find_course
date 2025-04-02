import { CourseCategory, CourseStatus, Level } from './courses-enum';


declare global {

 type SectionDto = {
  id: string; 
  title: string;
  description: string;
};

// Courses

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
  studentsCount:number;
  createdAt: string; 
  updatedAt: string;
}

type CourseDetailsPublicDto ={
  courseDto:CourseDto
  sections?: SectionDto[];
};


}

export {}