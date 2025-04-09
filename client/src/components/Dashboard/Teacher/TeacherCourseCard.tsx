import React from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import Image from 'next/image';
import { cn, transformToFrontendFormat } from '@/lib/utils';
import { Button } from '@/components/ui/button';
import { Pencil, Trash2 } from 'lucide-react';
import { CourseStatus } from '@/types/courses-enum';
import Link from 'next/link';

type Props = {
  course: CourseDto;
  onDelete: (course: CourseDto) => void;
};

const TeacherCourseCard = ({ course, onDelete }: Props) => {
  return (
    <Card className="w-full h-[400px] p-0 bg-background border-none text-foreground bg-customgreys-primarybg overflow-hidden hover:bg-white-100/10 transition duration-200 flex flex-col group">
      <CardHeader className="p-0 h-[400px] overflow-hidden">
        <Image
          src={course.imageUrl || '/placeholder.png'}
          alt={course.title}
          width={370}
          height={150}
          className="rounded-t-lg w-[100%] h-[100%] object-cover transition-transform"
          priority
        />
      </CardHeader>

      <CardContent className="w-full pb-6 pt-4 flex-grow flex flex-col justify-between text-gray-400">
        <div className="flex flex-col">
          <CardTitle className="text-primary-50 text-md md:text-lg line-clamp-2 overflow-hidden">
            {course.title}
          </CardTitle>

          <CardDescription className="bg-customgreys-dirtyGrey/50 px-2 py-1 mt-3 mb-3 rounded-xl w-fit text-sm">
            {transformToFrontendFormat(course.category)}
          </CardDescription>

          <p className="text-sm mb-2">
            Status:{' '}
            <span
              className={cn(
                'font-semibold px-2 py-1 rounded',
                course.status === CourseStatus.PUBLISHED
                  ? 'bg-green-500/20 text-green-400'
                  : 'bg-red-500/20 text-red-400',
              )}>
              {transformToFrontendFormat(course.status)}
            </span>
          </p>

          <p className="ml-1 mt-1 inline-block text-secondary bg-secondary/10 text-sm font-normal">
            <span className="font-bold text-white-100">{course.studentsCount}</span> Student
            {course.studentsCount > 1 ? 's' : ''} Enrolled
          </p>
        </div>

        <div className=" xl:flex xl:justify-between space-y-2 xl:space-y-0 gap-2 mt-3">
          <Link href={`/teacher/courses/edit/${course.id}`} className="w-full">
            <Button className="rounded w-full bg-primary-700 border-none hover:bg-primary-600 hover:text-customgreys-primarybg text-white-100 cursor-pointer">
              <Pencil className="w-4 h-4 mr-2" />
              Edit
            </Button>
          </Link>

          <Button
            className="!shrink rounded w-full bg-red-600 text-white-100 hover:bg-red-400 hover:text-customgreys-primarybg cursor-pointer"
            onClick={() => onDelete(course)}>
            <Trash2 className="w-4 h-4 mr-2" />
            Delete
          </Button>
        </div>
      </CardContent>
    </Card>
  );
};

export default TeacherCourseCard;
