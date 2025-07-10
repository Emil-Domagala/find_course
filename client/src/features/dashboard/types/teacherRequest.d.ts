import { TeacherRequestStatus } from './teacherRequestStatus';


export type TeacherRequest = {
  id: string;
  user: UserDto;
  status: TeacherRequestStatus;
  createdAt: string;
  seenByAdmin: boolean;
};
