import { AuthFieldSkeleton } from '@/features/nondashboard/auth/components/AuthField';
import { Skeleton } from '@/components/ui/skeleton';

const ResetPasswordLoading = () => {
  return (
    <>
      <AuthFieldSkeleton />
      <AuthFieldSkeleton />
      <Skeleton className="w-full mt-2 h-9" />
    </>
  );
};

export default ResetPasswordLoading;
