'use client';

import Header from '@/components/Dashboard/Header';
import { Button } from '@/components/ui/button';
import { useSelectFilter } from '@/hooks/useSelectFilter';

import { BecomeTeacherRequestStatus, SearchDirection } from '@/types/enums';
import { Loader } from 'lucide-react';
import TeacherRequestFilter from './TeacherRequestFilter';
import Pagination from '@/components/Common/Filter/Pagination';

const BecomeTeacherRequestsPage = ({}) => {
  const [requetsStatus, setRequestsStatus] = useSelectFilter<BecomeTeacherRequestStatus>({ valueName: 'status', initialValue: BecomeTeacherRequestStatus.PENDING });
  const [direction, setDirection] = useSelectFilter<SearchDirection>({ valueName: 'direction', initialValue: SearchDirection.ASC });
  const [seenByAdmin, setSeenByAdmin] = useSelectFilter<'true' | 'false'>({ valueName: 'seenByAdmin', initialValue: 'false' });
  const [size, setSize] = useSelectFilter<number>({ valueName: 'size', initialValue: 12 });
  const [page, setPage] = useSelectFilter<number>({ valueName: 'page', initialValue: 0 });

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

      <div className="flex-1 pt-6 bg-red-600"></div>

      <Pagination setPage={setPage} currentPage={page || 0} />
    </div>
  );
};

export default BecomeTeacherRequestsPage;
