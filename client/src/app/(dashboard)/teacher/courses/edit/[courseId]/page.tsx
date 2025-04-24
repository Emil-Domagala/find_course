// import { apiServerService } from '@/lib/api/apiServerSide';
import EditCourseForm from './EditCourseForm';

const EditCoursePage = async ({ params }: { params: { courseId: string } }) => {
  const { courseId } = await params;

  return <EditCourseForm courseId={courseId} />;
};
export default EditCoursePage;
