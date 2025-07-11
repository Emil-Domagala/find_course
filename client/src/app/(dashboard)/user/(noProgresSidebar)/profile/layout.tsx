import Header from '@/features/dashboard/components/Header';

const ProfilePage = ({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) => {
  return (
    <>
      <Header title="Profile" subtitle="View your profile" />
      <div className="max-w-[30rem] mt-5 mx-auto">{children}</div>
    </>
  );
};

export default ProfilePage;
