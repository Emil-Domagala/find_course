import UserButton from '@/components/Common/Navigation/UserButton';
import { AuthToken } from '@/types/accessToken';
import Link from 'next/link';

const buttonsBasic = `text-md py-2 px-4 rounded-lg text-white-50 font-semibold duration-300 transition-colors`;

const AuthButtons = ({ authToken }: { authToken?: AuthToken }) => {
  return (
    <>
      {!authToken ? (
        <div className="flex flex-row gap-2">
          <Link href={'/auth/register'} className={`hidden sm:block ${buttonsBasic} bg-customgreys-secondarybg hover:bg-customgreys-darkerGrey `}>
            Sign Up
          </Link>
          <Link href={'/auth/login'} className={`${buttonsBasic} bg-primary-700 hover:text-customgreys-primarybg hover:bg-primary-600 `}>
            Login
          </Link>
        </div>
      ) : (
        <UserButton authToken={authToken} />
      )}
    </>
  );
};

export default AuthButtons;
