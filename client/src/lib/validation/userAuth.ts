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

export const UserLoginSchema = z.object({
  email: z.string().trim().nonempty('Email is required').email({ message: 'Invalid email format' }),
  password: z.string().nonempty('Password is required').min(6, { message: 'At least 6 characters long' }).max(30, { message: 'At most 30 characters long' }),
});

export type UserLoginRequest = z.infer<typeof UserLoginSchema>;

export const ForgotPasswordShema = z.object({
  email: z.string().trim().nonempty('Email is required').email({ message: 'Invalid email format' }),
});

export type ForgotPasswordRequest = z.infer<typeof ForgotPasswordShema>;

export const NewPasswordSchema = z
  .object({
    password: z.string().nonempty('Password is required').min(6, { message: 'At least 6 characters long' }).max(30, { message: 'At most 30 characters long' }),
    confirmPassword: z
      .string()
      .nonempty('Confirm Password is required')
      .min(6, { message: 'At least 6 characters long' })
      .max(30, { message: 'At most 30 characters long' }),
  })
  .refine(
    (data) => {
      return data.password === data.confirmPassword;
    },
    {
      message: 'Confirm Password must match Password',
      path: ['confirmPassword'],
    },
  );

export type NewPassword = z.infer<typeof NewPasswordSchema>;
