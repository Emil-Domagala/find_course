import { Skeleton } from '@/components/ui/skeleton';

export const FilterSkeleton = () => {
  return (
    <div className="w-full bg-customgreys-secondarybg py-5">
      <div className="container max-w-[85rem]">
        <div className="flex flex-col md:flex-row gap-5 justify-center items-end w-full">
          {/* Category & Search */}
          <div className="flex flex-row gap-4 w-full md:w-auto flex-1">
            <div className="flex flex-col">
              <Skeleton className="w-24 h-6 mb-2" />
              <Skeleton className="w-32 h-12" />
            </div>
            <div className="flex flex-col w-full">
              <Skeleton className="w-40 h-6 mb-2" />
              <Skeleton className="w-full h-12" />
            </div>
          </div>

          {/* Filters */}
          <div className="flex flex-row gap-4 w-full justify-between md:justify-center items-end md:w-fit">
            <div className="flex flex-col">
              <Skeleton className="w-20 h-6 mb-2" />
              <Skeleton className="w-24 h-12" />
            </div>
            <div className="flex flex-col">
              <Skeleton className="w-14 h-6 mb-2" />
              <Skeleton className="w-14 h-12" />
            </div>
            <div className="flex flex-col">
              <Skeleton className="w-14 h-6 mb-2" />
              <Skeleton className="w-14 h-12" />
            </div>
            <Skeleton className="w-24 h-12" />
          </div>
        </div>
      </div>
    </div>
  );
};
