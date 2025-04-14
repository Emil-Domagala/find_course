'use client';
import { useRouter, useSearchParams } from 'next/navigation';
import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import { Form } from '@/components/ui/form';
import { NewPassword, NewPasswordSchema } from '@/lib/validation/userAuth';
import { Button } from '@/components/ui/button';
import { useState } from 'react';
import { useResetPasswordMutation } from '@/state/api';
import { ApiErrorResponse } from '@/types/apiError';
import { Loader } from 'lucide-react';
import { CustomFormField } from '@/components/Common/CustomFormField';

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

  const onSubmit = async (values: NewPassword) => {
    setIsError(false);
    setMessage('Sending request...');
    setShowInputs(false);

    if (!token) {
      setShowInputs(false);
      setIsError(true);
      setMessage('Invalid token, get new token from email');
      return;
    }

    try {
      await resetPassword({ password: values.password, token }).unwrap();
      setMessage('Password has been reset');
      pushToLogin();
    } catch (e) {
        console.log(e);
      setIsError(true);
      const errorFull = e as ApiErrorResponse;
      const error = errorFull.data;
      if (!error.message) {
        setMessage('An unexpected error occurred.');
        showInputsAgain();
        return;
      }
      if (error.message.includes('token')) {
        setMessage('Invalid token, get new token from email');
        showInputsAgain();
        return;
      }
      setMessage(error.message);
      showInputsAgain();
    }
  };

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)}>
        <div className="mb-5">
          {showInputs ? (
            <>
              <CustomFormField name="password" label="Password" type="password" />
              <CustomFormField name="confirmPassword" label="Confirm Password" type="password" />
            </>
          ) : (
            <h3 className={`text-center  font-semibold text-lg  ${isError ? 'text-red-500' : 'text-primary-750'}`}>
              {message}
            </h3>
          )}
        </div>
        <Button variant="primary" className="w-full mt-2" type="submit" disabled={isLoading || !showInputs}>
          Continue {isLoading && <Loader size={20} className="animate-[spin_2s_linear_infinite]" />}
        </Button>
      </form>
    </Form>
  );
};

export default ResetPassword;
