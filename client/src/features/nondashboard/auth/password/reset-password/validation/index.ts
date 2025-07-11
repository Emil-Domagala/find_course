import * as z from 'zod';

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
