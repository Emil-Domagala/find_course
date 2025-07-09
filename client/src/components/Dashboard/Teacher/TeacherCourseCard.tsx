import React from 'react';
import Image from 'next/image';
import { cn, transformToFrontendFormat } from '@/lib/utils';
import { Button } from '@/components/ui/button';
import { Pencil, Trash2 } from 'lucide-react';
import { CourseStatus } from '@/types/courses-enum';
import Link from 'next/link';
import Tag from '@/components/NonDashboard/Home/Tag';

type Props = {
  course: CourseDto;
  onDelete: (course: CourseDto) => void;
};

const TeacherCourseCard = ({ course, onDelete }: Props) => {
  return (
    <div className="w-full min-h-[300px] rounded-lg p-0 bg-background border-none text-foreground bg-customgreys-primarybg overflow-hidden hover:bg-white-100/10 transition duration-200 flex flex-col group">
      <div className="relative w-full">
        <Image src={course.imageUrl || '/placeholder.png'} alt={course.title} width={0} height={0} sizes="100vw" className="w-full h-auto" />
      </div>

      <div className="w-full p-4 pb-6 flex-grow flex flex-col justify-between text-gray-400">
        <div className="flex flex-col">
          <h2 className="font-semibold text-primary-50 text-md md:text-lg line-clamp-2 overflow-hidden">{course.title}</h2>

          <Tag className="bg-customgreys-dirtyGrey/20 w-fit my-2" size="small">
            {course.category}
          </Tag>

          <p className="text-sm my-2">
            Status:{' '}
            <span
              className={cn(
                'font-semibold px-2 py-1 rounded',
                course.status === CourseStatus.PUBLISHED ? 'bg-green-500/20 text-green-400' : 'bg-red-500/20 text-red-400',
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
          <Link aria-label={`Edit ${course.title}`} href={`/teacher/courses/edit/${course.id}`} className="w-full">
            <Button variant="primary" className="rounded w-full text-white-100 ">
              <Pencil className="w-4 h-4 mr-2" />
              Edit
            </Button>
          </Link>

          <Button aria-label={`Delete ${course.title}`} variant="warning" className="!shrink rounded w-full " onClick={() => onDelete(course)}>
            <Trash2 className="w-4 h-4 mr-2" />
            Delete
          </Button>
        </div>
      </div>
    </div>
  );
};

export default TeacherCourseCard;
