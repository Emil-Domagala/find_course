'use client';
import { CustomFormField } from '@/components/Common/CustomFormField';
import { Form } from '@/components/ui/form';

import { zodResolver } from '@hookform/resolvers/zod';
import { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { toast } from 'sonner';
import { ApiError } from 'next/dist/server/api-utils';
import EditUserFormLoading from './EditUserFormLoading';
import { useRouter } from 'next/navigation';
import CustomAddImg from '@/components/Common/CustomAddImg';
import { useGetUserInfoQuery, useDeleteUserMutation, useUpdateUserInfoMutation } from '@/features/dashboard/user/profile/api/user';
import ButtonWithSpinner from '@/components/Common/ButtonWithSpinner';
import { profileFormSchema, ProfileFormSchema } from '../validation';

// TODO: PICTURE IS BEING DELETED
const EditUserForm = () => {
  const router = useRouter();
  const { data: profileData, isLoading } = useGetUserInfoQuery();
  const [deleteUser] = useDeleteUserMutation();
  const [updateUserInfo, { isLoading: isUpdating }] = useUpdateUserInfoMutation();
  const [deletingAccount, setDeletingAccount] = useState(false);

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
    try {
      await updateUserInfo(formData).unwrap();
      toast.success('Profile updated successfully');
      router.refresh();
    } catch (e) {
      let message = 'Something went wrong. Please try again later.';
      if (e instanceof ApiError) {
        message = e.message;
      }
      toast.error('Error updating profile', {
        description: message,
      });
    }
  };

  const handleDeleteAccount = async () => {
    if (window.confirm('Are you sure you want to delete this account? It will be permanently deleted')) {
      try {
        setDeletingAccount(true);
        await deleteUser().unwrap();
        toast.success('Account deleted successfully');
        router.push('/');
        router.refresh();
      } catch (err) {
        setDeletingAccount(false);
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
              maxImageSizeMB={0.05}
              imgOnDelete={'/Profile_avatar_placeholder.png'}
            />
          </div>

          <CustomFormField name="username" label="First Name" type="text" placeholder="Enter your first name" />
          <CustomFormField name="userLastname" label="Last Name" type="text" placeholder="Enter your last name" />
          <CustomFormField name="password" label="New Password (optional)" type="password" />

          <div className="flex justify-between gap-4 mt-4">
            <ButtonWithSpinner className="shrink-1" isLoading={isUpdating} type="submit">
              Save Changes
            </ButtonWithSpinner>

            <ButtonWithSpinner className="shrink-1" isLoading={deletingAccount} type="button" variant="warning" onClick={() => handleDeleteAccount()}>
              Delete Account
            </ButtonWithSpinner>
          </div>
        </form>
      </Form>
    </>
  );
};

export default EditUserForm;
