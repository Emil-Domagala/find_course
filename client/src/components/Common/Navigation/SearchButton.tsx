'use client';

import { cn } from '@/lib/utils';
import { BookOpen } from 'lucide-react';
import Link from 'next/link';
import { usePathname } from 'next/navigation';

const SearchButton = ({ className }: { className?: string }) => {
  const pathname = usePathname();

  const viewCoursePageClass = pathname.toString().match('^/user/course/([0-9a-fA-F\-]{36})/chapter/([0-9a-fA-F\-]{36})$');

  className = cn(`${viewCoursePageClass && 'bg-customgreys-secondarybg'}`);

  return (
    <>
      {pathname != '/search' && (
        <div className="flex items-center gap-4">
          <Link
            href={'/search'}
            className={cn(
              'bg-customgreys-secondarybg items-center flex flex-row gap-2  px-5 sm:px-14 py-3 rounded-xl text-customgreys-dirtyGrey hover:text-white-50 hover:bg-customgreys-darkerGrey transition-all duration-300 text-sm sm:text-base',
              className,
            )}>
            <BookOpen size={18} className="left-1 sm:left-5 transform top-1/2" />
            <span className="hidden sm:inline">Search Courses</span>
            <span className="sm:hidden">Search</span>
          </Link>
        </div>
      )}
    </>
  );
};

export default SearchButton;
