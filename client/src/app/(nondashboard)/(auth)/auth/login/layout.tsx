import AuthFooter from '@/features/nondashboard/auth/components/AuthFooter';
import AuthHeader from '@/features/nondashboard/auth/components/AuthHeader';

const LoginLayout = ({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) => {
  return (
    <>
      <AuthHeader header="Sign in to Find Course" description="Welcome back! Please sign in to continue" />
      {children}
      <AuthFooter loggin description="Don't have an account? " link="Sign up" href="/auth/register" />
    </>
  );
};

export default LoginLayout;
