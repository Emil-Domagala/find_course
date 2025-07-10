import CourseDetail from '@/features/nondashboard/course';

export default async function CourseDetailsPublicPage({ params }: { params: { courseId: string } }) {
    const { courseId } = await params;
  return <CourseDetail courseId={courseId} />;
}