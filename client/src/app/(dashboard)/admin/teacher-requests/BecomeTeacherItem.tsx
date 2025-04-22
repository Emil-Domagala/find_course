import { Select } from '@/components/ui/select';
import { transformToFrontendFormat } from '@/lib/utils';
import { BecomeTeacherRequestStatus } from '@/types/enums';
import { BecomeTeacherRequest } from '@/types/user';
import { SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { useState } from 'react';

const BecomeTeacherItem = ({ becomeTeacherRequest }: { becomeTeacherRequest: BecomeTeacherRequest }) => {
  const { user, status, seenByAdmin, createdAt, id } = becomeTeacherRequest;
  const [statusChanged, setStatusChanged] = useState<BecomeTeacherRequestStatus>(status);

  return (
    <li className="flex flex-row items-center justify-between  w-full py-2 px-2 border-b-customgreys-primarybg border-b-[1px] ">
      <div className="w-1/4 text-start">
        <p className="font-semibold">
          {user?.username} {user?.userLastname}
        </p>
      </div>
      <div className="w-1/4 text-center">
        <p>{new Date(createdAt).toLocaleDateString()} </p>
      </div>
      <div className="w-1/4">
        <Select disabled={status !== 'PENDING'} value={String(statusChanged)} onValueChange={(newVal) => setStatusChanged(newVal as BecomeTeacherRequestStatus)}>
          <SelectTrigger className="border-none bg-customgreys-primarybg rounded-md overflow-hidden text-sm px-2 !h-8 w-32 m-auto">
            <SelectValue>
              <p className={`${statusChanged === 'DENIED' ? 'text-red-500' : statusChanged === 'PENDING' ? 'text-yellow-500' : 'text-green-500'}`}>
                {transformToFrontendFormat(statusChanged)}
              </p>
            </SelectValue>
          </SelectTrigger>
          <SelectContent className="border-none mt-1 py-2 bg-customgreys-darkGrey rounded-md" position="popper">
            {Object.values(BecomeTeacherRequestStatus).map((option) => (
              <SelectItem
                className={`group text-center cursor-pointer bg-customgreys-darkGrey min-w-[100%] p-2 hover:bg-customgreys-darkerGrey hover:!outline-none`}
                value={option}
                key={option}>
                <p className={`${option === 'DENIED' ? 'group-hover:text-red-500' : option === 'PENDING' ? 'group-hover:text-yellow-500' : 'group-hover:text-green-500'}`}>
                  {transformToFrontendFormat(option)}
                </p>
              </SelectItem>
            ))}
          </SelectContent>
        </Select>
      </div>

      <div className="w-1/4 flex justify-end">
        <input type="checkbox" name="seenByAdmin" id="seenByAdmin" disabled={seenByAdmin} />
      </div>
    </li>
  );
};

export default BecomeTeacherItem;
