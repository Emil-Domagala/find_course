import AuthFooter from '@/components/NonDashboard/auth/AuthFooter';
import AuthHeader from '@/components/NonDashboard/auth/AuthHeader';

const LoginLayout = ({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) => {
  return (
    <>
      <AuthHeader header="Sign in to Find Course" description="Welcome back! Please sign in to continue" />
      {children}
      <AuthFooter description="Don't have an account? " link="Sign up" href="/register" />
    </>
  );
};

export default LoginLayout;
