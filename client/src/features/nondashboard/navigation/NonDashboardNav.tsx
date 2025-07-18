'use server';

import Link from 'next/link';
import { cookies } from 'next/headers';
import AuthButtons from './AuthButtons';
import SearchButton from '@/components/Common/Navigation/SearchButton';
import { jwtDecode } from 'jwt-decode';
import { AuthToken } from '@/types/accessToken';
import CartButton from '@/components/Common/Navigation/CartButton';

const NonDashboardNav = async () => {
  const cookieStore = await cookies();

  const authToken = cookieStore.get(process.env.ACCESS_COOKIE_NAME!)?.value;
  let decoded;

  try {
    if (authToken) {
      decoded = jwtDecode(authToken) as AuthToken;
    }
  } catch (err) {
    console.error('JWT Verification Failed:', err);
  }

  return (
    <nav className="w-full bg-customgreys-primarybg">
      <div className="flex container justify-between items-center py-7 mx-auto">
        {/* LEFT */}
        <div className="flex gap-5 md:gap-20 items-center ">
          <Link href={'/'} className="font-bold py-2 text-lg md:text-3xl text-white-50 sm:text-2xl hover:text-customgreys-dirtyGrey duration-300">
            Find Course
          </Link>
          <SearchButton />
        </div>
        <div className="flex items-center sm:ml-4 gap-4 sm:gap-5">
          <CartButton authToken={decoded} />
          <AuthButtons authToken={decoded} />
        </div>
      </div>
    </nav>
  );
};

export default NonDashboardNav;
