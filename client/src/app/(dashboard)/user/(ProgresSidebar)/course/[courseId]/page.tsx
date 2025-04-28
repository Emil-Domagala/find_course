const WatchCoursePage = async ({ params }: { params: { courseId: string } }) => {
  const { courseId } = await params;
  return (
    <div className={''}>
      <p>{courseId}</p>
    </div>
  );
};

export default WatchCoursePage;
