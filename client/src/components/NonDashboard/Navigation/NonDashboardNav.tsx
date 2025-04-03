import { BookOpen } from 'lucide-react';
import Link from 'next/link';

const buttonsBasic = `text-md py-2 px-4 rounded-lg text-white-50 font-semibold duration-300 transition-colors`;

const NonDashboardNav = () => {
  return (
    <nav className="w-full bg-customgreys-primarybg">
      <div className="flex container justify-between items-center py-8 mx-auto">
        {/* LEFT */}
        <div className="flex gap-10 md:gap-20 items-center ">
          <Link
            href={'/'}
            className="font-bold text-lg md:text-3xl text-white-50 sm:text-2xl hover:text-customgreys-dirtyGrey duration-300">
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
        <div className="flex flex-row gap-2">
          <Link
            href={'/register'}
            className={`hidden sm:block ${buttonsBasic} bg-customgreys-secondarybg hover:bg-customgreys-darkerGrey `}>
            Sign Up
          </Link>
          <Link
            href={'/login'}
            className={`${buttonsBasic} bg-primary-700 hover:text-customgreys-primarybg hover:bg-primary-600 `}>
            Login
          </Link>
        </div>
      </div>
    </nav>
  );
};

export default NonDashboardNav;
