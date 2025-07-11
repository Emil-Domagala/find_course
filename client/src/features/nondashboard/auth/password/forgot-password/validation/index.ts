import * as z from 'zod';


export const ForgotPasswordShema = z.object({
  email: z.string().trim().nonempty('Email is required').email({ message: 'Invalid email format' }),
});

export type ForgotPasswordRequest = z.infer<typeof ForgotPasswordShema>;