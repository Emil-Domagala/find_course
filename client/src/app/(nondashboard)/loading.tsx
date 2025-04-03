import { CoursesSkeleton } from '@/components/NonDashboard/Home/Courses';
import { HomeHeroSkeleton } from '@/components/NonDashboard/Home/HomeHero';
import { TagsSkeleton } from '@/components/NonDashboard/Home/Tags';

const HomeLoading = () => {
  return (
    <div className="container">
      <HomeHeroSkeleton />
      <TagsSkeleton />
      <CoursesSkeleton size={4} />
    </div>
  );
};

export default HomeLoading;
