import EditCourseForm from './EditCourseForm';

type EditCoursePageProps = {
  params: { courseId: string };
  searchParams?: { [key: string]: string | string[] | undefined };
};

const EditCoursePage = ({ params }: EditCoursePageProps) => {
  const { courseId } = params;

  return <EditCourseForm courseId={courseId} />;
};
export default EditCoursePage;
