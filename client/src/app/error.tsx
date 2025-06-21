'use client';

import Footer from '@/components/NonDashboard/Footer/Footer';
import NonDashboardNav from '@/components/NonDashboard/Navigation/NonDashboardNav';
import { ApiErrorResponse } from '@/types/apiError';

export default function Error({ error }: { error: ApiErrorResponse }) {
  return (
    <div className="flex flex-col min-h-screen">
      {/* <NonDashboardNav /> */}
      <div className="flex-1 flex flex-col">
        <div className="p-6 text-center">
          <div className="bgc">{/* <h1 className="text-2xl font-bold">Error {status}</h1> */}</div>
          {/* <p className="text-lg">{message}</p> */}
        </div>
      </div>
      <Footer />
    </div>
  );
}
