import { cookies } from 'next/headers';
import SearchButton from '../../Common/Navigation/SearchButton';
import { jwtDecode } from 'jwt-decode';
import { SidebarTrigger } from '@/components/ui/sidebar';
import { AuthToken } from '@/types/auth';
import UserButton from '@/components/Common/Navigation/UserButton';

const DashboardNav = async () => {
  const cookieStore = await cookies();

  const authToken = cookieStore.get(process.env.ACCESS_COOKIE_NAME!)?.value;
  let decoded;
  try {
    decoded = jwtDecode(authToken!) as AuthToken;
  } catch (err) {
    console.error('JWT Verification Failed:', err);
  }

  return (
    <nav className="w-full px-4 sm:px-8 pt-5 z-10">
      <div className="flex justify-between items-center w-full my-3">
        <div className="flex justify-between items-center gap-2 sm:gap-5">
          <div className="md:hidden">
            <SidebarTrigger className="text-customgreys-dirtyGrey hover:text-white-50 transition-colors" />
          </div>
          <SearchButton className="!bg-customgreys-primarybg" />
        </div>
        <div className="flex items-center gap-4 sm:gap-5">
          <UserButton authToken={decoded} className="bg-customgreys-primarybg" classNamePopover="bg-customgreys-primarybg hover:bg-customgreys-primarybg/50" />
        </div>
      </div>
    </nav>
  );
};

export default DashboardNav;
