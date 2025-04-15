import { Skeleton } from '@/components/ui/skeleton';

const EditUserFormLoading = () => {
  return (
    <div className="space-y-5">
      <div className="flex justify-center">
        <Skeleton className="h-24 w-24 rounded-full" />
      </div>
      <div>
        <Skeleton className="h-4 w-24 mb-2" />
        <Skeleton className="h-9 w-full" />
      </div>
      <div>
        <Skeleton className="h-4 w-24 mb-2" />
        <Skeleton className="h-9 w-full" />
      </div>
      <div>
        <Skeleton className="h-4 w-24 mb-2" />
        <Skeleton className="h-9 w-full" />
      </div>

      <div className="flex justify-between mt-2">
        <Skeleton className="h-9 w-40" />
        <Skeleton className="h-9 w-40" />
      </div>
    </div>
  );
};

export default EditUserFormLoading;
