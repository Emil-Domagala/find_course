import { AuthFieldSkeleton } from '@/features/nondashboard/auth/components/AuthField';
import { Skeleton } from '@/components/ui/skeleton';

const LoginLoading = () => {
  return (
    <>
      <AuthFieldSkeleton />
      <AuthFieldSkeleton />
      <Skeleton className="w-full mt-2 h-9" />
    </>
  );
};

export default LoginLoading;
