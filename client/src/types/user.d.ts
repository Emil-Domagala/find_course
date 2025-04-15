export type BecomeTeacherRequest = {
  user?: UserDto;
  status: 'PENDING' | 'ACCEPTED' | 'DENIED';
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
