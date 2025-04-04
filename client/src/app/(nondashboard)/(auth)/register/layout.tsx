import AuthFooter from '@/components/NonDashboard/auth/AuthFooter';
import AuthHeader from '@/components/NonDashboard/auth/AuthHeader';

const RegisterLayout = ({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) => {
  return (
    <>
      <AuthHeader header="Create Account" description="Welcome! Please fill in the details to get started" />
      {children}
      <AuthFooter description="Already have an account? " link="Sign in" href="/login" />
    </>
  );
};

export default RegisterLayout;
