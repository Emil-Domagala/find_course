import { cookies } from 'next/headers';
import SearchButton from '../../Common/Navigation/SearchButton';
import { jwtDecode } from 'jwt-decode';
import { SidebarTrigger } from '@/components/ui/sidebar';
import { AuthToken } from '@/types/auth';

const DashboardNav = async () => {
  const cookieStore = await cookies();

  const authToken = cookieStore.get(process.env.AUTH_COOKIE_NAME!)?.value;
  const userRole = [];
  try {
    const decoded = jwtDecode(authToken!) as AuthToken;
    userRole.push(decoded.roles);
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
          <SearchButton className="bg-customgreys-primarybg" />
        </div>
      </div>
    </nav>
  );
};

export default DashboardNav;
