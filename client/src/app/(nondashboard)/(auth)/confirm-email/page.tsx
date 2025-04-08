'use client';

import { Button } from '@/components/ui/button';
import { InputOTP, InputOTPGroup, InputOTPSlot } from '@/components/ui/input-otp';
import { useConfirmEmailMutation, useResendConfirmEmailTokenMutation } from '@/state/api';
import { REGEXP_ONLY_DIGITS_AND_CHARS } from 'input-otp';
import { useState } from 'react';

const ConfirmEmailPage = () => {
  const [showInputs, setShowInputs] = useState(true);
  const [message, setMessage] = useState('Request Send');

  const [confirmEmail] = useConfirmEmailMutation();
  const [resendConfirmEmail] = useResendConfirmEmailTokenMutation();
  const handleVerifyEmail = async (args: string) => {
    setShowInputs(false);
    try {
      await confirmEmail(args);
    } catch (err) {
      console.log(err);
    }
    console.log(args);
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
        <h3 className="text-center text-primary-750 font-semibold text-lg">{message}</h3>
      )}

      <div className="mx-auto mt-10">
        <span className="text-md ">Didn&apos;t recive an email? </span>
        <Button
          onClick={resendConfirmEmail}
          variant="link"
          className="text-primary-750 hover:text-primary-600 text-md transition-colors duration-300">
          Resend
        </Button>
      </div>
    </>
  );
};

export default ConfirmEmailPage;
