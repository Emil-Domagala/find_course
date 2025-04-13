'use client';

import Filter from '@/components/Common/Filter/Filter';
import Pagination from '@/components/Common/Filter/Pagination';
import Header from '@/components/Dashboard/Header';
import TeacherCourseCard from '@/components/Dashboard/Teacher/TeacherCourseCard';
import { Button } from '@/components/ui/button';
import { useSearchFilters } from '@/hooks/useSearchFilters';
import { useCreateCourseMutation, useDeleteCourseMutation, useLazyGetCoursesTeacherQuery } from '@/state/api';
import { Loader } from 'lucide-react';
import { useRouter } from 'next/navigation';
import { useEffect, useState } from 'react';

const MyCourses = () => {
  const router = useRouter();
  const [isCreating, setIsCreating] = useState(false);

  const {
    size,
    setSize,
    page,
    setPage,
    sortField,
    setSortField,
    direction,
    setDirection,
    keyword,
    setKeyword,
    category,
    setCategory,
  } = useSearchFilters();

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
      console.log('course deleted' + course.id);
      deleteCourse({ courseId: course.id });
    }
  };

  const handleCreateCourse = async () => {
    try {
      setIsCreating(true);
      const createdCourse = await createCourse().unwrap();
      router.push(`/teacher/courses/edit/${createdCourse?.id}`);
    } catch (e) {
      console.log(e);
    } finally {
      setIsCreating(false);
    }
  };

  return (
    <div className=" w-full h-full">
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
        sortField={sortField}
        setSortField={setSortField}
        direction={direction}
        setDirection={setDirection}
        size={size}
        setSize={setSize}
        handleFetchCourses={handleFetchCourses}
        isLoading={isLoading}
      />

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-7 mt-6 w-full h-full flex-1">
        {coursesPage?.content?.map((course) => (
          <TeacherCourseCard key={course.id} course={course} onDelete={handleDelete} />
        ))}
      </div>
      <Pagination setPage={setPage} page={page} coursesPage={coursesPage} />
    </div>
  );
};

export default MyCourses;
