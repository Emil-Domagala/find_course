import { DisplayCoursesHeadingSkele, DisplayCoursesSkeleton } from '@/components/NonDashboard/Search/DisplayCourses';
import { FilterSkeleton } from './FilterSkeleton';

const LoadingSearchPage = () => {
  return (
    <>
      <FilterSkeleton />
      <div className="container">
        <DisplayCoursesHeadingSkele />
        <DisplayCoursesSkeleton size={12} />
      </div>
    </>
  );
};

export default LoadingSearchPage;
