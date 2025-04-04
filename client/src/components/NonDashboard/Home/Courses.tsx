'use client';
import CourseCardSearch, { CourseCardSearchSkeleton } from '../CourseCardSearch';
import { motion } from 'framer-motion';
import { useGetCoursesPublicQuery } from '@/state/api';

export const CoursesSkeleton = ({ size }: { size: number }) => {
  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      {[...Array(+size)].map((_, index) => (
        <CourseCardSearchSkeleton key={index} />
      ))}
    </div>
  );
};

const Courses = () => {
  const desiredSize = 4;

  const { data: coursesPage, isLoading } = useGetCoursesPublicQuery({ size: desiredSize, page: 0 });
  const courses: CourseDto[] = coursesPage?.content ?? [];

  if (isLoading) return <CoursesSkeleton size={desiredSize} />;

  return (
    <div
      className={`grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 ${courses.length === 0 ? '!grid-cols-1' : ''}`}>
      {courses && courses.length > 0 ? (
        courses.map((course, index) => (
          <motion.div
            initial={{ y: 30, opacity: 0 }}
            whileInView={{ y: 0, opacity: 1 }}
            transition={{ duration: 0.3, delay: index * 0.2 }}
            viewport={{ amount: 0.1, once: true }}
            key={course.id}>
            <CourseCardSearch course={course} />
          </motion.div>
        ))
      ) : (
        <p className="mx-auto text-lg text-gray-400">There are no courses yet</p>
      )}
    </div>
  );
};

export default Courses;
