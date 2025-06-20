import Tag from '@/components/NonDashboard/Home/Tag';
import { Button } from '@/components/ui/button';
import { centsToDollars } from '@/lib/utils';
import { useRemoveCourseFromCartMutation } from '@/state/endpoints/cart/cart';

import Image from 'next/image';
import Link from 'next/link';

const CartItem = ({ course }: { course: CourseDto }) => {
  const [removeCourseFromCart] = useRemoveCourseFromCartMutation();

  return (
    <li className="flex flex-col md:flex-row justify-between min-h-[10rem] p-4 border-b-2 first-of-type:border-t-2 border-customgreys-secondarybg">
      <Link
        href={`/course/${course.id}`}
        scroll={false}
        className="hover:bg-customgreys-secondarybg rounded-lg mb-2 md:mb-0 duration-300 transition-colors">
        <div className="flex flex-col md:flex-row">
          <div className="relative aspect-video min-w-[14rem] mb-2 md:mb-0">
            <Image
              src={course.imageUrl || '/placeholder.png'}
              alt={course.title}
              fill
              sizes="(max-width: 768px) 100vw, (max-width: 1200px) 50vw, 33vw"
              className="object-cover transition-transform aspect-video"
            />
          </div>
          <div className="flex flex-col justify-between w-full md:max-w-[20rem] p-4">
            <h2 className="font-semibold line-clamp-1 text-lg mb-2">{course.title}</h2>
            <div className="flex gap-2 w-full justify-between md:justify-normal">
              <Tag className="bg-customgreys-dirtyGrey/20 w-fit text-sm" size="small">
                {course.category}
              </Tag>
              <Tag className="bg-primary-700 w-fit text-sm " size="small">
                {course.level}
              </Tag>
            </div>

            <div className="mt-2">
              <p className="text-sm line-clamp-2  max-h-[4rem] overflow-hidden">{course.description}</p>
              <p className="text-customgreys-dirtyGrey text-sm">
                By {course.teacher.username} {course.teacher.userLastname}
              </p>
            </div>
          </div>
        </div>
      </Link>

      <div className="flex items-center gap-[6rem] h-full justify-between md:justify-normal  px-4 md:px-0">
        <div className="h-full">
          <p className="text-primary-500 font-semibold text-xl">${centsToDollars(course.price)}</p>
        </div>
        <Button variant="warning" onClick={() => removeCourseFromCart({ courseId: course.id })}>
          Remove
        </Button>
      </div>
    </li>
  );
};

export default CartItem;
