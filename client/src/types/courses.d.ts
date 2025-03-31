declare global {
enum Level {
  BEGINNER = "BEGINNER",
  INTERMEDIATE = "INTERMEDIATE",
  ADVANCED = "ADVANCED",
}

 enum CourseStatus {
  DRAFT = "DRAFT",
  PUBLISHED = "PUBLISHED",
}



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
  category: string;
  imageUrl: string;
  price: number;
  level: Level;
  status: CourseStatus;
  createdAt: string; 
  updatedAt: string;
}

type CourseDetailsPublicDto = CourseDto & {
  sections?: SectionDto[];
};


}

export {}