import Header from '@/components/Dashboard/Header';
import EnrolledCourses from './page';

const EnrolledCoursesLayout = () => {
  return (
    <div className="flex flex-col w-full min-h-full">
      <Header title="My Courses" subtitle="View your enrolled courses" />
        <EnrolledCourses />
    </div>
  );
};

export default EnrolledCoursesLayout;
