'use client';

import Filter from '@/components/Common/Filter/Filter';
import Pagination from '@/components/Common/Filter/Pagination';
import Header from '@/components/Dashboard/Header';
import TeacherCourseCard from '@/components/Dashboard/Teacher/TeacherCourseCard';
import { Button } from '@/components/ui/button';
import { useSearchFilters } from '@/hooks/useSearchFilters';
import { useCreateCourseMutation, useLazyGetCoursesTeacherQuery } from '@/state/api';
import { Loader } from 'lucide-react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { useEffect } from 'react';

const MyCourses = () => {
  const router = useRouter();

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
  const [createCourse, { data: createdCourse, isLoading: isCreating }] = useCreateCourseMutation();

  const handleFetchCourses = () => {
    return fetchCourses({ page, size, sortField, direction, keyword, category });
  };

  useEffect(() => {
    handleFetchCourses();
  }, []);

  const handleDelete = (course: CourseDto) => {
    if (window.confirm('Are you sure you want to delete this course?')) {
      console.log('course deleted' + course.id);
    }
  };

  const handleCreateCourse = async () => {
    await createCourse();
    router.push(`/teacher/courses/edit/${createdCourse?.id}`);
  };

  return (
    <div className=" w-full h-full">
      <Header
        title="Your Courses"
        subtitle="Manage your courses"
        rightElement={
          <Button onClick={handleCreateCourse} variant="primary" className="p-3 text-white-50 font-medium text-md ">
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
