import AuthFooter from '@/components/NonDashboard/auth/AuthFooter';
import AuthHeader from '@/components/NonDashboard/auth/AuthHeader';

const ForgotPasswordLayout = ({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) => {
  return (
    <>
      <AuthHeader header="Reset Your Password" description="Enter your email address and we'll send instructions to reset your password." />
      {children}
      <AuthFooter description="Remembered your password? " link="Sign in" href="/auth/login" hideForgotPasswordLink />
    </>
  );
};

export default ForgotPasswordLayout;
