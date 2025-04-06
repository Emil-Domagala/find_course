export type UserLoginRequest = {
  email: string;
  password: string;
};

export type AuthToken = {
  sub: string;
  roles: string;
  isEmailVerified: boolean;
  iat: number;
  exp: number;
};
