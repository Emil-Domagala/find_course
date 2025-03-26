import { Skeleton } from '../../ui/skeleton';

export const TagSkeleton = () => {
  return <Skeleton className="w-24 h-6 rounded-full"></Skeleton>;
};

const Tag = ({ children }: { children: React.ReactNode }) => {
  return <div className="px-3 py-1 bg-customgreys-secondarybg rounded-full text-sm">{children}</div>;
};

export default Tag;
