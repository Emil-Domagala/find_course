'use client';

import { Button } from '@/components/ui/button';
import { InputOTP, InputOTPGroup, InputOTPSlot } from '@/components/ui/input-otp';
import { useConfirmEmailMutation, useResendConfirmEmailTokenMutation } from '@/state/api';
import { ApiErrorResponse } from '@/types/apiError';
import { REGEXP_ONLY_DIGITS_AND_CHARS } from 'input-otp';
import { useRouter } from 'next/navigation';
import { useState } from 'react';

const ConfirmEmailPage = () => {
  const router = useRouter();
  const [showInputs, setShowInputs] = useState(true);
  const [message, setMessage] = useState('Request Send');
  const [isError, setIsError] = useState(false);

  const [confirmEmail] = useConfirmEmailMutation();
  const [resendConfirmEmail] = useResendConfirmEmailTokenMutation();

  const showInputsAgain = () =>
    setTimeout(() => {
      setShowInputs(true);
    }, 1500);

  const pushToCourses = () => {
    console.log('push to profile');
    setTimeout(() => {
      router.push('/user/courses');
    }, 1500);
  };

  const handleError = (e: unknown) => {
    setIsError(true);
    const errorFull = e as ApiErrorResponse;
    const error = errorFull.data;
    if (!error.message) {
      setMessage('An unexpected error occurred.');
      showInputsAgain();
      return;
    }
    setMessage(error.message);
    if (error.message.includes('verified')) {
      pushToCourses();
    } else {
      showInputsAgain();
    }
  };

  const handleResendEmail = async () => {
    setIsError(false);
    setMessage('Sending request...');
    setShowInputs(false);
    try {
      await resendConfirmEmail({}).unwrap();
      setMessage('New confirmation email sent!');
    } catch (e) {
      handleError(e);
    }
  };

  const handleVerifyEmail = async (args: string) => {
    setShowInputs(false);
    setMessage('Verifying email...');
    setIsError(false);
    try {
      await confirmEmail(args).unwrap();
      setMessage('Email confirmed');
      pushToCourses();
    } catch (e) {
      handleError(e);
    }
  };

  return (
    <>
      {showInputs ? (
        <InputOTP
          maxLength={6}
          pattern={REGEXP_ONLY_DIGITS_AND_CHARS}
          inputMode="text"
          onComplete={(args) => handleVerifyEmail(args)}>
          <InputOTPGroup className="mx-auto">
            <InputOTPSlot index={0} />
            <InputOTPSlot index={1} />
            <InputOTPSlot index={2} />
            <InputOTPSlot index={3} />
            <InputOTPSlot index={4} />
            <InputOTPSlot index={5} />
          </InputOTPGroup>
        </InputOTP>
      ) : (
        <h3 className={`text-center  font-semibold text-lg ${isError ? 'text-red-500' : 'text-primary-750'}`}>
          {message}
        </h3>
      )}

      <div className="mx-auto mt-10">
        <span className="text-md ">Didn&apos;t recive an email? </span>
        <Button
          onClick={() => handleResendEmail()}
          variant="link"
          className="text-primary-750 hover:text-primary-600 text-md transition-colors duration-300">
          Resend
        </Button>
      </div>
    </>
  );
};

export default ConfirmEmailPage;
