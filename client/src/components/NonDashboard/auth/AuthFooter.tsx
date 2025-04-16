import { Skeleton } from '@/components/ui/skeleton';
import Link from 'next/link';

export const AuthFooterSkeleton = () => {
  return <Skeleton className="h-4 w-64 mx-auto mt-10"></Skeleton>;
};

const AuthFooter = ({
  link,
  href,
  description,
  hideForgotPasswordLink = false,
  loggin,
}: {
  link: string;
  href: string;
  description: string;
  hideForgotPasswordLink?: boolean;
  loggin: boolean;
}) => {
  return (
    <>
      <p className="text-customgreys-dirtyGrey text-center mt-2">
        By {loggin ? 'logging in' : 'creating an account'}, you agree to our{' '}
        <Link href="/privacy-policy" className="text-primary-700 hover:underline">
          Privacy Policy
        </Link>{' '}
        and{' '}
        <Link href="/terms-of-use" className="text-primary-700 hover:underline">
          Terms of Use
        </Link>
      </p>
      <div className="mx-auto mt-8">
        <span className="text-md ">{description}</span>
        <Link className="text-primary-750 hover:text-primary-600 text-md transition-colors duration-300" href={href}>
          {link}
        </Link>
      </div>
      {!hideForgotPasswordLink && (
        <Link
          href={'/auth/forgot-password'}
          className="mx-auto mt-2 text-primary-750 hover:text-primary-600 text-md transition-colors duration-300">
          Forgot password?
        </Link>
      )}
    </>
  );
};

export default AuthFooter;
