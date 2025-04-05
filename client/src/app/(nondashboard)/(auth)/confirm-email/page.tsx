'use client';

import { Button } from '@/components/ui/button';
import { InputOTP, InputOTPGroup, InputOTPSlot } from '@/components/ui/input-otp';
import { REGEXP_ONLY_DIGITS_AND_CHARS } from 'input-otp';

const ConfirmEmailPage = () => {
  return (
    <>
      <InputOTP
        maxLength={6}
        pattern={REGEXP_ONLY_DIGITS_AND_CHARS}
        inputMode="text"
        onComplete={(args) => {
          console.log('complited');
          console.log(args);
        }}>
        <InputOTPGroup className="mx-auto">
          <InputOTPSlot index={0} />
          <InputOTPSlot index={1} />
          <InputOTPSlot index={2} />
          <InputOTPSlot index={3} />
          <InputOTPSlot index={4} />
          <InputOTPSlot index={5} />
        </InputOTPGroup>
      </InputOTP>

      <div className="mx-auto mt-10">
        <span className="text-md ">Didn&apos;t recive an email? </span>
        <Button
          variant="link"
          className="text-primary-750 hover:text-primary-600 text-md transition-colors duration-300">
          Resend
        </Button>
      </div>
    </>
  );
};

export default ConfirmEmailPage;
