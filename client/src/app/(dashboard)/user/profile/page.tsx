'use client';
import Header from '@/components/Dashboard/Header';
import EditUserForm from './EditUserForm';

const ProfilePage = () => {
  return (
    <>
      <Header title="Profile" subtitle="View your profile" />
      <div className="max-w-[30rem] mt-5 ">
        <EditUserForm />
      </div>
    </>
  );
};

export default ProfilePage;
