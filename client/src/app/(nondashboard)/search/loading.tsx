import { DisplayCoursesHeadingSkele, DisplayCoursesSkeleton } from '@/components/NonDashboard/Search/DisplayCourses';
import { FilterSkeleton } from '../../../components/Common/Filter/FilterSkeleton';

const LoadingSearchPage = () => {
  return (
    <>
      <FilterSkeleton />
      <div className="container">
        <DisplayCoursesHeadingSkele />
        <DisplayCoursesSkeleton size={4} />
      </div>
    </>
  );
};

export default LoadingSearchPage;
