'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import { Form, FormControl, FormDescription, FormField, FormItem, FormLabel, FormMessage } from '@/components/ui/form';
import { UserRegisterRequest, UserRegisterSchema } from '@/lib/validation/userRegister';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import Link from 'next/link';

const RegisterPage = () => {
  const form = useForm<UserRegisterRequest>({
    resolver: zodResolver(UserRegisterSchema),
    defaultValues: {
      email: '',
      username: '',
      userLastname: '',
      password: '',
    },
  });

  const onSubmit = async (values: UserRegisterRequest) => {
    console.log(values);
  };

  return (
    <div className="flex justify-center items-center py-5">
      <Card className="mx-auto shadow-none bg-customgreys-secondarybg border-none px-6 py-10">
        {/* Header */}
        <div className="">
          <h1 className="align-middle font-bold text-center text-xl md:text-2xl mb-4">Create Account</h1>
          <p className="text-md text-stone-400">Welcome! Please fill in the details to get started</p>
        </div>
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)}>
            <FormField
              control={form.control}
              name="username"
              render={({ field }) => (
                <FormItem className="relative pb-4.5">
                  <FormLabel className="text-white-50 font-medium text-md">First name</FormLabel>
                  <FormControl>
                    <Input
                      type="text"
                      {...field}
                      className="bg-customgreys-primarybg text-white-50 !shadow-none border-none font-medium text-md md:text-lg "
                    />
                  </FormControl>
                  {/* {description && showDesc && <FormDescription>{description}</FormDescription>} */}
                  <FormMessage className="text-red-500 text-xs absolute bottom-0 " />
                </FormItem>
              )}
            />
            <Button variant="primary" className="w-full mt-8" type="submit">
              Sign Up
            </Button>
          </form>
        </Form>
        {/* footer */}
        <div className="mx-auto">
          <span className="text-md ">Already have an account? </span>
          <Link
            className="text-primary-750 hover:text-primary-600 text-md transition-colors duration-300"
            href={'/login'}>
            Sign in
          </Link>
        </div>
      </Card>
    </div>
  );
};

export default RegisterPage;
