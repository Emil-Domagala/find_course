'use client';

import SendTeacherApplicationLoading from '@/features/dashboard/user/profile/components/SendTeacherApplicationLoading';
import { Accordion, AccordionContent, AccordionItem, AccordionTrigger } from '@/components/ui/accordion';
import { Button } from '@/components/ui/button';
import { transformToFrontendFormat } from '@/lib/utils';
import { ApiErrorResponse } from '@/types/apiError';
import { useState } from 'react';
import { useGetTeacherApplicationInformationQuery, useSendTeacherApplicationMutation } from '../api/teacherApplicationUser';

const SendTeacherApplication = () => {
  const [sendBecomeTeacherRequest] = useSendTeacherApplicationMutation();
  const { data: myTeacherRequest, isLoading } = useGetTeacherApplicationInformationQuery();

  const [showSendRequest, setShowSendRequest] = useState(true);
  const [message, setMessage] = useState('Request Sent');
  const [isError, setIsError] = useState(false);

  const handleBecomeTeacher = async () => {
    setIsError(false);
    setMessage('Sending request...');
    setShowSendRequest(false);
    try {
      await sendBecomeTeacherRequest().unwrap();
      setMessage('Request sent');
    } catch (e: unknown) {
      setIsError(true);
      const errorMessage = (e as ApiErrorResponse)?.data?.message || (e instanceof Error ? e.message : 'Something went wrong');
      setMessage(errorMessage);
    }
  };

  if (isLoading) return <SendTeacherApplicationLoading />;
  return (
    <Accordion type="single" collapsible className=" mt-7 bg-customgreys-primarybg/50 rounded-lg overflow-hidden">
      <AccordionItem value="item-1">
        <AccordionTrigger
          aria-label="Teacher Application Status"
          className="bg-customgreys-primarybg p-3 w-full rounded-lg text-white-100 text-lg font-semibold  ">
          Teacher Application Status
        </AccordionTrigger>
        <AccordionContent className=" px-3 py-5">
          {myTeacherRequest?.id ? (
            <p>
              Your request was send {new Date(myTeacherRequest.createdAt).toLocaleDateString()} and it is{' '}
              <span
                className={`${
                  myTeacherRequest.status === 'DENIED' ? 'text-red-500' : myTeacherRequest.status === 'PENDING' ? 'text-yellow-500' : 'text-green-500'
                }`}>
                {transformToFrontendFormat(myTeacherRequest.status)}
              </span>
            </p>
          ) : showSendRequest ? (
            <>
              <p className="mb-3">Let your dream job come true!</p>
              <Button aria-label="Become a Teacher" className="w-full" variant="primary" onClick={handleBecomeTeacher}>
                Become a Teacher
              </Button>
            </>
          ) : (
            <h3 className={`text-center  font-semibold text-lg ${isError ? 'text-red-500' : 'text-primary-750'}`}>{message}</h3>
          )}
        </AccordionContent>
      </AccordionItem>
    </Accordion>
  );
};

export default SendTeacherApplication;
