import { Skeleton } from '@/components/ui/skeleton';
import Image from 'next/image';
import Link from 'next/link';
import Tag from './Home/Tag';
import { centsToDollars } from '@/lib/utils';

export const CourseCardSearchSkeleton = () => {
  return <Skeleton className="h-[300px] rounded-lg"></Skeleton>;
};

const CourseCardSearch = ({ course }: { course: CourseDto }) => {
  return (
    <Link href={`course/${course.id}`}>
      <div
        className={`bg-customgreys-secondarybg overflow-hidden rounded-lg hover:bg-white-100/10 transition duration-200 flex flex-col cursor-pointer h-full group`}>
        <div className="relative w-auto pt-[56.25%]">
          <Image
            src={course.imageUrl || '/placeholder.png'}
            alt={course.title}
            fill
            sizes="(max-width: 768px) 100vw, (max-width: 1200px) 50vw, 33vw"
            className="object-cover transition-transform"
          />
        </div>
        <div className="p-4 flex flex-col justify-between flex-grow">
          <div className="flex justify-between">
            <Tag className="bg-customgreys-dirtyGrey/20 w-fit mb-2" size="small">
              {course.category}
            </Tag>
            <Tag className="bg-primary-700 w-fit mb-2" size="small">
              {course.level}
            </Tag>
          </div>
          <div>
            <h2 className="font-semibold line-clamp-1">{course.title}</h2>
            <p className="text-sm mt-1 line-clamp-2 max-h-[5rem] overflow-hidden">{course.description}</p>
          </div>
          <div className="mt-2">
            <p className="text-customgreys-dirtyGrey text-sm">
              By {course.teacher.username} {course.teacher.userLastname}
            </p>
            <div className="flex justify-between items-center mt-1">
              <span className="text-primary-500 font-semibold">${centsToDollars(course.price)}</span>
              <span className="text-customgreys-dirtyGrey text-sm">{course.studentsCount} Enrolled</span>
            </div>
          </div>
        </div>
      </div>
    </Link>
  );
};

export default CourseCardSearch;
