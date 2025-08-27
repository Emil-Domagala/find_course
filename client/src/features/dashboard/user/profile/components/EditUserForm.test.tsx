describe('EditUserForm', () => {
  describe('Form Validation', () => {
    test('Display warnings when invalid data', async () => {});
    test('Delete warnings when data corrected', async () => {});
  });
  describe('Api Interaction', () => {
    describe('useGetUserInfoQuery', () => {
      test('If loading loading state is displayed', async () => {});
      test('User data is displayed', async () => {});
    });
    describe('useDeleteUserMutation', () => {
      test('Displayed confirmation b4 deleting account', async () => {});
      test('Cancel deleting user account does not trigger delete mutation', async () => {});
      test('Submit without password works correctly', async () => {});

      test('Sucessfully deletes user account', async () => {});
      test('Display error msg if error occured', async () => {});
    });
    describe('useUpdateUserInfoMutation', () => {
      test('When data correct sucessfully updates user', async () => {});
      test('Attach image if provided', async () => {});
      test('Delete image if checked', async () => {});
      test('Save Changes button shows spinner during update', async () => {});
      test('Display error msg if error occured', async () => {});
    });
  });
});
