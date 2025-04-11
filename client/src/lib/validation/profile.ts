import * as z from 'zod';

export const profileFormSchema = z.object({
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
  password: z.string().max(30, { message: 'At most 30 characters long' }).optional(),
  image: z.union([z.string(), z.instanceof(File)]).optional(),
});

export type ProfileFormSchema = z.infer<typeof profileFormSchema>;
