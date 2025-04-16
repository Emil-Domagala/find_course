'use client';

import { cn } from '@/lib/utils';
import { Check } from 'lucide-react';
import { usePathname } from 'next/navigation';
import React from 'react';

const CheckoutStepper = () => {
  const pathname = usePathname();
  let currentStep: number = 1;
  if (pathname === '/user/cart') currentStep = 1;
  if (pathname === '/user/cart/completion') currentStep = 2;

  return (
    <div className="w-1/2 md:w-1/3 mb-4 flex flex-col items-center">
      <div className="w-full flex items-center justify-between mb-2">
        {[1, 2].map((step, index) => (
          <React.Fragment key={step}>
            <div className="flex flex-col items-center relative pb-7">
              <div
                className={cn('w-8 h-8 flex items-center justify-center rounded-full', {
                  'bg-green-500': currentStep > step || (currentStep === 2 && step === 2),
                  'bg-primary-700': currentStep === step && step !== 2,
                  'border border-customgreys-dirtyGrey text-customgreys-dirtyGrey': currentStep < step,
                })}>
                {currentStep > step || (currentStep === 2 && step === 2) ? (
                  <Check className="w-5 h-5" />
                ) : (
                  <span>{step}</span>
                )}
              </div>
              <p
                className={cn('text-sm absolute bottom-0', {
                  'text-white-100': currentStep >= step,
                  'text-customgreys-dirtyGrey': currentStep < step,
                })}>
                {step === 1 && 'Cart'}
                {step === 2 && 'Completion'}
              </p>
            </div>
            {index < 1 && (
              <div
                className={cn('w-1/2 h-[1px] self-start mt-4', {
                  'bg-green-500': currentStep > step,
                  'bg-customgreys-dirtyGrey': currentStep <= step,
                })}
              />
            )}
          </React.Fragment>
        ))}
      </div>
    </div>
  );
};

export default CheckoutStepper;
