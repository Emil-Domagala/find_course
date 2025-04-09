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
