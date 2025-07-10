'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import { Form } from '@/components/ui/form';
import { ForgotPasswordRequest, ForgotPasswordShema } from '@/lib/validation/userAuth';
import { useState } from 'react';
import { ApiErrorResponse } from '@/types/apiError';
import { CustomFormField } from '@/components/Common/CustomFormField';
import { useSendResetPasswordEmailMutation } from '@/features/nondashboard/auth/password/api/resetPassword';
import ButtonWithSpinner from '@/components/Common/ButtonWithSpinner';

const ForgotPassword = () => {
  const [sendResetPassword, { isLoading }] = useSendResetPasswordEmailMutation();
  const [showInputs, setShowInputs] = useState(true);
  const [message, setMessage] = useState('');
  const [isError, setIsError] = useState(false);

  const form = useForm<ForgotPasswordRequest>({
    resolver: zodResolver(ForgotPasswordShema),
    defaultValues: {
      email: '',
    },
  });

  const showInputsAgain = () =>
    setTimeout(() => {
      setShowInputs(true);
    }, 1500);

  const onSubmit = async (values: ForgotPasswordRequest) => {
    setIsError(false);
    setMessage('Sending request...');
    setShowInputs(false);
    try {
      await sendResetPassword(values).unwrap();
      setMessage('Reset password email has been send');
    } catch (e) {
      setIsError(true);
      const errorMessage = (e as ApiErrorResponse)?.data?.message || (e instanceof Error ? e.message : 'An unexpected error occurred.');
      setMessage(errorMessage);
      showInputsAgain();
    }
  };

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)}>
        <div className="mb-5">
          {showInputs ? (
            <CustomFormField name="email" label="Email adress" type="email" />
          ) : (
            <h3 className={`text-center  font-semibold text-lg  ${isError ? 'text-red-500' : 'text-primary-750'}`}>{message}</h3>
          )}
        </div>
        <ButtonWithSpinner isLoading={isLoading} type="submit" disabled={isLoading || !showInputs}>
          Continue
        </ButtonWithSpinner>
      </form>
    </Form>
  );
};

export default ForgotPassword;
