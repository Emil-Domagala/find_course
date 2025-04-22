import { BecomeTeacherRequestStatus } from './enums';

export type BecomeTeacherRequest = {
  id: string;
  user?: UserDto;
  status: BecomeTeacherRequestStatus;
  createdAt: string;
  seenByAdmin?: boolean;
};

declare global {
  type UserDto = {
    id: string;
    email: string;
    username: string;
    userLastname: string;
    imageUrl?: string;
  };
}

export {};
