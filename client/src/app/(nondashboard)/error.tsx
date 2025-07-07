'use client';

import { ApiErrorResponse } from '@/types/apiError';

function isApiErrorResponse(error: unknown): error is ApiErrorResponse {
  return (
    typeof error === 'object' &&
    error !== null &&
    'status' in error &&
    typeof error.status === 'number' &&
    'data' in error &&
    typeof error.data === 'object' &&
    error.data !== null &&
    'message' in error.data &&
    typeof error.data.message === 'string'
  );
}

export default function Error({ error }: { error: unknown }) {
  const isApiError = isApiErrorResponse(error);

  return (
    <div className="pt-10 px-6 text-center">
      <div className="bg-[url('/galaxy.jpg')] bg-cover bg-[position:0%_60%] bg-no-repeat text-transparent bg-clip-text text-center">
        <h1 className="text-[19vw] md:text-[11cqi] font-bold leading-none">Error {isApiError ? error.status : 500}</h1>
      </div>
      <p className="text-2xl font-semibold leading-tight pb-4">{isApiError ? error.data.message : 'An unexpected error occured, please try again later.'}</p>
    </div>
  );
}
