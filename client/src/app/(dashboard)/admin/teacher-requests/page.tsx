'use client';

import Header from '@/components/Dashboard/Header';
import { Button } from '@/components/ui/button';
import { useSelectFilter } from '@/hooks/useSelectFilter';

import { BecomeTeacherRequestStatus, SearchDirection } from '@/types/enums';
import { Loader } from 'lucide-react';
import TeacherRequestFilter from '@/components/Dashboard/Admin/TeacherRequestFilter';
import Pagination from '@/components/Common/Filter/Pagination';
import BecomeTeacherItem from '@/components/Dashboard/Admin/BecomeTeacherItem';
import { useEffect, useState } from 'react';
import { useAdminUpdateTeacherRequestsMutation, useLazyGetAdminBecomeUserRequestsQuery } from '@/state/api';
import LoadingSpinner from '@/components/Common/LoadingSpinner';
import { toast } from 'sonner';
import { ApiErrorResponse } from '@/types/apiError';

export type UpdateTeacherRequest = { id: string; status?: BecomeTeacherRequestStatus; seenByAdmin?: boolean };

const BecomeTeacherRequestsPage = ({}) => {
  const [dataToSend, setDataToSend] = useState<UpdateTeacherRequest[]>([]);

  // Filter states
  const [requetsStatus, setRequestsStatus] = useSelectFilter<BecomeTeacherRequestStatus>({ valueName: 'status', initialValue: BecomeTeacherRequestStatus.PENDING });
  const [direction, setDirection] = useSelectFilter<SearchDirection>({ valueName: 'direction', initialValue: SearchDirection.ASC });
  const [seenByAdmin, setSeenByAdmin] = useSelectFilter<'true' | 'false'>({ valueName: 'seenByAdmin', initialValue: 'false' });
  const [size, setSize] = useSelectFilter<number>({ valueName: 'size', initialValue: 12 });
  const [page, setPage] = useSelectFilter<number>({ valueName: 'page', initialValue: 0 });

  // RTK Query
  const [fetchBecomeTeacherRequest, { data: becomeTeacherRequestPage, isLoading }] = useLazyGetAdminBecomeUserRequestsQuery();
  const [adminUpdateTeacherRequests, { isLoading: isUpdating }] = useAdminUpdateTeacherRequestsMutation();

  const handleAddDataToSend = (itemId: string, change: Partial<UpdateTeacherRequest>) => {
    const existingItemIndex = dataToSend.findIndex((item) => item.id === itemId);

    if (existingItemIndex !== -1) {
      const updatedData = [...dataToSend];
      updatedData[existingItemIndex] = { ...updatedData[existingItemIndex], ...change };
      setDataToSend(updatedData);
    } else {
      setDataToSend([...dataToSend, { ...change, id: itemId }]);
    }
  };

  const handleSaveChanges = async () => {
    const clearedDataToSend = dataToSend.filter((item) => item.seenByAdmin !== false && item.status !== BecomeTeacherRequestStatus.PENDING);
    try {
      adminUpdateTeacherRequests(clearedDataToSend).unwrap();
      toast.success('Data Updated');
    } catch (e) {
      const errorFull = e as ApiErrorResponse;
      const error = errorFull.data;
      let message = 'Something went wrong, try again later';
      if (error.message) {
        message = error.message;
      }
      toast.error(message);
    }
  };

  const handleFetchBecomeTeacherRequest = () => {
    return fetchBecomeTeacherRequest({ page, size, direction, status: requetsStatus, seenByAdmin });
  };

  useEffect(() => {
    handleFetchBecomeTeacherRequest();
  }, [page]);

  return (
    <div className="flex flex-col w-full min-h-full ">
      <Header
        title="Become Teacher Requestes"
        subtitle="Manage requests"
        rightElement={
          <Button variant="primary" className="p-3 font-medium text-md " onClick={handleSaveChanges}>
            Save Changes {isUpdating && <Loader size={20} className="animate-[spin_2s_linear_infinite]" />}
          </Button>
        }
      />
      <TeacherRequestFilter
        requetsStatus={requetsStatus}
        setRequestsStatus={setRequestsStatus}
        direction={direction || SearchDirection.ASC}
        setDirection={setDirection}
        seenByAdmin={seenByAdmin}
        setSeenByAdmin={setSeenByAdmin}
        size={size || 12}
        setSize={setSize}
        isLoading={false}
        handleFetchCourses={() => handleFetchBecomeTeacherRequest()}
      />

      <div className="flex-1 pt-6  ">
        <div className="max-w-4xl m-auto">
          <div className="flex w-full justify-between items-center text-md px-2">
            <p className="w-1/4 ">Full Name</p>
            <p className="w-1/4 text-center ">Created At</p>
            <p className="w-1/4  text-center">Status</p>
            <p className="w-1/4 text-right">Seen</p>
          </div>
          <ul>
            {isLoading ? (
              <LoadingSpinner />
            ) : becomeTeacherRequestPage && becomeTeacherRequestPage?.content.length === 0 ? (
              <p className="p-4 text-center text-lg">No New Requests Found</p>
            ) : (
              becomeTeacherRequestPage?.content.map((becomeTeacherRequest) => (
                <BecomeTeacherItem key={becomeTeacherRequest.id} becomeTeacherRequest={becomeTeacherRequest} handleAddDataToSend={handleAddDataToSend} />
              ))
            )}
          </ul>
        </div>
      </div>

      <Pagination setPage={setPage} currentPage={page || 0} />
    </div>
  );
};

export default BecomeTeacherRequestsPage;
