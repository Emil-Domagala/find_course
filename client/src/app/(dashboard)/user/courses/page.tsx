'use client';
import CourseCard from '@/components/Common/CourseCard';
import Pagination from '@/components/Common/Filter/Pagination';
import Header from '@/components/Dashboard/Header';
import { useLazyGetEnrolledCoursesQuery } from '@/state/api';
import { useEffect, useState } from 'react';

const EnrolledCourses = ({}) => {
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);

  const [getEnrolledCourses, { data: coursesPage, isLoading }] = useLazyGetEnrolledCoursesQuery();

  const handleGetEnrolledCourses = () => {
    return getEnrolledCourses({ page, size });
  };

  useEffect(() => {
    handleGetEnrolledCourses();
  }, [page, size]);

  return (
    <div className="user-courses">
      <Header title="My Courses" subtitle="View your enrolled courses" />
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-7 mt-6 w-full h-full flex-1">
        {coursesPage?.content?.map((course) => (
          <CourseCard key={course.id} course={course} link={`/user/courses/${course.id}`} />
        ))}
      </div>
      <Pagination setPage={setPage} currentPage={page} totalPages={coursesPage?.totalPages} />
    </div>
  );
};

export default EnrolledCourses;
