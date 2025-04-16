'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import { Form } from '@/components/ui/form';
import { UserRegisterRequest, UserRegisterSchema } from '@/lib/validation/userAuth';
import { Button } from '@/components/ui/button';
import { useState } from 'react';
import { useRegisterMutation } from '@/state/api';
import { ApiErrorResponse } from '@/types/apiError';
import { useRouter } from 'next/navigation';
import { Loader } from 'lucide-react';
import { CustomFormField } from '@/components/Common/CustomFormField';
import Link from 'next/link';

const RegisterPage = () => {
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
    } catch (e) {
      const errorFull = e as ApiErrorResponse;
      const error = errorFull.data;
      if (!error.errors) {
        form.setError('root', { message: 'An unexpected error occurred.' });
        return;
      }
      error.errors.forEach((err) => {
        if (['email', 'username', 'userLastname', 'password'].includes(err.field)) {
          form.setError(err.field as keyof UserRegisterRequest, { message: err.message });
          return;
        }
        form.setError('root', { message: err.message });
      });
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
        {form.formState.errors.root && (
          <p className="text-red-500 text-sm text-center">{form.formState.errors.root.message}</p>
        )}
        <Button variant="primary" className="w-full mt-2" type="submit" disabled={isLoading}>
          Sign Up {isLoading && <Loader size={20} className="animate-[spin_2s_linear_infinite]" />}
        </Button>
      </form>
    </Form>
  );
};

export default RegisterPage;
