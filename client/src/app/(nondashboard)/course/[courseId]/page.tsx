import Tag from '@/components/NonDashboard/Home/Tag';
import { Accordion, AccordionContent, AccordionItem, AccordionTrigger } from '@/components/ui/accordion';
import { Button } from '@/components/ui/button';
import { getCoursesPublic } from '@/lib/api/api';
import { FileText } from 'lucide-react';
import Image from 'next/image';

const CourseDetailPage = async ({ params }: { params: { courseId: string } }) => {
  const { courseId } = params;
  const course = await getCoursesPublic(courseId);
  console.log(course);

  return (
    <>
      <div className="md:bg-customgreys-secondarybg">
        <div className="container max-w-[1200px] flex flex-col-reverse md:flex-row gap-8 h-[500px] py-6">
          {/* Info */}
          <div className="flex flex-col basis-1/2 ">
            <h1 className="text-white-50 font-semibold text-2xl mb-4">{course.courseDto.title}</h1>
            <p className=" text-lg text-gray-400">{course.courseDto.description}</p>

            <p className="text-customgreys-dirtyGrey text-sm">
              Created by: {course.courseDto.teacher.username} {course.courseDto.teacher.userLastname}
            </p>
            <p className='text-customgreys-dirtyGrey text-sm"'>{course.courseDto.studentsCount} Enrollments</p>
            <Tag className="bg-customgreys-dirtyGrey/20 w-fit mt-2">
              {course.courseDto.category}
            </Tag>

            {/* BUY */}

            <span className="text-primary-500 text-xl font-semibold py-4">Only ${course.courseDto.price}</span>
            <div className="flex flex-row gap-2">
              <form action="">
                <Button variant="secondary">Buy Now</Button>
              </form>
              <form action="">
                <Button variant="outline">Add to Cart</Button>
              </form>
            </div>
          </div>
          {/* Image */}

          <div className="relative h-full basis-1/2 overflow-hidden rounded-lg ">
            <Image
              src={course.courseDto.imageUrl || '/placeholder.png'}
              alt={course.courseDto.title}
              fill
              sizes="(max-width: 768px) 100vw, (max-width:1200) 50vw, 33vw"
            />
          </div>
        </div>
      </div>
      {/* Accordeon */}
      <div className="container">
        <h4 className="text-white-50/90 text-lg font-semibold mb-2">Course content</h4>

        {course.sections ? (
          <Accordion type="multiple" className="w-full">
            {course.sections.map((section) => (
              <AccordionItem
                className="border-x border-b border-gray-600 overflow-hidden first:border-t first:rounded-t-lg last:rounded-b-lg"
                key={section.id}
                value={section.title}>
                <AccordionTrigger className="hover:bg-gray-700/50 bg-customgreys-primarybg/50 px-4 py-3">
                  <h5 className="text-gray-400 font-medium">{section.title}</h5>
                </AccordionTrigger>
                <AccordionContent className="bg-customgreys-secondarybg/50 px-4 py-4">
                  <ul>
                    {section.chapter?.map((chapter) => (
                      <li className="flex items-center text-gray-400/90 py-1" key={chapter.id}>
                        <FileText className="mr-2 size-4" />
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
