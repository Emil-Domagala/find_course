import { CoursesSkeleton } from '@/features/nondashboard/home/components/Courses';
import { HomeHeroSkeleton } from '@/features/nondashboard/home/components/HomeHero';
import { TagsSkeleton } from '@/features/nondashboard/home/components/Tags';

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
