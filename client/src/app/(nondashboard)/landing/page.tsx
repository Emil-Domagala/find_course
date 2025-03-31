'use client';
import LandingHero, { LandingHeroSkeleton } from '@/components/NonDashboard/Landing/LandingHero';
import { motion } from 'framer-motion';
import Tags, { TagsSkeleton } from '@/components/NonDashboard/Landing/Tags';
import { useGetCoursesPublicQuery } from '@/state/api';

const LoadingSkeleton = () => {
  return (
    <>
      <LandingHeroSkeleton />
      <TagsSkeleton/>
    </>
  );
};

const Landing = () => {
  const {data:coursesPage,isLoading}=useGetCoursesPublicQuery({size:4})
const courses: CourseDto[] = coursesPage?.content ?? [];

  if(isLoading||!coursesPage)return <LoadingSkeleton/>
  
  return (
    <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ duration: 0.3 }}>
      <LandingHero />
      <Tags />

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      {courses && courses.length > 0
        ? courses.map((course,index) => (
            <motion.div
              initial={{ y: 50, opacity: 0 }}
              whileInView={{ y: 0, opacity: 1 }}
              transition={{ duration: 0.3,delay:index*0.2 }}
              viewport={{ amount: 0.3, once: true }}
              key={course.id}></motion.div>
          ))
        : 'There are no courses yet'}
    </div>

    </motion.div>
  );
};

export default Landing;
