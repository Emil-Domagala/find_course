import AuthFooter from '@/features/nondashboard/auth/components/AuthFooter';
import AuthHeader from '@/features/nondashboard/auth/components/AuthHeader';

const ForgotPasswordLayout = ({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) => {
  return (
    <>
      <AuthHeader header="Set New Password" description="Enter your new password and confirm it" />
      {children}
      <AuthFooter description="Remembered your password? " link="Sign in" href="/auth/login" hideForgotPasswordLink />
    </>
  );
};

export default ForgotPasswordLayout;
