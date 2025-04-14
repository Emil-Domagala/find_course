import { Skeleton } from '@/components/ui/skeleton';

const ConfirmEmailLoading = () => {
  return (
    <>
      <div className="flex gap-2">
        {Array.from({ length: 6 }).map((_, i) => (
          <Skeleton key={i} className="w-12 h-14" />
        ))}
      </div>
      <Skeleton className="w-full mt-2 h-9" />
    </>
  );
};

export default ConfirmEmailLoading;
