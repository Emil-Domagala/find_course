import { AuthFieldSkeleton } from '@/components/NonDashboard/auth/AuthField';
import { Skeleton } from '@/components/ui/skeleton';

const SendResetPasswordLoading = () => {
  return (
    <>
      <AuthFieldSkeleton />
      <Skeleton className="w-full mt-2 h-9" />
    </>
  );
};

export default SendResetPasswordLoading;
