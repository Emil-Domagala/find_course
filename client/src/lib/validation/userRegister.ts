import * as z from "zod";

export const UserRegisterSchema = z.object({
  email: z.string().email("Invalid email format"),
  username: z.string({message:"Username is required"}).min(3,{message:"Username must be at least 3 characters long"}).max(30,{message:"Username must be at most 30 characters long"}),
  userLastname: z.string({message:"Lastname is required"}).min(2,{message:"Lastname must be at least 2 characters long"}).max(30,{message:"Lastname must be at most 30 characters long"}),
  password: z.string({message:"Password is required"}).min(6,{message:"Password must be at least 6 characters long"}).max(30,{message:"Password must be at most 30 characters long"}),
});

export type UserRegisterRequest = z.infer<typeof UserRegisterSchema>;