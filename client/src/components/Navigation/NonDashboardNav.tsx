import { BookOpen } from 'lucide-react';
import Link from 'next/link';

const NonDashboardNav = () => {
  return (
    <nav className="w-full bg-customgreys-primarybg">
      <div className="flex container justify-between items-center py-8 mx-auto">
        {/* LEFT */}
        <div className="flex gap-10 md:gap-20 items-center ">
          <Link href={'/'} className="font-bold text-lg sm:text-xl hover:text-customgreys-dirtyGrey duration-300">
            Find Course
          </Link>
          <div className="flex items-center gap-4">
            <Link
              href={'/search'}
              className="bg-customgreys-secondarybg items-center flex flex-row gap-2  px-5 sm:px-14 py-3 sm:py-4 rounded-xl text-customgreys-dirtyGrey hover:text-white-50 hover:bg-customgreys-darkerGrey transition-all duration-300 text-sm sm:text-base">
              <BookOpen size={18} className="left-1 sm:left-5 transform top-1/2" />
              <span className="hidden sm:inline">Search Courses</span>
              <span className="sm:hidden">Search</span>
            </Link>
          </div>
        </div>

        {/*  Auth buttons*/}
        <button>Auth button</button>
      </div>
    </nav>
  );
};

export default NonDashboardNav;
