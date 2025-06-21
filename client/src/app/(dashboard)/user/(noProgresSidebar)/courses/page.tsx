'use client';
import CourseCard from '@/components/Common/CourseCard';
import Pagination from '@/components/Common/Filter/Pagination';
import Header from '@/components/Dashboard/Header';
import { useLazyGetEnrolledCoursesQuery } from '@/state/endpoints/course/courseStudent';
import { useEffect, useState } from 'react';

const EnrolledCourses = ({}) => {
  const [page, setPage] = useState<number | undefined>(0);
  const [size] = useState(10);

  const [getEnrolledCourses, { data: coursesPage }] = useLazyGetEnrolledCoursesQuery();

  const handleGetEnrolledCourses = () => {
    return getEnrolledCourses({ page, size });
  };

  useEffect(() => {
    handleGetEnrolledCourses();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page, size]);

  return (
    <div className="flex flex-col w-full min-h-full">
      <Header title="My Courses" subtitle="View your enrolled courses" />
      <div className="flex-1">
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-7 mt-6 w-full h-full flex-1">
          {coursesPage?.content?.map((course) => (
            <CourseCard
              key={course.id}
              course={course}
              link={`/user/course/${course.id}/chapter/${course.firstChapter}`}
              cardClasses="bg-customgreys-primarybg hover:bg-customgreys-primarybg/60"
            />
          ))}
        </div>
      </div>
      <Pagination setPage={setPage} currentPage={page || 0} totalPages={coursesPage?.totalPages} />
    </div>
  );
};

export default EnrolledCourses;
