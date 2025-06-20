import { BecomeTeacherRequest } from '@/types/user';
import { api } from '../../api';

export const teacherApplicationUserApi = api.injectEndpoints({
  endpoints: (build) => ({
    // Seend Become teacher request
    sendTeacherApplication: build.mutation<void, void>({
      query: () => ({ url: 'user/teacher-application', method: 'POST' }),
    }),
    // Get Become teacher request status
    getTeacherApplicationInformation: build.query<BecomeTeacherRequest, void>({
      query: () => ({ url: 'user/teacher-application' }),
    }),
  }),
});

export const { useSendTeacherApplicationMutation, useGetTeacherApplicationInformationQuery } = teacherApplicationUserApi;
