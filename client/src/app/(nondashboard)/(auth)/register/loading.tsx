import { AuthFieldSkeleton } from '@/components/NonDashboard/auth/AuthField';
import { AuthFooterSkeleton } from '@/components/NonDashboard/auth/AuthFooter';
import { AuthHeaderSkeleton } from '@/components/NonDashboard/auth/AuthHeader';
import { Skeleton } from '@/components/ui/skeleton';

const RegisterLoading = () => {
  return (
    <div className="flex justify-center items-center  mt-10 ">
      <div className="rounded-xl flex flex-col  sm:min-w-[32rem] sm:mx-auto shadow-none mx-4 bg-customgreys-secondarybg border-none px-6 py-10 gap-0">
        <AuthHeaderSkeleton />
        <div className="flex flex-row gap-6">
          <AuthFieldSkeleton />
          <AuthFieldSkeleton />
        </div>
        <AuthFieldSkeleton />
        <AuthFieldSkeleton />
        <Skeleton className="w-full mt-2 h-9" />
        <AuthFooterSkeleton />
      </div>
    </div>
  );
};

export default RegisterLoading;
