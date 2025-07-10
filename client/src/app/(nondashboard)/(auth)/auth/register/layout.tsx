import AuthFooter from '@/features/nondashboard/auth/components/AuthFooter';
import AuthHeader from '@/features/nondashboard/auth/components/AuthHeader';

const RegisterLayout = ({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) => {
  return (
    <>
      <AuthHeader header="Create Account" description="Welcome! Please fill in the details to get started" />
      {children}
      <AuthFooter loggin={false} description="Already have an account? " link="Sign in" href="/auth/login" />
    </>
  );
};

export default RegisterLayout;
