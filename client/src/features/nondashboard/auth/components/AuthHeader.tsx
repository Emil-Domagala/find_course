import { Skeleton } from '@/components/ui/skeleton';

export const AuthHeaderSkeleton = () => {
  return (
    <div className="flex flex-col items-center justify-center mb-10">
      <Skeleton className="mb-4 h-7 md:h-8 w-60"></Skeleton>
      <Skeleton className="h-4 w-80 mx-8"></Skeleton>
    </div>
  );
};

const AuthHeader = ({ header, description }: { header: string; description: string }) => {
  return (
    <div className="mb-10">
      <h1 className="align-middle font-bold text-center text-xl md:text-2xl mb-4">{header}</h1>
      <p className="text-md text-stone-400 text-center">{description}</p>
    </div>
  );
};

export default AuthHeader;
