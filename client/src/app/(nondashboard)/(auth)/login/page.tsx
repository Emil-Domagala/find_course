'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import { Form } from '@/components/ui/form';
import { UserLoginSchema } from '@/lib/validation/userAuth';
import { Button } from '@/components/ui/button';
import AuthField from '@/components/NonDashboard/auth/AuthField';
import { useState } from 'react';
import { useLoginMutation } from '@/state/api';
import { ApiErrorResponse } from '@/types/apiError';
import { useRouter } from 'next/navigation';
import { UserLoginRequest } from '@/types/auth';

const LoginPage = () => {
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
      // router.push('/dashboard');
      router.refresh();
    } catch (e) {
      const errorFull = e as ApiErrorResponse;
      const error = errorFull.data;
      console.log(error);
      if (!error.message) {
        form.setError('root', { message: 'An unexpected error occurred.' });
        return;
      }

      form.setError('root', { message: error.message });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)}>
        <AuthField form={form} type="email" name="email" label="Email adress" />
        <AuthField form={form} type="password" name="password" label="Password" />
        {form.formState.errors.root && (
          <p className="text-red-500 text-sm text-center">{form.formState.errors.root.message}</p>
        )}
        <Button variant="primary" className="w-full mt-2" type="submit" disabled={isLoading}>
          Continue
        </Button>
      </form>
    </Form>
  );
};

export default LoginPage;
