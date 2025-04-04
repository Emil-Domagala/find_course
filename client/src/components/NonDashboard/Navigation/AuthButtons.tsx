'use client';
import { Button } from '@/components/ui/button';
import { useLogoutMutation } from '@/state/api';
import { Loader } from 'lucide-react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';

const buttonsBasic = `text-md py-2 px-4 rounded-lg text-white-50 font-semibold duration-300 transition-colors`;

const AuthButtons = ({ authToken }: { authToken: boolean }) => {
  const router = useRouter();
  const [logoutUser, { isLoading }] = useLogoutMutation();

  const handleLogout = async () => {
    await logoutUser({});
    router.push('/');
    router.refresh();
  };

  return (
    <>
      {!authToken ? (
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
      ) : (
        <form action={handleLogout}>
          <Button variant={'secondary'} className={`${buttonsBasic} `} disabled={isLoading}>
            Logout
            {isLoading && <Loader />}
          </Button>
        </form>
      )}
    </>
  );
};

export default AuthButtons;
