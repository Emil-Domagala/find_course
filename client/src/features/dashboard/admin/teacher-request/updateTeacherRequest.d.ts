export type UpdateTeacherRequest = {
  id: string;
  status?: TeacherRequestStatus;
  seenByAdmin?: boolean;
};
