import { TeacherRequestStatus } from '@/types/search-enums';
import { TeacherRequest } from '@/types/teacherRequest';
import { createUserDto } from './userFactory';
import { v4 as uuid } from 'uuid';

export function createTeacherRequest(overrides: Partial<TeacherRequest> = {}): TeacherRequest {
  return {
    id: overrides.id ?? uuid(),
    user: overrides.user ?? createUserDto(),
    status: overrides.status ?? TeacherRequestStatus.PENDING,
    createdAt: overrides.createdAt ?? '',
    seenByAdmin: overrides.seenByAdmin ?? false,
  };
}
