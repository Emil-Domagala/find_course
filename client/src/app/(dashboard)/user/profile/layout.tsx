import Header from '@/components/Dashboard/Header';

const ProfilePage = ({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) => {
  return (
    <>
      <Header title="Profile" subtitle="View your profile" />
      <div className="max-w-[30rem] mt-5 ">{children}</div>
    </>
  );
};

export default ProfilePage;
