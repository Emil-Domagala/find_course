'use client';

import CustomSelect from '@/components/Common/Filter/CustomSelect';
import { Button } from '@/components/ui/button';
import { BecomeTeacherRequestStatus, SearchDirection } from '@/types/enums';
import { Loader } from 'lucide-react';
import { transformToFrontendFormat } from '@/lib/utils';
import { SetStateAction } from 'react';

type Props = {
  requetsStatus: BecomeTeacherRequestStatus | undefined;
  setRequestsStatus: React.Dispatch<SetStateAction<BecomeTeacherRequestStatus | undefined>>;
  direction: SearchDirection;
  setDirection: React.Dispatch<SetStateAction<SearchDirection | undefined>>;
  seenByAdmin: 'true' | 'false' | undefined;
  setSeenByAdmin: React.Dispatch<SetStateAction<'true' | 'false' | undefined>>;
  size: number;
  setSize: React.Dispatch<SetStateAction<number | undefined>>;
  isLoading: boolean;
  handleFetchCourses: () => void;
};

const TeacherRequestFilter = ({ handleFetchCourses, isLoading, requetsStatus, setRequestsStatus, direction, setDirection, seenByAdmin, setSeenByAdmin, size, setSize }: Props) => {
  return (
    <div className="flex flex-col md:flex-row  gap-5 justify-center items-end w-full">
      <div className={`flex flex-row gap-2 justify-between items-end w-full md:gap-5 md:justify-center md:w-fit`}>
        <CustomSelect
          label="Request Status"
          value={requetsStatus}
          onChange={setRequestsStatus}
          options={Object.values(BecomeTeacherRequestStatus)}
          placeholder="Status"
          transformFn={transformToFrontendFormat}
          clearable={true}
          selectTriggerClasses={'md:w-52 w-full'}
          selectContentClasses={'max-h-60'}
        />
        <CustomSelect
          label="Seen"
          value={seenByAdmin}
          onChange={setSeenByAdmin}
          options={['true', 'false']}
          placeholder="Status"
          transformFn={(e) => e}
          clearable={true}
          selectTriggerClasses={'md:w-52 w-full'}
          selectContentClasses={'max-h-60'}
        />

        <CustomSelect
          label="Direction"
          value={direction}
          onChange={setDirection}
          options={Object.values(SearchDirection)}
          placeholder="Select Direction"
          transformFn={(e) => {
            if (e == 'ASC') return 'Newest first';
            if (e == 'DESC') return 'Oldest first';
          }}
          selectTriggerClasses={'w-26'}
          selectContentClasses={'max-h-60'}
        />

        <CustomSelect
          label="Size"
          value={size || 12}
          onChange={setSize}
          options={[12, 24, 48, 100]}
          placeholder="Select Size"
          transformFn={(e) => e}
          selectTriggerClasses={'w-14'}
          selectContentClasses={'max-h-60'}
        />
      </div>
      {/* Apply */}
      <Button variant="primary" className="h-12 text-md" onClick={handleFetchCourses}>
        Search {isLoading && <Loader size={20} className="animate-[spin_2s_linear_infinite]" />}
      </Button>
    </div>
  );
};

export default TeacherRequestFilter;
