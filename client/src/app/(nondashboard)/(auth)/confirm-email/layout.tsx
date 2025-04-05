import AuthHeader from '@/components/NonDashboard/auth/AuthHeader';
import { AuthToken } from '@/types/auth';
import { jwtDecode } from 'jwt-decode';
import { cookies } from 'next/headers';
import { redirect } from 'next/navigation';

const ConfirmEmailLayout = async ({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) => {
  const cookieStore = await cookies();
  const authToken = cookieStore.get(process.env.AUTH_COOKIE_NAME!)?.value;
  if (!authToken) return redirect('/auth/login');

  const decoded = jwtDecode(authToken) as AuthToken;

  return (
    <>
      <AuthHeader header="Verify your Email" description={`We have sent a code to ${decoded.sub} `} />
      {children}
    </>
  );
};

export default ConfirmEmailLayout;
