import { Skeleton } from '@/components/ui/skeleton';

const WatchCourseLoading = () => {
  return (
    <div className="flex h-full">
      <div className="flex-grow mx-auto">
        <Skeleton className="h-7 w-[10rem] my-4" />
        <Skeleton className="h-[50vh] w-full" />
      </div>
    </div>
  );
};

export default WatchCourseLoading;
