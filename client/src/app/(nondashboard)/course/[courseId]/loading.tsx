import { TagSkeleton } from '@/components/NonDashboard/Home/Tag';
import { Skeleton } from '@/components/ui/skeleton';

const CourseDetailLoading = () => {
  return (
    <>
      {/* Top Section */}
      <div className="md:bg-customgreys-secondarybg">
        <div className="container max-w-[1200px] flex flex-col-reverse md:flex-row gap-8 h-auto md:h-[500px] py-6">
          {/* Info Skeleton */}
          <div className="flex flex-col basis-1/2 ">
            <Skeleton className="h-8 w-3/4 mb-9" /> {/* Title */}
            <Skeleton className="h-4 w-full mb-3" /> {/* Description line 1 */}
            <Skeleton className="h-4 w-5/6 mb-3" /> {/* Description line 2 */}
            <Skeleton className="h-4 w-11/12 mb-6" /> {/* Description line 3 */}
            <Skeleton className="h-3 w-1/3 mb-2" /> {/* Created by */}
            <Skeleton className="h-3 w-1/4 mb-3" /> {/* Enrollments */}
            <TagSkeleton />
            {/* BUY Skeleton */}
            <Skeleton className="h-7 w-28 my-5" /> {/* Price */}
            <Skeleton className="h-10 w-24 rounded-md" /> {/* Buy Now Button */}
          </div>

          {/* Image Skeleton */}
          <div className="relative h-64 w-full md:h-full aspect-video overflow-hidden rounded-lg">
            <Skeleton className="h-full w-full" />
          </div>
        </div>
      </div>
    </>
  );
};

export default CourseDetailLoading;
