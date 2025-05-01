'use client';

import Filter from '@/components/Common/Filter/Filter';
import Pagination from '@/components/Common/Filter/Pagination';
import Header from '@/components/Dashboard/Header';
import TeacherCourseCard from '@/components/Dashboard/Teacher/TeacherCourseCard';
import { Button } from '@/components/ui/button';
import { SearchDirection, SearchField } from '@/types/enums';
import { useSelectFilter } from '@/hooks/useSelectFilter';
import { useCreateCourseMutation, useDeleteCourseMutation, useLazyGetCoursesTeacherQuery } from '@/state/api';
import { CourseCategory } from '@/types/courses-enum';
import { Loader } from 'lucide-react';
import { useRouter } from 'next/navigation';
import { useEffect, useState } from 'react';
import { toast } from 'sonner';
import { ApiErrorResponse } from '@/types/apiError';

const MyCourses = () => {
  const router = useRouter();
  const [isCreating, setIsCreating] = useState(false);

  const [category, setCategory] = useSelectFilter<CourseCategory>({ valueName: 'category' });
  const [keyword, setKeyword] = useSelectFilter<string>({ valueName: 'keyword' });
  const [sortField, setSortField] = useSelectFilter<SearchField>({
    valueName: 'sortField',
    initialValue: SearchField.CreatedAt,
  });
  const [direction, setDirection] = useSelectFilter<SearchDirection>({
    valueName: 'direction',
    initialValue: SearchDirection.ASC,
  });
  const [size, setSize] = useSelectFilter<number>({ valueName: 'size', initialValue: 12 });
  const [page, setPage] = useSelectFilter<number>({ valueName: 'page', initialValue: 0 });

  const [fetchCourses, { data: coursesPage, isLoading }] = useLazyGetCoursesTeacherQuery();
  const [createCourse] = useCreateCourseMutation();
  const [deleteCourse] = useDeleteCourseMutation();

  const handleFetchCourses = () => {
    return fetchCourses({ page, size, sortField, direction, keyword, category });
  };

  useEffect(() => {
    handleFetchCourses();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page]);

  const handleDelete = (course: CourseDto) => {
    if (window.confirm('Are you sure you want to delete this course?')) {
      deleteCourse({ courseId: course.id });
    }
  };

  const handleCreateCourse = async () => {
    try {
      setIsCreating(true);
      const createdCourse = await createCourse().unwrap();
      toast.success('Course created successfully');
      router.push(`/teacher/courses/edit/${createdCourse?.id}`);
    } catch (e) {
      const errorFull = e as ApiErrorResponse;
      const error = errorFull.data;
      let message = 'Something went wrong';
      if (error.message) {
        message = error.message;
      }
      toast.error(message);
    } finally {
      setIsCreating(false);
    }
  };

  return (
    <div className="flex flex-col w-full min-h-full ">
      <Header
        title="Your Courses"
        subtitle="Manage your courses"
        rightElement={
          <Button onClick={handleCreateCourse} variant="primary" className="p-3 font-medium text-md ">
            Create Course {isCreating && <Loader size={20} className="animate-[spin_2s_linear_infinite]" />}
          </Button>
        }
      />
      <Filter
        category={category}
        setCategory={setCategory}
        keyword={keyword}
        setKeyword={setKeyword}
        sortField={sortField || SearchField.CreatedAt}
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
          {coursesPage?.content?.map((course) => (
            <TeacherCourseCard key={course.id} course={course} onDelete={handleDelete} />
          ))}
        </div>
      </div>
      <Pagination setPage={setPage} currentPage={page || 0} totalPages={coursesPage?.totalPages} />
    </div>
  );
};

export default MyCourses;
