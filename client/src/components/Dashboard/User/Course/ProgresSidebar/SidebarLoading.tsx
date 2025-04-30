import { Skeleton } from '@/components/ui/skeleton';
import { ChevronDown } from 'lucide-react';

export const SectionItemLoading = ({ item }: { item: number }) => {
  return (
    <>
      <div className="py-6 px-8">
        <div className="flex justify-between items-center">
          <p className="text-gray-500 text-sm">Section 0{item}</p>
          <ChevronDown className="text-white-50/70 w-4 h-4" />
        </div>
        <Skeleton className="h-5 w-[150px]" />
      </div>
      <hr className="border-gray-700" />
    </>
  );
};


export const SidebarLoading = () => {
  return (
    <>
      {/* Header */}
      <div className="pt-9 pb-6 px-8 w-[300px]">
        <Skeleton className="h-7 w-full font-bold " />
      </div>
      <hr className="border-gray-700" />
      {[0, 1, 2, 3, 4].map((item) => {
        return <SectionItemLoading key={item} item={item} />;
      })}
    </>
  );
};
