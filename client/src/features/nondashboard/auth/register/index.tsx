'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import { Form } from '@/components/ui/form';
import { useState } from 'react';
import { ApiErrorResponse } from '@/types/apiError';
import { useRouter } from 'next/navigation';
import { CustomFormField } from '@/components/Common/CustomFormField';
import ButtonWithSpinner from '@/components/Common/ButtonWithSpinner';
import { useRegisterMutation } from './api';
import { UserRegisterRequest, UserRegisterSchema } from './validation';

const Register = () => {
  const router = useRouter();
  const [isLoading, setIsLoading] = useState(false);
  const [registerUser] = useRegisterMutation();

  const form = useForm<UserRegisterRequest>({
    resolver: zodResolver(UserRegisterSchema),
    defaultValues: {
      email: '',
      username: '',
      userLastname: '',
      password: '',
    },
  });

  const onSubmit = async (values: UserRegisterRequest) => {
    try {
      setIsLoading(true);
      await registerUser(values).unwrap();
      router.push('/confirm-email');
      router.refresh();
    } catch (e: unknown) {
      const errorMessage = (e as ApiErrorResponse)?.data?.message || (e instanceof Error ? e.message : 'An unexpected error occurred.');
      const fieldErrors = (e as ApiErrorResponse)?.data?.errors;
      if (fieldErrors) {
        fieldErrors.forEach((err) => {
          if (['email', 'username', 'userLastname', 'password'].includes(err.field)) {
            form.setError(err.field as keyof UserRegisterRequest, { message: err.message });
          }
        });
      } else {
        form.setError('root', { message: errorMessage });
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)}>
        <div className="flex flex-row gap-6">
          <CustomFormField name="username" label="First Name" type="text" className="mb-2 w-full" />
          <CustomFormField name="userLastname" label="Last Name" type="text" className="mb-2 w-full" />
        </div>
        <CustomFormField name="email" label="Email adress" type="email" className="mb-2" />
        <CustomFormField name="password" label="Password" type="password" className="mb-2" />
        {form.formState.errors.root && <p className="text-red-500 text-sm text-center">{form.formState.errors.root.message}</p>}
        <ButtonWithSpinner className="w-full mt-2" isLoading={isLoading}>
          Sign Up
        </ButtonWithSpinner>
      </form>
    </Form>
  );
};

export default Register;
