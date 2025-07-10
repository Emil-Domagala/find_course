import BecomeTeacherUserLoading from '@/features/dashboard/user/profile/components/BecomeTeacherUserLoading';
import EditUserFormLoading from '@/features/dashboard/user/profile/components/EditUserFormLoading';

const ProfileLoading = () => {
  return (
    <>
      <EditUserFormLoading />
      <BecomeTeacherUserLoading />
    </>
  );
};

export default ProfileLoading;
