export type AuthToken = {
  sub: string;
  roles: string;
  isEmailVerified: boolean;
  picture?: string;
  iat: number;
  exp: number;
};
