export type CartResponse = {
  cart?: CartDto;
  warnings?: string[];
};

export type CartDto = {
  id: string;
  courses: CourseDto[];
  totalPrice: number;
};
