import EditCourseForm from './EditCourseForm';

const EditCoursePage = ({ params }: { params: { courseId: string } }) => {
  const { courseId } = params;

  return <EditCourseForm courseId={courseId as string} />;
};
export default EditCoursePage;
