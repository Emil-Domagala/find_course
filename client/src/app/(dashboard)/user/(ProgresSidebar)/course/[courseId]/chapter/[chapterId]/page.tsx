const WatchCoursePage = async ({ params }: { params: { courseId: string; chapterId: string } }) => {
  const { courseId, chapterId } = await params;
  return (
    <div className={''}>
      <p>courseId: {courseId}</p>
      <p>chapterId: {chapterId}</p>
    </div>
  );
};

export default WatchCoursePage;
