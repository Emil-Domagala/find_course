import SendTeacherApplicationLoading from '@/features/dashboard/user/profile/components/SendTeacherApplicationLoading';
import EditUserFormLoading from '@/features/dashboard/user/profile/components/EditUserFormLoading';

const ProfileLoading = () => {
  return (
    <>
      <EditUserFormLoading />
      <SendTeacherApplicationLoading />
    </>
  );
};

export default ProfileLoading;
