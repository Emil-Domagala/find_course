'use client';
import { useRouter, useSearchParams } from 'next/navigation';
import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import { Form } from '@/components/ui/form';
import { NewPassword, NewPasswordSchema } from '@/lib/validation/userAuth';
import { useState } from 'react';
import { ApiErrorResponse } from '@/types/apiError';
import { CustomFormField } from '@/components/Common/CustomFormField';
import ButtonWithSpinner from '@/components/Common/ButtonWithSpinner';
import { useResetPasswordMutation } from '../api';

const ResetPassword = ({}) => {
  const router = useRouter();
  const searchParams = useSearchParams();
  const token = searchParams.get('token');
  const [resetPassword, { isLoading }] = useResetPasswordMutation();
  const [showInputs, setShowInputs] = useState(true);
  const [message, setMessage] = useState('Request Send');
  const [isError, setIsError] = useState(false);

  const form = useForm<NewPassword>({
    resolver: zodResolver(NewPasswordSchema),
    defaultValues: {
      password: '',
      confirmPassword: '',
    },
  });

  const showInputsAgain = () =>
    setTimeout(() => {
      setShowInputs(true);
    }, 1500);

  const pushToLogin = () =>
    setTimeout(() => {
      router.push('/auth/login');
    }, 1500);

  const handleInvalidToken = () => {
    setMessage('Invalid token, get new token from email');
    return setTimeout(() => {
      router.push('/auth/forgot-password');
    }, 1500);
  };
  const onSubmit = async (values: NewPassword) => {
    setIsError(false);
    setMessage('Sending request...');
    setShowInputs(false);

    if (!token) {
      setShowInputs(false);
      setIsError(true);
      return handleInvalidToken();
    }

    try {
      await resetPassword({ password: values.password, token }).unwrap();
      setMessage('Password has been reset');
      pushToLogin();
    } catch (e) {
      setIsError(true);
      const errorMessage = (e as ApiErrorResponse)?.data?.message || (e instanceof Error ? e.message : 'An unexpected error occurred.');
      if (errorMessage.includes('token')) {
        return handleInvalidToken();
      }
      setMessage(errorMessage);
      showInputsAgain();
    }
  };

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)}>
        <div className="mb-5">
          {showInputs ? (
            <>
              <CustomFormField name="password" label="New Password" type="password" />
              <CustomFormField name="confirmPassword" label="Confirm Password" type="password" />
            </>
          ) : (
            <h3 className={`text-center  font-semibold text-lg  ${isError ? 'text-red-500' : 'text-primary-750'}`}>{message}</h3>
          )}
        </div>
        <ButtonWithSpinner className="w-full mt-2" isLoading={isLoading} disabled={!showInputs || isLoading}>
          Reset Password
        </ButtonWithSpinner>
      </form>
    </Form>
  );
};

export default ResetPassword;
