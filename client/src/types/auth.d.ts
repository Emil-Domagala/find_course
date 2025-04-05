export type UserLoginRequest = {
  email: string;
  password: string;
};

export type AuthToken={  
  sub: string;
    roles: string;
    iat: number;
    exp: number;
  }
