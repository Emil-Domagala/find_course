'use client';

import { CustomFormField } from '@/components/Common/CustomFormField';
import Header from '@/components/Dashboard/Header';
import { Button } from '@/components/ui/button';
import { CourseFormData, courseSchema } from '@/lib/validation/course';
import { useAppDispatch, useAppSelector } from '@/state/redux';
import { CourseCategory, CourseStatus, Level } from '@/types/courses-enum';
import { zodResolver } from '@hookform/resolvers/zod';
import { ArrowLeft, Loader, Plus } from 'lucide-react';
import { useParams, useRouter } from 'next/navigation';
import { useForm } from 'react-hook-form';
import { Form } from '@/components/ui/form';
import { useEffect } from 'react';
import { openSectionModal, setSections } from '@/state';
import { transformToFrontendFormat } from '@/lib/utils';
import CustomAddImg from '@/components/Common/CustomAddImg';
import { useGetTeacherCourseByIdQuery, useUpdateCourseMutation } from '@/state/api';
import SectionModal from './SectionModal';
import DroppableComponent from './DroppableComponent';
import ChapterModal from './ChapterModal';
import { toast } from 'sonner';
import { ApiErrorResponse } from '@/types/apiError';

const EditCourseForm = () => {
  const { courseId }: { courseId: string } = useParams();
  const router = useRouter();

  const { data: course, isLoading } = useGetTeacherCourseByIdQuery(courseId as string, { skip: !courseId });
  const [updateCourse] = useUpdateCourseMutation();

  const dispatch = useAppDispatch();
  const { sections } = useAppSelector((state) => state.global.courseEditor);

  const methods = useForm<CourseFormData>({
    resolver: zodResolver(courseSchema),
    defaultValues: {
      status: undefined,
      category: undefined,
      title: '',
      description: '',
      level: undefined,
      price: 0,
      image: '',
    },
  });

  useEffect(() => {
    if (course) {
      methods.reset({
        title: course.title,
        description: course.description,
        category: course.category,
        price: +course.price,
        status: course.status,
        level: course.level,
        image: course.imageUrl,
      });
      dispatch(setSections(course.sections || []));
    }
  }, [course, methods]);

  const onSubmit = async (data: CourseFormData) => {
    const createCoursePayload = {
      id: courseId,
      title: data.title,
      description: data.description,
      category: data.category,
      price: +data.price,
      status: data.status,
      level: data.level,
      sections: sections,
    };

    const formData = new FormData();
    const courseDataBlob = new Blob([JSON.stringify(createCoursePayload)], {
      type: 'application/json',
    });

    formData.append('courseData', courseDataBlob);

    if (data.image) formData.append('image', data.image);
    try {
      await updateCourse({ courseData: formData, courseId }).unwrap();
      toast.success('Course Updated Successfully');
    } catch (e) {
      const errorFull = e as ApiErrorResponse;
      const error = errorFull.data;
      let message = 'Something went wrong';
      if (error.message) {
        message = error.message;
      }
      toast.error(message);
    }
  };

  if (isLoading || !course) return <h1>LOADING</h1>;

  const displayImageUrl = course?.imageUrl || '/placeholder.png';

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
                  className="flex items-center pb-0"
                  initialValue={course?.status}
                />
                <Button type="submit" className="bg-primary-700 hover:bg-primary-600" disabled={isLoading}>
                  {methods.watch('status') == CourseStatus.PUBLISHED ? 'Update Course' : 'Save Draft'}
                  {isLoading && <Loader size={20} className="animate-[spin_2s_linear_infinite]" />}
                </Button>
              </div>
            }
          />

          <div className="flex justify-between md:flex-row flex-col gap-10 mt-5 font-dm-sans">
            <div className="basis-1/2">
              <div className="space-y-4 max-h-64 ">
                <CustomFormField name="title" label="Title" type="text" placeholder="Write course title here" className="border-none" initialValue={course?.title} />

                <CustomFormField
                  name="description"
                  label="Description"
                  type="textarea"
                  placeholder="Write course description here"
                  inputClassName="max-h-[50vh]"
                  initialValue={course?.description}
                />

                <CustomFormField
                  name="category"
                  label="Category"
                  type="select"
                  placeholder="Select category here"
                  options={Object.values(CourseCategory).map((category) => ({
                    value: category,
                    label: transformToFrontendFormat(category),
                  }))}
                  initialValue={course?.category}
                />
                <CustomFormField
                  name="level"
                  label="Level"
                  type="select"
                  placeholder="Select level here"
                  options={Object.values(Level).map((level) => ({
                    value: level,
                    label: transformToFrontendFormat(level),
                  }))}
                  initialValue={course?.level}
                />

                <CustomFormField name="price" label="Price in cents" type="number" placeholder={'0'} initialValue={+course?.price} />
              </div>
            </div>

            <div className="basis-1/2">
              <div className="flex items-center mb-4 w-full ">
                <CustomAddImg
                  className="w-full h-full max-w-[600px] mx-auto"
                  aspect={16 / 9}
                  cropShape="rect"
                  name="image"
                  maxImgDimetion={800}
                  maxImageSizeMB={0.5}
                  imageUrl={displayImageUrl}
                  imgOnDelete="/placeholder.png"
                  deletable={false}
                />
              </div>
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

                {isLoading ? <p>Loading course content...</p> : sections.length > 0 ? <DroppableComponent /> : <p>No sections available</p>}
              </div>
            </div>
          </div>
        </form>
      </Form>
      <SectionModal />
      <ChapterModal />
    </>
  );
};

export default EditCourseForm;
