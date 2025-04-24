'use client';
import { CustomFormField } from '@/components/Common/CustomFormField';
import { Button } from '@/components/ui/button';
import { Form } from '@/components/ui/form';
import { profileFormSchema, ProfileFormSchema } from '@/lib/validation/profile';
import { useDeleteUserMutation, useGetUserInfoQuery, useUpdateUserInfoMutation } from '@/state/api';
import { zodResolver } from '@hookform/resolvers/zod';
import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { toast } from 'sonner';
import { ApiError } from 'next/dist/server/api-utils';
import EditUserFormLoading from './EditUserFormLoading';
import { useRouter } from 'next/navigation';
import CustomAddImg from '@/components/Common/CustomAddImg';

const EditUserForm = () => {
  const router = useRouter();
  const { data: profileData, isLoading } = useGetUserInfoQuery();
  const [deleteUser] = useDeleteUserMutation();
  const [updateUserInfo] = useUpdateUserInfoMutation();

  const methods = useForm<ProfileFormSchema>({
    resolver: zodResolver(profileFormSchema),
    defaultValues: {
      username: '',
      userLastname: '',
      password: '',
      image: undefined,
      deleteImage: false,
    },
  });

  useEffect(() => {
    if (profileData) {
      methods.reset({
        username: profileData.username || '',
        userLastname: profileData.userLastname || '',
        password: '',
        image: undefined,
        deleteImage: false,
      });
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [profileData, methods.reset]);

  const onSubmit = async (data: ProfileFormSchema) => {
    const formData = new FormData();
    const userData: { username: string; userLastname: string; password?: string; deleteImage?: boolean } = {
      username: data.username,
      userLastname: data.userLastname,
      deleteImage: false,
    };
    if (data.password) {
      userData.password = data.password;
    }

    if (data.image instanceof File) {
      formData.append('image', data.image, data.image.name);
    } else if (data.image === undefined) {
      userData.deleteImage = true;
    }
    formData.append(
      'userData',
      new Blob([JSON.stringify(userData)], {
        type: 'application/json',
      }),
    );
    updateUserInfo(formData);
  };

  const handleDeleteAccount = async () => {
    if (window.confirm('Are you sure you want to delete this account? It will be permanently deleted')) {
      try {
        await deleteUser().unwrap();
        toast.success('Account deleted successfully');
        router.push('/');
        router.refresh();
      } catch (err) {
        let message = 'Something went wrong. Please try again later.';
        if (err instanceof ApiError) {
          message = err.message;
        }
        toast.error('Error deleting account', {
          description: message,
        });
      }
    }
  };

  if (isLoading) {
    return <EditUserFormLoading />;
  }

  const displayImageUrl = profileData?.imageUrl || '/Profile_avatar_placeholder.png';

  return (
    <>
      <Form {...methods}>
        <form onSubmit={methods.handleSubmit(onSubmit)}>
          <div className=" mb-4 m-auto w-fit">
            <CustomAddImg
              imageUrl={displayImageUrl}
              className="h-24 w-24 rounded-full"
              name="image"
              cropShape="round"
              aspect={1}
              maxImgDimetion={150}
              maxImageSizeMB={0.2}
              imgOnDelete={'/Profile_avatar_placeholder.png'}
            />
          </div>

          <CustomFormField name="username" label="First Name" type="text" placeholder="Enter your first name" />
          <CustomFormField name="userLastname" label="Last Name" type="text" placeholder="Enter your last name" />
          <CustomFormField name="password" label="New Password (optional)" type="password" />

          <div className="flex justify-between mt-4">
            <Button type="submit" variant="primary" className="min-w-40">
              Save Changes
            </Button>
            <Button type="button" variant="warning" className="min-w-40" onClick={() => handleDeleteAccount()}>
              Delete Account
            </Button>
          </div>
        </form>
      </Form>
    </>
  );
};

export default EditUserForm;
