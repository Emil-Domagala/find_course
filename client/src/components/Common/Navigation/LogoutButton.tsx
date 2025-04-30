'use client';
import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';
import { useLogoutMutation } from '@/state/api';
import { Loader } from 'lucide-react';
import { useRouter } from 'next/navigation';

const LogoutButton = ({ className }: { className?: string }) => {
  const router = useRouter();
  const [logoutUser, { isLoading }] = useLogoutMutation();

  const handleLogout = async () => {
    await logoutUser({});
    router.push('/');
    router.refresh();
  };

  return (
    <form action={handleLogout}>
      <Button
        variant={'secondary'}
        className={cn(`text-md py-2 px-4 w-full rounded-none text-white-50 font-semibold hover:bg-red-600 duration-300 transition-colors`, className)}
        disabled={isLoading}>
        {isLoading ? <Loader size={20} className="animate-[spin_2s_linear_infinite]" /> : 'Logout'}
      </Button>
    </form>
  );
};

export default LogoutButton;
