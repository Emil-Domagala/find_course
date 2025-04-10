import { getCoursesPublic } from '@/lib/api/api';
import EditCourseForm from './EditCourseForm';

const EditCoursePage = async ({ params }: { params: { courseId: string } }) => {
  const { courseId } = await params;
  const course = await getCoursesPublic(courseId);

  console.log(course);

  // return <EditCourseForm course={course} />;
};
export default EditCoursePage;
