'use client';

import Header from '@/components/Dashboard/Header';
import { useSelectFilter } from '@/hooks/useSelectFilter';
import { TeacherRequestStatus, SearchDirection } from '@/types/search-enums';
import TeacherRequestFilter from '@/features/dashboard/admin/teacher-request/components/TeacherRequestFilter';
import Pagination from '@/components/Common/Filter/Pagination';
import BecomeTeacherItem from '@/features/dashboard/admin/teacher-request/components/BecomeTeacherItem';
import { useEffect, useState } from 'react';
import LoadingSpinner from '@/components/Common/LoadingSpinner';
import { toast } from 'sonner';
import { ApiErrorResponse } from '@/types/apiError';
import {
  useAdminUpdateTeacherRequestsMutation,
  useLazyGetAdminBecomeUserRequestsQuery,
} from '@/features/dashboard/admin/teacher-request/api/teacherApplicationAdmin';
import ButtonWithSpinner from '@/components/Common/ButtonWithSpinner';
import { UpdateTeacherRequest } from '@/types/teacherRequest';

// TODO: add form here!!!
const TeacherRequestAdmin = ({}) => {
  const [dataToSend, setDataToSend] = useState<UpdateTeacherRequest[]>([]);

  // Filter states
  const [requetsStatus, setRequestsStatus] = useSelectFilter<TeacherRequestStatus>({ valueName: 'status' });
  const [direction, setDirection] = useSelectFilter<SearchDirection>({ valueName: 'direction', initialValue: SearchDirection.ASC });
  const [seenByAdmin, setSeenByAdmin] = useSelectFilter<'true' | 'false'>({ valueName: 'seenByAdmin' });
  const [size, setSize] = useSelectFilter<number>({ valueName: 'size', initialValue: 12 });
  const [page, setPage] = useSelectFilter<number>({ valueName: 'page' });

  // RTK Query
  const [fetchRequests, { data: teacherApplications, isLoading }] = useLazyGetAdminBecomeUserRequestsQuery();
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
    const clearedDataToSend = dataToSend.filter((item) => item.seenByAdmin !== false && item.status !== TeacherRequestStatus.PENDING);
    try {
      await adminUpdateTeacherRequests(clearedDataToSend).unwrap();
      toast.success('Data Updated');
    } catch (e) {
      const errorMessage = (e as ApiErrorResponse)?.data?.message || (e instanceof Error ? e.message : 'Something went wrong, try again later');
      toast.error(errorMessage);
    }
  };

  const handleFetchRequests = () => {
    return fetchRequests({
      page: page ?? 0,
      size: size ?? 12,
      direction: direction ?? SearchDirection.ASC,
      status: requetsStatus ?? TeacherRequestStatus.PENDING,
      seenByAdmin: seenByAdmin ?? 'false',
    });
  };

  useEffect(() => {
    handleFetchRequests();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page]);

  return (
    <div className="flex flex-col w-full min-h-full ">
      <Header
        title="Become Teacher Requestes"
        subtitle="Manage requests"
        rightElement={
          <ButtonWithSpinner isLoading={isUpdating} onClick={handleSaveChanges}>
            Save Changes
          </ButtonWithSpinner>
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
        onClick={() => handleFetchRequests()}
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
            ) : teacherApplications && teacherApplications.content.length < 1 ? (
              <p className="p-4 text-center text-lg">No Requests Found</p>
            ) : (
              teacherApplications?.content.map((item) => (
                <BecomeTeacherItem key={item.id} becomeTeacherRequest={item} handleAddDataToSend={handleAddDataToSend} />
              ))
            )}
          </ul>
        </div>
      </div>

      <Pagination totalPages={teacherApplications?.totalPages || 0} setPage={setPage} currentPage={page || 0} />
    </div>
  );
};

export default TeacherRequestAdmin;
