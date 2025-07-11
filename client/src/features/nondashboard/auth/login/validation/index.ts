import * as z from 'zod';

export const UserLoginSchema = z.object({
  email: z.string().trim().nonempty('Email is required').email({ message: 'Invalid email format' }),
  password: z.string().nonempty('Password is required').min(6, { message: 'At least 6 characters long' }).max(30, { message: 'At most 30 characters long' }),
});

export type UserLoginRequest = z.infer<typeof UserLoginSchema>;
