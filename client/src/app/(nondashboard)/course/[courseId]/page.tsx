import Tag from '@/components/NonDashboard/Home/Tag';
import { Accordion, AccordionContent, AccordionItem, AccordionTrigger } from '@/components/ui/accordion';
import { apiServerService } from '@/lib/api/apiServerSide';
import { FileText, Video } from 'lucide-react';
import Image from 'next/image';
import { cookies } from 'next/headers';
import BuyButton from './BuyButton';
import { centsToDollars } from '@/lib/utils';
import { SectionDetailsPublicDto } from '@/types/courses';
import { ChapterType } from '@/types/enums';

const CourseDetailPage = async ({ params }: { params: { courseId: string } }) => {
  const { courseId } = params;
  const course = await apiServerService.getCoursesPublic(courseId);
  const cookieStore = await cookies();
  const authToken = cookieStore.get(process.env.AUTH_COOKIE_NAME as string)?.value;

  return (
    <>
      <div className="md:bg-customgreys-secondarybg">
        <div className="container flex flex-col-reverse md:flex-row gap-8 md:h-[500px] py-6">
          {/* Info */}
          <div className="flex flex-col basis-1/2 ">
            <h1 className="text-white-50 font-semibold text-2xl mb-4">{course.title}</h1>
            <p className=" text-lg text-gray-400 mb-2">{course.description}</p>

            <p className="text-customgreys-dirtyGrey text-sm">
              Created by: {course.teacher.username} {course.teacher.userLastname}
            </p>
            <p className='text-customgreys-dirtyGrey text-sm"'>{course.studentsCount} Enrollments</p>
            <Tag className="bg-customgreys-dirtyGrey/20 w-fit mt-2">{course.category}</Tag>

            {/* BUY */}

            <span className="text-primary-500 text-xl font-semibold py-4">Only ${centsToDollars(course.price)}</span>
            <div className="flex flex-row gap-2">
              <BuyButton courseId={courseId} authToken={authToken} />
            </div>
          </div>
          {/* Image */}

          <div className="relative w-full  basis-1/2 ">
            <Image src={course.imageUrl || '/placeholder.png'} alt={course.title} width={0} height={0} sizes="100vw" className="w-full h-auto rounded-lg" />
          </div>
        </div>
      </div>
      {/* Accordeon */}
      <div className="container">
        <h4 className="text-white-50/90 text-xl font-semibold my-2">Course content</h4>

        {course.sections && course.sections.length > 0 ? (
          <Accordion type="multiple" className="w-full">
            {course.sections.map((section: SectionDetailsPublicDto) => (
              <AccordionItem
                className="border-x border-b border-gray-600 overflow-hidden first:border-t first:rounded-t-lg last:rounded-b-lg"
                key={section.id}
                value={section.title}>
                <AccordionTrigger className="hover:bg-gray-700/50 bg-customgreys-primarybg/50 px-4 py-3">
                  <h5 className="text-gray-400 font-medium">{section.title}</h5>
                </AccordionTrigger>
                <AccordionContent className="bg-customgreys-secondarybg/50 px-4 py-4">
                  <ul>
                    {section.chapters?.map((chapter) => (
                      <li className="flex items-center text-gray-400/90 py-1" key={chapter.id}>
                        {chapter.type === ChapterType.TEXT ? <FileText className="mr-2 size-4" /> : <Video className="mr-2 size-4" />}
                        <span className="text-sm">{chapter.title}</span>
                      </li>
                    ))}
                  </ul>
                </AccordionContent>
              </AccordionItem>
            ))}
          </Accordion>
        ) : (
          <p className="mx-auto text-lg text-gray-400">There are no courses content yet</p>
        )}
      </div>
    </>
  );
};

export default CourseDetailPage;
