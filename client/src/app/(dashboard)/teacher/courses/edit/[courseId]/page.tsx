// type Props = {};

const EditCourse = async ({ params }: { params: { courseId: string } }) => {
  const { courseId } = await params;
  console.log(courseId);
  // const course = await getCoursesPublic(courseId);
  return <h1>Edit Course</h1>;
};

export default EditCourse;
