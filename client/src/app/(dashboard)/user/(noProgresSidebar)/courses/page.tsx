'use client';
import CourseCard from '@/components/Common/CourseCard';
import Pagination from '@/components/Common/Filter/Pagination';
import { DisplayCoursesSkeleton } from '@/components/NonDashboard/Search/DisplayCourses';
import { useLazyGetEnrolledCoursesQuery } from '@/state/endpoints/course/courseStudent';
import { useEffect, useState } from 'react';

const EnrolledCourses = ({}) => {
  const [page, setPage] = useState<number | undefined>(0);
  const [size] = useState(10);

  const [getEnrolledCourses, { data: coursesPage, isLoading }] = useLazyGetEnrolledCoursesQuery();

  const handleGetEnrolledCourses = () => {
    return getEnrolledCourses({ page, size });
  };

  useEffect(() => {
    handleGetEnrolledCourses();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page, size]);

  return (
    <>
      <div className="flex-1">
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-7 mt-6 w-full h-full flex-1">
          {isLoading ? (
            <DisplayCoursesSkeleton size={4} />
          ) : coursesPage?.content?.length === 0 ? (
            <p className="mx-auto h-full text-lg text-gray-400 mt-5 mb-10">You haven&apos;t enrolled in any courses yet</p>
          ) : (
            coursesPage?.content?.map((course) => (
              <CourseCard
                key={course.id}
                course={course}
                link={`/user/course/${course.id}/chapter/${course.firstChapter}`}
                cardClasses="bg-customgreys-primarybg hover:bg-customgreys-primarybg/60"
              />
            ))
          )}
        </div>
      </div>
      <Pagination setPage={setPage} currentPage={page || 0} totalPages={coursesPage?.totalPages} />
    </>
  );
};

export default EnrolledCourses;
