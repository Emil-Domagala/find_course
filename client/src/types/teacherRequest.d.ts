import { TeacherRequestStatus } from './search-enums';

export type TeacherRequest = {
  id: string;
  user: UserDto;
  status: TeacherRequestStatus;
  createdAt: string;
  seenByAdmin: boolean;
};

export type UpdateTeacherRequest = {
  id: string;
  status?: TeacherRequestStatus;
  seenByAdmin?: boolean;
};
