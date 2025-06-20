'use client';

import BecomeTeacherUserLoading from '@/components/Dashboard/User/Profile/BecomeTeacherUserLoading';
import { Accordion, AccordionContent, AccordionItem, AccordionTrigger } from '@/components/ui/accordion';
import { Button } from '@/components/ui/button';
import { transformToFrontendFormat } from '@/lib/utils';
import { useSendTeacherApplicationMutation, useGetTeacherApplicationInformationQuery } from '@/state/endpoints/teacherApplication/teacherApplicationUser';
import { ApiErrorResponse } from '@/types/apiError';
import { useState } from 'react';

const BecomeTeacherUser = () => {
  const [sendBecomeTeacherRequest] = useSendTeacherApplicationMutation();
  const { data: becomeTeacherRequestStatus, isLoading } = useGetTeacherApplicationInformationQuery();

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
    } catch (e) {
      const errorFull = e as ApiErrorResponse;

      setIsError(true);

      if (errorFull.data.message) {
        setMessage(errorFull.data.message);
      } else {
        setMessage('An unexpected error occurred.');
      }
    }
  };

  if (isLoading) return <BecomeTeacherUserLoading />;
  return (
    <Accordion type="single" collapsible className=" mt-7 bg-customgreys-primarybg/50 rounded-lg overflow-hidden">
      <AccordionItem value="item-1">
        <AccordionTrigger className="bg-customgreys-primarybg p-3 w-full rounded-lg text-white-100 text-lg font-semibold  ">Become a Teacher</AccordionTrigger>
        <AccordionContent className=" px-3 py-5">
          {becomeTeacherRequestStatus?.id ? (
            <p>
              Your request was send {new Date(becomeTeacherRequestStatus.createdAt).toLocaleDateString()} and it is{' '}
              <span
                className={`${
                  becomeTeacherRequestStatus.status === 'DENIED'
                    ? 'text-red-500'
                    : becomeTeacherRequestStatus.status === 'PENDING'
                    ? 'text-yellow-500'
                    : 'text-green-500'
                }`}>
                {transformToFrontendFormat(becomeTeacherRequestStatus.status)}
              </span>
            </p>
          ) : showSendRequest ? (
            <>
              <p className="mb-3">Let your dream job come true!</p>
              <Button className="w-full" variant="primary" onClick={handleBecomeTeacher}>
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

export default BecomeTeacherUser;
