import { Select } from '@/components/ui/select';
import { transformToFrontendFormat } from '@/lib/utils';
import { SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { useState } from 'react';
import { TeacherRequest } from '@/features/dashboard/types/teacherRequest';
import { UpdateTeacherRequest } from '../updateTeacherRequest';
import { TeacherRequestStatus } from '@/features/dashboard/types/teacherRequestStatus';

const BecomeTeacherItem = ({
  becomeTeacherRequest,
  handleAddDataToSend,
}: {
  becomeTeacherRequest: TeacherRequest;
  handleAddDataToSend: (itemId: string, change: Partial<UpdateTeacherRequest>) => void;
}) => {
  const { user, status, seenByAdmin, createdAt, id } = becomeTeacherRequest;
  const [statusChanged, setStatusChanged] = useState<TeacherRequestStatus>(status);
  const [seenByAdminChanged, setSeenByAdminChanged] = useState<boolean>(seenByAdmin || false);

  const handleChangeStatus = (newStatus: TeacherRequestStatus) => {
    const newSeen = newStatus !== TeacherRequestStatus.PENDING;
    setStatusChanged(newStatus);
    handleAddDataToSend(id, { status: newStatus, seenByAdmin: newSeen });
    setSeenByAdminChanged(newSeen);
  };
  const handleChangeSeenByAdmin = (newSeen: boolean) => {
    setSeenByAdminChanged(newSeen);
    handleAddDataToSend(id, { seenByAdmin: newSeen });
  };

  return (
    <li className="flex flex-row items-center justify-between  w-full py-2 px-2 border-b-customgreys-primarybg border-b-[1px] ">
      <div className="w-1/4 text-start">
        <p className="font-semibold">
          {user.username} {user.userLastname}
        </p>
      </div>
      <div className="w-1/4 text-center">
        <p>{new Date(createdAt).toLocaleDateString()} </p>
      </div>
      <div className="w-1/4">
        <Select disabled={status !== 'PENDING'} value={String(statusChanged)} onValueChange={(newVal) => handleChangeStatus(newVal as TeacherRequestStatus)}>
          <SelectTrigger
            aria-label={`change-status-for-${user.email}`}
            className="border-none bg-customgreys-primarybg rounded-md overflow-hidden text-sm px-2 !h-8 w-32 m-auto">
            <SelectValue>
              <p className={`${statusChanged === 'DENIED' ? 'text-red-500' : statusChanged === 'PENDING' ? 'text-yellow-500' : 'text-green-500'}`}>
                {transformToFrontendFormat(statusChanged)}
              </p>
            </SelectValue>
          </SelectTrigger>
          <SelectContent className="border-none mt-1 py-2 bg-customgreys-darkGrey rounded-md" position="popper">
            {Object.values(TeacherRequestStatus).map((option) => (
              <SelectItem
                className={`group text-center cursor-pointer bg-customgreys-darkGrey min-w-[100%] p-2 hover:bg-customgreys-darkerGrey hover:!outline-none`}
                value={option}
                key={option}>
                <p
                  className={`${
                    option === 'DENIED' ? 'group-hover:text-red-500' : option === 'PENDING' ? 'group-hover:text-yellow-500' : 'group-hover:text-green-500'
                  }`}>
                  {transformToFrontendFormat(option)}
                </p>
              </SelectItem>
            ))}
          </SelectContent>
        </Select>
      </div>

      <div className="w-1/4 flex justify-end">
        <input
          aria-label={`seen-by-admin-for-${user.email}`}
          checked={seenByAdminChanged}
          type="checkbox"
          name="seenByAdmin"
          id="seenByAdmin"
          disabled={seenByAdmin}
          onChange={(e) => handleChangeSeenByAdmin(e.target.checked)}
        />
      </div>
    </li>
  );
};

export default BecomeTeacherItem;
