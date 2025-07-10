'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import { Form } from '@/components/ui/form';
import { UserLoginRequest, UserLoginSchema } from '@/lib/validation/userAuth';
import { useState } from 'react';
import { ApiErrorResponse } from '@/types/apiError';
import { useRouter, useSearchParams } from 'next/navigation';
import { CustomFormField } from '@/components/Common/CustomFormField';
import ButtonWithSpinner from '@/components/Common/ButtonWithSpinner';
import { useLoginMutation } from './api';

const Login = () => {
  const searchParams = useSearchParams();
  const redirect = searchParams.get('redirect') || '/user/courses';

  const router = useRouter();
  const [isLoading, setIsLoading] = useState(false);
  const [loginUser] = useLoginMutation();

  const form = useForm<UserLoginRequest>({
    resolver: zodResolver(UserLoginSchema),
    defaultValues: {
      email: '',
      password: '',
    },
  });

  const onSubmit = async (values: UserLoginRequest) => {
    try {
      setIsLoading(true);
      await loginUser(values).unwrap();
      router.push(redirect);
      router.refresh();
    } catch (e: unknown) {
      const errorMessage = (e as ApiErrorResponse)?.data?.message || (e instanceof Error ? e.message : 'An unexpected error occurred.');
      form.setError('root', { message: errorMessage });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)}>
        <CustomFormField type="email" name="email" label="Email adress" className="mb-2" />
        <CustomFormField type="password" name="password" label="Password" className="mb-2" />
        {form.formState.errors.root && <p className="text-red-500 text-sm text-center">{form.formState.errors.root.message}</p>}
        <ButtonWithSpinner variant="primary" type="submit" isLoading={isLoading}>
          Continue
        </ButtonWithSpinner>
      </form>
    </Form>
  );
};

export default Login;
