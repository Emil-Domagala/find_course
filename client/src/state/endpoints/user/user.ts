import { api } from '../../api';

export const userApi = api.injectEndpoints({
  endpoints: (build) => ({
    // Get User
    getUserInfo: build.query<UserDto, void>({
      query: () => ({ url: 'user' }),
      providesTags: ['User'],
    }),
    // Delete User
    deleteUser: build.mutation<void, void>({
      query: () => ({ url: 'user', method: 'DELETE' }),
      invalidatesTags: ['User'],
    }),
    // Update User
    updateUserInfo: build.mutation<UserDto, FormData>({
      query: (user) => ({ url: 'user', method: 'PATCH', body: user }),
      async onQueryStarted(_arg, { dispatch, queryFulfilled }) {
        try {
          const { data: updatedUser } = await queryFulfilled;
          dispatch(
            // @ts-expect-error code is fine redux error
            api.util.updateQueryData('getUserInfo', undefined, () => {
              return updatedUser;
            }),
          );
        } catch (err) {
          console.error(err);
        }
      },
    }),
  }),
});

export const { useGetUserInfoQuery, useDeleteUserMutation, useUpdateUserInfoMutation } = userApi;
