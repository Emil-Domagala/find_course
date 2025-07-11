import * as z from 'zod';

export const UserRegisterSchema = z.object({
  email: z.string().trim().nonempty('Email is required').email({ message: 'Invalid email format' }),
  username: z
    .string()
    .trim()
    .nonempty('First Name is required')
    .min(3, { message: 'At least 3 characters long' })
    .max(30, { message: 'At most 30 characters long' }),
  userLastname: z
    .string()
    .trim()
    .nonempty('Last Name is required')
    .min(2, { message: 'At least 2 characters long' })
    .max(30, { message: 'At most 30 characters long' }),
  password: z.string().nonempty('Password is required').min(6, { message: 'At least 6 characters long' }).max(30, { message: 'At most 30 characters long' }),
});

export type UserRegisterRequest = z.infer<typeof UserRegisterSchema>;
