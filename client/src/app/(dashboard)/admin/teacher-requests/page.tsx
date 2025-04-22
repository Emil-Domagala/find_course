'use client';

import Header from '@/components/Dashboard/Header';
import { Button } from '@/components/ui/button';
import { useSelectFilter } from '@/hooks/useSelectFilter';

import { BecomeTeacherRequestStatus, SearchDirection } from '@/types/enums';
import { Loader } from 'lucide-react';
import TeacherRequestFilter from './TeacherRequestFilter';
import Pagination from '@/components/Common/Filter/Pagination';
import BecomeTeacherItem from './BecomeTeacherItem';
import { useEffect } from 'react';
import { useLazyGetAdminBecomeUserRequestsQuery } from '@/state/api';

const BecomeTeacherRequestsPage = ({}) => {
  const [requetsStatus, setRequestsStatus] = useSelectFilter<BecomeTeacherRequestStatus>({ valueName: 'status', initialValue: BecomeTeacherRequestStatus.PENDING });
  const [direction, setDirection] = useSelectFilter<SearchDirection>({ valueName: 'direction', initialValue: SearchDirection.ASC });
  const [seenByAdmin, setSeenByAdmin] = useSelectFilter<'true' | 'false'>({ valueName: 'seenByAdmin', initialValue: 'false' });
  const [size, setSize] = useSelectFilter<number>({ valueName: 'size', initialValue: 12 });
  const [page, setPage] = useSelectFilter<number>({ valueName: 'page', initialValue: 0 });

  const [fetchBecomeTeacherRequest, { data: becomeTeacherRequestPage, isLoading }] = useLazyGetAdminBecomeUserRequestsQuery();

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
          <Button variant="primary" className="p-3 font-medium text-md ">
            Save {true && <Loader size={20} className="animate-[spin_2s_linear_infinite]" />}
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
        handleFetchCourses={() => {}}
      />

      <div className="flex-1 pt-6  ">
        <div className="max-w-4xl m-auto">
          <div className="flex w-full justify-between items-center text-md px-2">
            <p className="w-1/4 ">Full Name</p>
            <p className="w-1/4 text-center ">Created At</p>
            <p className="w-1/4  text-center">Status</p>
            <p className="w-1/4 text-right">Seen</p>
          </div>
          {becomeTeacherRequestPage?.content.map((becomeTeacherRequest) => <BecomeTeacherItem key={becomeTeacherRequest.id} becomeTeacherRequest={becomeTeacherRequest} />)}
        </div>
      </div>

      <Pagination setPage={setPage} currentPage={page || 0} />
    </div>
  );
};

export default BecomeTeacherRequestsPage;
