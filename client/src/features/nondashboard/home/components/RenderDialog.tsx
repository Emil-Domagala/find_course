'use client';

import { Button } from '@/components/ui/button';
import React, { useEffect, useState } from 'react';

const RenderDialog = ({}) => {
  const [show, setShow] = useState<boolean>(false);

  useEffect(() => {
    const currentStatus = sessionStorage.getItem('renderWarning');
    if (currentStatus == null) {
      setShow(true);
    } else {
      setShow(currentStatus === 'true');
    }
  }, []);

  useEffect(() => {
    sessionStorage.setItem('renderWarning', String(show));
  }, [show]);

  const handleClose = (e: React.MouseEvent<HTMLElement>) => {
    e.stopPropagation();
    setShow(false);
  };

  return (
    <div
      className={`z-50 fixed top-0 bottom-0 left-0 right-0 w-full h-full bg-customgreys-darkGrey/60 flex items-center justify-center ${show ? '' : 'hidden'}`}
      onClick={handleClose}>
      <div className=" p-6 w-full mx-2 md:max-w-[30rem] rounded-lg bg-customgreys-secondarybg shadow-sm" onClick={(e) => e.stopPropagation()}>
        {/* <h2 className="text-lg font-semibold mb-2">Server Starting Up</h2> */}
        <h2 className="text-lg font-semibold mb-2">SendGrid WARNING</h2>
        <div className="mb-2 text-pretty">
          <p>Due to SendGrid deleting free plan this service will not be able to send emails, Im currently working on it.</p>
          {/* <p>
            This server is hosted on{' '}
            <a href="https://render.com/" target="_blank" rel="noopener noreferrer" className="text-primary-700 font-semibold hover:underline">
              Render
            </a>
            , which may spin down after periods of inactivity.
          </p>
          <p>As a result, backend data might be temporarily unavailable while the server starts — this can take up to 2 minutes.</p>
          <p>Please hang tight — things will be ready shortly!</p> */}
        </div>
        <div className="flex justify-end">
          <Button className="px-10" variant="primary" onClick={handleClose}>
            Close
          </Button>
        </div>
      </div>
    </div>
  );
};

export default RenderDialog;
