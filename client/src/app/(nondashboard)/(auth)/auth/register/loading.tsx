import { AuthFieldSkeleton } from '@/components/NonDashboard/auth/AuthField';
import { Skeleton } from '@/components/ui/skeleton';

const RegisterLoading = () => {
  return (
    <>
      <div className="flex flex-row gap-6">
        <AuthFieldSkeleton />
        <AuthFieldSkeleton />
      </div>
      <AuthFieldSkeleton />
      <AuthFieldSkeleton />
      <Skeleton className="w-full mt-2 h-9" />
    </>
  );
};

export default RegisterLoading;
