// type Props = {};
'use client';

import Header from '@/components/Dashboard/Header';
import AuthField from '@/components/NonDashboard/auth/AuthField';
import { Button } from '@/components/ui/button';
import { CourseFormData, courseSchema } from '@/lib/validation/course';
import { useAppDispatch, useAppSelector } from '@/state/redux';
import { CourseCategory, CourseStatus, Level } from '@/types/courses-enum';
import { zodResolver } from '@hookform/resolvers/zod';
import { ArrowLeft } from 'lucide-react';
import { useParams, useRouter } from 'next/navigation';
import { Form, useForm } from 'react-hook-form';

const EditCourse = () => {
  const router = useRouter();
  const params = useParams();
  const courseId = params.courseId as string;
  console.log(courseId);

  const dispatch = useAppDispatch();
  const { sections } = useAppSelector((state) => state.global.courseEditor);

  const form = useForm<CourseFormData>({
    resolver: zodResolver(courseSchema),
    defaultValues: {
      status: CourseStatus.DRAFT,
      category: CourseCategory.PROGRAMMING,
      title: '',
      description: '',
      level: Level.BEGINNER,
      price: 0,
      image: '',
    },
  });

  const onSubmit = async (data: CourseFormData) => {
    console.log(data);
  };

  // const course = await getCoursesPublic(courseId);
  return (
    <>
      <div className="flex items-center gap-5 mb-5">
        <button
          className="flex items-center border border-customgreys-dirtyGrey rounded-lg p-2 gap-2 cursor-pointer hover:bg-customgreys-dirtyGrey hover:text-white-100 text-customgreys-dirtyGrey"
          onClick={() => router.push('/teacher/courses', { scroll: false })}>
          <ArrowLeft className="w-4 h-4" />
          <span>Back to Courses</span>
        </button>
      </div>
      <Form {...form}>
        <form onSubmit={form.handleSubmit(onSubmit)}>
          <Header
            title="Course Setup"
            subtitle="Complete all fields and save your course"
            rightElement={
              <div className="flex items-center space-x-4">
                <Button type="submit" className="bg-primary-700 hover:bg-primary-600">
                  {status === CourseStatus.PUBLISHED ? 'Update Published Course' : 'Save Draft'}
                </Button>
              </div>
            }
          />
        </form>
      </Form>
    </>
  );
};

export default EditCourse;
