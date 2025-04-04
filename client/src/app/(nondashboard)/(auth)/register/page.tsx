'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import { Form } from '@/components/ui/form';
import { UserRegisterRequest, UserRegisterSchema } from '@/lib/validation/userAuth';
import { Button } from '@/components/ui/button';
import AuthFooter from '@/components/NonDashboard/auth/AuthFooter';
import AuthHeader from '@/components/NonDashboard/auth/AuthHeader';
import AuthField from '@/components/NonDashboard/auth/AuthField';
import { useState } from 'react';
import { useRegisterMutation } from '@/state/api';
import { ApiErrorResponse } from '@/types/apiError';
import { useRouter } from 'next/navigation';

const RegisterPage = () => {
  const router = useRouter();
  const [errorInPassword, setErrorInPassword] = useState(false);
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
      router.push('/');
      router.refresh();
    } catch (e) {
      const errorFull = e as ApiErrorResponse;
      const error = errorFull.data;

      console.log(error);
      if (!error.errors) {
        form.setError('root', { message: 'An unexpected error occurred.' });
        return;
      }
      error.errors.forEach((err) => {
        if (['email', 'username', 'userLastname', 'password'].includes(err.field)) {
          form.setError(err.field as keyof UserRegisterRequest, { message: err.message });
          if (err.field === 'password') return setErrorInPassword(true);

          return;
        }
        form.setError('root', { message: err.message });
      });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <>
      <div className="flex justify-center items-center mt-10">
        <div className="rounded-xl flex flex-col sm:mx-auto shadow-none mx-4 bg-customgreys-secondarybg border-none px-6 py-10 gap-0">
          {/* Header */}
          <AuthHeader header="Create Account" description="Welcome! Please fill in the details to get started" />

          <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)}>
              <div className="flex flex-row gap-6">
                <AuthField form={form} type="text" name="username" label="First Name" />
                <AuthField form={form} type="text" name="userLastname" label="Last Name" />
              </div>
              <AuthField form={form} type="email" name="email" label="Email adress" />
              <AuthField
                form={form}
                type="password"
                name="password"
                label="Password"
                showDesc={errorInPassword}
                description="Password must be beetween 6 and 30 characters"
              />
              <Button variant="primary" className="w-full mt-2" type="submit" disabled={isLoading}>
                Sign Up
              </Button>
            </form>
          </Form>

          <AuthFooter description="Already have an account? " link="Sign in" href="/login" />
        </div>
      </div>
    </>
  );
};

export default RegisterPage;
