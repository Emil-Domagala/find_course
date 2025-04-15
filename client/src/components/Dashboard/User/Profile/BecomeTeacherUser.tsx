'use client';

import BecomeTeacherUserLoading from '@/components/Dashboard/User/Profile/BecomeTeacherUserLoading';
import { Accordion, AccordionContent, AccordionItem, AccordionTrigger } from '@/components/ui/accordion';
import { Button } from '@/components/ui/button';
import { transformToFrontendFormat } from '@/lib/utils';
import { useGetBecomeTeacherRequestStatusQuery, useSendBecomeTeacherRequestMutation } from '@/state/api';
import { ApiError } from 'next/dist/server/api-utils';
import { useState } from 'react';

const BecomeTeacherUser = () => {
  const [sendBecomeTeacherRequest] = useSendBecomeTeacherRequestMutation();
  const { data: becomeTeacherRequestStatus, isLoading } = useGetBecomeTeacherRequestStatusQuery();

  console.log(becomeTeacherRequestStatus);

  //   Send become teacher request states
  const [showSendRequest, setShowSendRequest] = useState(true);
  const [message, setMessage] = useState('Request Sent');
  const [isError, setIsError] = useState(false);

  const handleBecomeTeacher = async () => {
    setIsError(false);
    setMessage('Sending request...');
    setShowSendRequest(false);
    try {
      await sendBecomeTeacherRequest();
      setMessage('Request sent');
    } catch (e) {
      setIsError(true);
      console.log(e);
      if (e instanceof ApiError) {
        setMessage(e.message);
      } else {
        setMessage('An unexpected error occurred.');
      }
    }
  };

  if (isLoading) return <BecomeTeacherUserLoading />;
  return (
    <Accordion type="single" collapsible className=" mt-7 bg-customgreys-primarybg/50 rounded-lg overflow-hidden">
      <AccordionItem value="item-1">
        <AccordionTrigger className="bg-customgreys-primarybg p-3 w-full rounded-lg text-white-100 text-lg font-semibold  ">
          Become a Teacher
        </AccordionTrigger>
        <AccordionContent className=" px-3 py-5">
          {becomeTeacherRequestStatus?.user ? (
            <p>
              Your request was send {new Date(becomeTeacherRequestStatus.createdAt).toLocaleDateString()} and it is{' '}
              <span
                className={`${becomeTeacherRequestStatus.status === 'DENIED' ? 'text-red-500' : becomeTeacherRequestStatus.status === 'PENDING' ? 'text-yellow-500' : 'text-green-500'}`}>
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
            <h3 className={`text-center  font-semibold text-lg ${isError ? 'text-red-500' : 'text-primary-750'}`}>
              {message}
            </h3>
          )}
        </AccordionContent>
      </AccordionItem>
    </Accordion>
  );
};

export default BecomeTeacherUser;
