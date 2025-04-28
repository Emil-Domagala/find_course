import { Skeleton } from '@/components/ui/skeleton';
import Image from 'next/image';
import Link from 'next/link';
import Tag from '../NonDashboard/Home/Tag';
import { cn, formatPrice } from '@/lib/utils';
import { Avatar, AvatarFallback, AvatarImage } from '@radix-ui/react-avatar';

export const CourseCardSkeleton = () => {
  return <Skeleton className="h-[300px] rounded-lg"></Skeleton>;
};

const CourseCard = ({ course, isSearch, link, cardClasses }: { course: CourseDto; isSearch?: boolean; link: string; cardClasses?: string }) => {
  return (
    <Link href={link}>
      <div
        className={cn(
          `bg-customgreys-secondarybg overflow-hidden rounded-lg hover:bg-white-100/10 transition duration-200 flex flex-col cursor-pointer h-full group`,
          cardClasses,
        )}>
        <div className="relative w-full">
          <Image src={course.imageUrl || '/placeholder.png'} alt={course.title} width={0} height={0} sizes="100vw" className="w-full h-auto" />
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
          <div className="mt-2 flex gap-2 items-center">
            <Avatar className="size-6 rounded-full overflow-hidden">
              <AvatarImage alt={course.teacher.username} src={course.teacher.imageUrl || '/Profile_avatar_placeholder.png'} />
              <AvatarFallback>
                <Skeleton className="size-6 rounded-full bg-customgreys-darkerGrey" />
              </AvatarFallback>
            </Avatar>
            <p className="text-customgreys-dirtyGrey text-sm">
              {course.teacher.username} {course.teacher.userLastname}
            </p>
          </div>
          {isSearch && (
            <div className="flex justify-between items-center mt-1">
              <span className="text-customgreys-dirtyGrey text-sm">{course.studentsCount} Enrolled</span>
              <span className="text-primary-500 font-semibold text-md">{formatPrice(course.price)}</span>
            </div>
          )}
        </div>
      </div>
    </Link>
  );
};

export default CourseCard;
