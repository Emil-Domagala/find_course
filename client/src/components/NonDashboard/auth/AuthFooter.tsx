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
}: {
  link: string;
  href: string;
  description: string;
  hideForgotPasswordLink?: boolean;
}) => {
  return (
    <>
      <div className="mx-auto mt-10">
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
