import { Skeleton } from '@/components/ui/skeleton';
import CourseCardSearch, { CourseCardSearchSkeleton } from '../CourseCardSearch';

type Props = { isLoading: boolean; coursesPage?: Page<CourseDto>; size: number };

export const DisplayCoursesSkeleton = ({ size }: { size: number }) => {
  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      {[...Array(+size)].map((_, index) => (
        <CourseCardSearchSkeleton key={index} />
      ))}
    </div>
  );
};

export const DisplayCoursesHeadingSkele = () => {
  return (
    <>
      <Skeleton className="w-72 h-8 mb-2 mt-14" />
      <Skeleton className="w-32 h-4 mb-5" />
    </>
  );
};

const DisplayCourses = ({ coursesPage }: Props) => {
  const courses: CourseDto[] = coursesPage?.content ?? [];

  return (
    <>
      <h1 className="font-normal text-2xl mt-14">List of available courses</h1>
      <h2 className="text-gray-500 mb-3">{coursesPage?.totalElements || 0} courses available</h2>

      <div
        className={`grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 ${
          courses?.length === 0 ? '!grid-cols-1' : ''
        }`}>
        {courses && courses.length > 0 ? (
          courses.map((course) => (
            <div key={course.id}>
              <CourseCardSearch course={course} />
            </div>
          ))
        ) : (
          <p className="mx-auto h-full text-lg text-gray-400 mt-5 mb-10">There are no courses yet</p>
        )}
      </div>
    </>
  );
};

export default DisplayCourses;
