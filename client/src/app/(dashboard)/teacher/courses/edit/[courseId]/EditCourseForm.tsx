'use client';

import { CustomFormField } from '@/components/Common/CustomFormField';
import Header from '@/components/Dashboard/Header';
import { Button } from '@/components/ui/button';
import { CourseFormData, courseSchema } from '@/lib/validation/course';
import { useAppDispatch, useAppSelector } from '@/state/redux';
import { CourseCategory, CourseStatus, Level } from '@/types/courses-enum';
import { zodResolver } from '@hookform/resolvers/zod';
import { ArrowLeft, Plus } from 'lucide-react';
import { useRouter } from 'next/navigation';
import { useForm } from 'react-hook-form';
import { Form } from '@/components/ui/form';
import { useEffect } from 'react';
import { openSectionModal, setSections } from '@/state';
import { transformToFrontendFormat } from '@/lib/utils';

const EditCourseForm = ({ course }: { course: CourseDetailsPublicDto }) => {
  const router = useRouter();

  console.log(course);

  const dispatch = useAppDispatch();
  const { sections } = useAppSelector((state) => state.global.courseEditor);

  const methods = useForm<CourseFormData>({
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

  useEffect(() => {
    console.log(methods.formState.errors);
  }, [methods.formState.errors]);

  useEffect(() => {
    if (course) {
      methods.reset({
        title: course.courseDto.title,
        description: course.courseDto.description,
        category: course.courseDto.category,
        price: course.courseDto.price,
        status: course.courseDto.status,
        level: course.courseDto.level,
      });
      dispatch(setSections(course.sections || []));
    }
  }, [course, methods]);

  const onSubmit = async (data: CourseFormData) => {
    console.log('Submitting');
    console.log(data);
  };

  // const course = await getCoursesPublic(courseId);
  return (
    <>
      <div className="flex items-center gap-5 mb-5">
        <button
          className="flex items-center border border-customgreys-dirtyGrey rounded-lg p-2 gap-2 cursor-pointer hover:bg-customgreys-dirtyGrey hover:text-white-100 text-customgreys-dirtyGrey"
          onClick={() => router.push('/teacher/courses/my-courses', { scroll: false })}>
          <ArrowLeft className="w-4 h-4" />
          <span>Back to Courses</span>
        </button>
      </div>
      <Form {...methods}>
        <form onSubmit={methods.handleSubmit(onSubmit)}>
          <Header
            title="Course Setup"
            subtitle="Complete all fields and save your course"
            rightElement={
              <div className="flex flex-col-reverse md:flex-row gap-4 md:gap-0 items-center space-x-4">
                <CustomFormField
                  options={Object.values(CourseStatus).map((status) => ({
                    value: status,
                    label: transformToFrontendFormat(status),
                  }))}
                  label=""
                  name="status"
                  type="select"
                  className="flex items-center space-x-2"
                  initialValue={course?.courseDto.status}
                />
                <Button type="submit" className="bg-primary-700 hover:bg-primary-600">
                  {methods.watch('status') === CourseStatus.PUBLISHED ? 'Update Course' : 'Save Draft'}
                </Button>
              </div>
            }
          />

          <div className="flex justify-between md:flex-row flex-col gap-10 mt-5 font-dm-sans">
            <div className="basis-1/2">
              <div className="space-y-4 max-h-64 ">
                <CustomFormField
                  name="title"
                  label="Course Title"
                  type="text"
                  placeholder="Write course title here"
                  className="border-none"
                  initialValue={course?.courseDto.title}
                />

                <CustomFormField
                  name="description"
                  label="Course Description"
                  type="textarea"
                  placeholder="Write course description here"
                  inputClassName="max-h-[50vh]"
                  initialValue={course?.courseDto.description}
                />

                <CustomFormField
                  name="category"
                  label="Course Category"
                  type="select"
                  placeholder="Select category here"
                  options={Object.values(CourseCategory).map((category) => ({
                    value: category,
                    label: transformToFrontendFormat(category),
                  }))}
                  initialValue={course?.courseDto.category}
                />
                <CustomFormField
                  name="level"
                  label="Course Level"
                  type="select"
                  placeholder="Select level here"
                  options={Object.values(Level).map((level) => ({
                    value: level,
                    label: transformToFrontendFormat(level),
                  }))}
                  initialValue={course?.courseDto.level}
                />

                <CustomFormField
                  name="price"
                  label="Course Price"
                  type="number"
                  placeholder="0"
                  initialValue={course?.courseDto.price}
                />
              </div>
            </div>

            <div className="basis-1/2">
              <CustomFormField
                name="image"
                label="Course Image"
                type="file"
                accept="image/*"
                isPicture
                initialValue={course?.courseDto.imageUrl}
              />
              <div className="bg-customgreys-darkGrey mt-4 md:mt-0 p-4 rounded-lg">
                <div className="flex justify-between items-center mb-2">
                  <h2 className="text-2xl font-semibold text-secondary-foreground">Sections</h2>

                  <Button
                    type="button"
                    variant="outline"
                    size="sm"
                    onClick={() => dispatch(openSectionModal({ sectionIndex: null }))}
                    className="border-none text-primary-700 group">
                    <Plus className="mr-1 h-4 w-4 text-primary-700 group-hover:white-100" />
                    <span className="text-primary-700 group-hover:white-100">Add Section</span>
                  </Button>
                </div>
                {/* 
              {isLoading ? (
                <p>Loading course content...</p>
              ) : sections.length > 0 ? (
                <DroppableComponent />
              ) : (
                <p>No sections available</p>
              )} */}
              </div>
            </div>
          </div>
        </form>
      </Form>
    </>
  );
};

export default EditCourseForm;
