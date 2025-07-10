import { TeacherRequest } from '@/features/dashboard/types/teacherRequest';
import { api } from '../../../../../state/api';

export const teacherApplicationUserApi = api.injectEndpoints({
  endpoints: (build) => ({
    // Seend Become teacher request
    sendTeacherApplication: build.mutation<void, void>({
      query: () => ({ url: 'user/teacher-application', method: 'POST' }),
    }),
    // Get Become teacher request status
    getTeacherApplicationInformation: build.query<TeacherRequest, void>({
      query: () => ({ url: 'user/teacher-application' }),
    }),
  }),
});

export const { useSendTeacherApplicationMutation, useGetTeacherApplicationInformationQuery } = teacherApplicationUserApi;
