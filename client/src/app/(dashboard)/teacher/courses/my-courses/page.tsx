'use client';

import Filter from '@/components/Common/Filter/Filter';
import Pagination from '@/components/Common/Filter/Pagination';
import Header from '@/components/Dashboard/Header';
import TeacherCourseCard from '@/components/Dashboard/Teacher/TeacherCourseCard';
import { SearchDirection, CourseDtoSortField } from '@/types/search-enums';
import { useSelectFilter } from '@/hooks/useSelectFilter';
import { CourseCategory } from '@/types/courses-enum';
import { useRouter } from 'next/navigation';
import { useEffect } from 'react';
import { toast } from 'sonner';
import { ApiErrorResponse } from '@/types/apiError';
import { useCreateCourseMutation, useDeleteCourseMutation, useLazyGetCoursesTeacherQuery } from '@/state/endpoints/course/courseTeacher';
import ButtonWithSpinner from '@/components/Common/ButtonWithSpinner';
import { DisplayCoursesSkeleton } from '@/components/NonDashboard/Search/DisplayCourses';

const MyCoursesTeacherPage = () => {
  const router = useRouter();

  const [category, setCategory] = useSelectFilter<CourseCategory>({ valueName: 'category' });
  const [keyword, setKeyword] = useSelectFilter<string>({ valueName: 'keyword' });
  const [sortField, setSortField] = useSelectFilter<CourseDtoSortField>({
    valueName: 'sortField',
    initialValue: CourseDtoSortField.CreatedAt,
  });
  const [direction, setDirection] = useSelectFilter<SearchDirection>({
    valueName: 'direction',
    initialValue: SearchDirection.ASC,
  });
  const [size, setSize] = useSelectFilter<number>({ valueName: 'size', initialValue: 12 });
  const [page, setPage] = useSelectFilter<number>({ valueName: 'page', initialValue: 0 });

  const [fetchCourses, { data: coursesPage, isLoading }] = useLazyGetCoursesTeacherQuery();
  const [createCourse, { isLoading: isCreating }] = useCreateCourseMutation();
  const [deleteCourse] = useDeleteCourseMutation();

  const handleFetchCourses = () => {
    return fetchCourses({ page, size, sortField, direction, keyword, category });
  };

  useEffect(() => {
    handleFetchCourses();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page]);

  const handleDelete = async (course: CourseDto) => {
    if (window.confirm('Are you sure you want to delete this course?')) {
      try {
        await deleteCourse({ courseId: course.id }).unwrap();
        toast.success('Course deleted successfully');
      } catch (e: unknown) {
        const errorMessage = (e as ApiErrorResponse)?.data?.message || (e instanceof Error ? e.message : 'Something went wrong, try again later');
        toast.error('Deleting Course: ' + errorMessage);
      }
    }
  };

  const handleCreateCourse = async () => {
    try {
      const createdCourse = await createCourse().unwrap();
      toast.success('Course created successfully');
      router.push(`/teacher/courses/edit/${createdCourse?.id}`);
    } catch (e: unknown) {
      const errorMessage = (e as ApiErrorResponse)?.data?.message || (e instanceof Error ? e.message : 'Something went wrong, try again later');
      toast.error(errorMessage);
    }
  };

  return (
    <div className="flex flex-col w-full min-h-full ">
      <Header
        title="Your Courses"
        subtitle="Manage your courses"
        rightElement={
          <ButtonWithSpinner isLoading={isCreating} onClick={handleCreateCourse}>
            Create Course
          </ButtonWithSpinner>
        }
      />
      <Filter
        category={category}
        setCategory={setCategory}
        keyword={keyword}
        setKeyword={setKeyword}
        sortField={sortField || CourseDtoSortField.CreatedAt}
        setSortField={setSortField}
        direction={direction || SearchDirection.ASC}
        setDirection={setDirection}
        size={size || 12}
        setSize={setSize}
        handleFetchCourses={handleFetchCourses}
        isLoading={isLoading}
      />

      <div className="flex-1">
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-7 mt-6 ">
          {isLoading ? (
            <DisplayCoursesSkeleton size={4} />
          ) : coursesPage?.content?.length === 0 ? (
            <p aria-label="no courses found" className="mx-auto h-full text-lg text-gray-400 mt-5 mb-10">
              You haven&apos;t created any course yet
            </p>
          ) : (
            coursesPage?.content?.map((course) => <TeacherCourseCard key={course.id} course={course} onDelete={handleDelete} />)
          )}
        </div>
      </div>
      <Pagination setPage={setPage} currentPage={page || 0} totalPages={coursesPage?.totalPages || 0} />
    </div>
  );
};

export default MyCoursesTeacherPage;
