import { DisplayCoursesHeadingSkele, DisplayCoursesSkeleton } from '@/components/Common/DisplayUserCourses';
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
