import * as z from 'zod';

export const UserRegisterSchema = z.object({
  email: z.string().trim().email('Invalid email format'),
  username: z
    .string()
    .trim()
    .min(3, { message: 'At least 3 characters long' })
    .max(30, { message: 'At most 30 characters long' }),
  userLastname: z
    .string()
    .trim()
    .min(2, { message: 'At least 2 characters long' })
    .max(30, { message: 'At most 30 characters long' }),
  password: z
    .string()
    .min(6, { message: 'At least 6 characters long' })
    .max(30, { message: 'At most 30 characters long' }),
});

export type UserRegisterRequest = z.infer<typeof UserRegisterSchema>;

export const UserLoginSchema = z.object({
  email: z.string().trim().email('Invalid email format'),
  password: z
    .string()
    .min(6, { message: 'At least 6 characters long' })
    .max(30, { message: 'At most 30 characters long' }),
});

export type UserLoginRequest = z.infer<typeof UserLoginSchema>;
