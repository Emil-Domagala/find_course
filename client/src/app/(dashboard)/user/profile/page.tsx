'use client';
import { CustomFormField } from '@/components/Common/CustomFormField';
import { Button } from '@/components/ui/button';
import { Form } from '@/components/ui/form';
import { profileFormSchema, ProfileFormSchema } from '@/lib/validation/profile';
import { useGetUserInfoQuery } from '@/state/api';
import { zodResolver } from '@hookform/resolvers/zod';
import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import CustomAddImage from '@/components/Common/CustomAddImage';
import ProfileLoading from './loading';

const EditUserPage = () => {
  const { data: profileData, isLoading } = useGetUserInfoQuery();

  const methods = useForm<ProfileFormSchema>({
    resolver: zodResolver(profileFormSchema),

    defaultValues: {
      username: '',
      userLastname: '',
      password: '',
      image: undefined,
    },
  });

  useEffect(() => {
    if (profileData) {
      methods.reset({
        username: profileData.username || '',
        userLastname: profileData.userLastname || '',
        password: '',
        image: undefined,
      });
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [profileData, methods.reset]);

  const onSubmit = async (data: ProfileFormSchema) => {
    console.log('Submitting Profile Data:');
    console.log(data);

    const formData = new FormData();
    formData.append('username', data.username);
    formData.append('userLastname', data.userLastname);
    if (data.password) {
      formData.append('password', data.password);
    }
    if (data.image instanceof File) {
      formData.append('image', data.image);
    } else if (data.image === null) {
      formData.append('removeImage', 'true');
    }
  };

  if (isLoading) {
    return <ProfileLoading />;
  }

  const displayImageUrl = profileData?.imageUrl || '/placeholder.png';

  return (
    <div className="space-y-6 ">
      <Form {...methods}>
        <form onSubmit={methods.handleSubmit(onSubmit)} className="space-y-4">
          <div className="flex flex-col items-center space-y-2">
            <CustomAddImage
              fallbackText={profileData?.username || ''}
              imageUrl={displayImageUrl}
              methods={methods}
              avatarClassName="h-24 w-24 rounded-full"
              className="h-24 w-24 rounded-full"
            />
          </div>

          <CustomFormField name="username" label="First Name" type="text" placeholder="Enter your first name" />
          <CustomFormField name="userLastname" label="Last Name" type="text" placeholder="Enter your last name" />
          <CustomFormField name="password" label="New Password (optional)" type="password" />

          <div className="flex justify-between">
            <Button type="submit" variant="primary" className="min-w-40">
              Save Changes
            </Button>
            <Button type="button" variant="warning" className="min-w-40">
              Delete Account
            </Button>
          </div>
        </form>
      </Form>
    </div>
  );
};

export default EditUserPage;
