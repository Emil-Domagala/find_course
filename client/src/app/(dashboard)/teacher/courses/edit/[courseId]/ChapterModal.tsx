import { CustomFormField } from '@/components/Common/CustomFormField';
import CustomModal from '@/components/Common/CustomModal';
import { Button } from '@/components/ui/button';
import { Form } from '@/components/ui/form';
import { ChapterFormData, chapterSchema } from '@/lib/validation/course';

import { addChapter, closeChapterModal, editChapter } from '@/state';
import { useAppDispatch, useAppSelector } from '@/state/redux';
import { ChapterDetailsProtectedDto } from '@/types/courses';
import { zodResolver } from '@hookform/resolvers/zod';
import { X } from 'lucide-react';
import React, { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { toast } from 'sonner';
import { v4 as uuidv4 } from 'uuid';

const ChapterModal = () => {
  const dispatch = useAppDispatch();
  const { isChapterModalOpen, selectedSectionIndex, selectedChapterIndex, sections } = useAppSelector((state) => state.global.courseEditor);

  const chapter: ChapterDetailsProtectedDto | undefined =
    selectedSectionIndex !== null && selectedChapterIndex !== null ? sections[selectedSectionIndex].chapters![selectedChapterIndex] : undefined;

  const methods = useForm<ChapterFormData>({
    resolver: zodResolver(chapterSchema),
    defaultValues: {
      title: '',
      content: '',
      videoUrl: '',
    },
  });

  useEffect(() => {
    if (chapter) {
      methods.reset({
        title: chapter.title,
        content: chapter.content,
        videoUrl: chapter.videoUrl || '',
      });
    } else {
      methods.reset({
        title: '',
        content: '',
        videoUrl: '',
      });
    }
  }, [chapter, methods]);

  const onClose = () => {
    dispatch(closeChapterModal());
  };

  const onSubmit = (data: ChapterFormData) => {
    if (selectedSectionIndex === null) return;

    const newChapter: ChapterDetailsProtectedDto & { tempId?: string } = {
      id: chapter?.id || '',
      tempId: uuidv4(),
      title: data.title,
      content: data.content,
      videoUrl: data.videoUrl,
    };

    if (selectedChapterIndex === null) {
      dispatch(
        addChapter({
          sectionIndex: selectedSectionIndex,
          chapter: newChapter,
        }),
      );
    } else {
      dispatch(
        editChapter({
          sectionIndex: selectedSectionIndex,
          chapterIndex: selectedChapterIndex,
          chapter: newChapter,
        }),
      );
    }

    toast.success(`Chapter added/updated successfully but you need to save the course to apply the changes`);
    onClose();
  };

  return (
    <CustomModal isOpen={isChapterModalOpen} onClose={onClose}>
      <div className="flex flex-col">
        <div className="flex justify-between items-center mb-4">
          <h2 className="text-2xl font-bold">Add/Edit Chapter</h2>
          <button onClick={onClose} className="text-gray-500 hover:text-gray-700">
            <X className="w-6 h-6" />
          </button>
        </div>

        <Form {...methods}>
          <form onSubmit={methods.handleSubmit(onSubmit)} className=" space-y-4">
            <CustomFormField name="title" label="Chapter Title" placeholder="Write chapter title here" />

            <CustomFormField name="content" label="Chapter Content" type="textarea" placeholder="Write chapter content here" />

            <CustomFormField name="videoUrl" label="Video Url" placeholder="Paste video url here" />


{/* TODO: Videos are not supported yet. I will add it later. */}
            {/* <FormField
              control={methods.control}
              name="videoUrl"
              render={({ field: { onChange, value } }) => (
                <FormItem>
                  <FormLabel className="text-customgreys-dirtyGrey text-sm">Chapter Video</FormLabel>
                  <FormControl>
                    <div>
                      <Input
                        type="file"
                        accept="video/*"
                        onChange={(e) => {
                          const file = e.target.files?.[0];
                          if (file) {
                            onChange(file);
                          }
                        }}
                        className="border-none bg-customgreys-darkGrey py-2 cursor-pointer"
                      />
                      {typeof value === 'string' && value && <div className="my-2 text-sm text-gray-600">Current video: {value.split('/').pop()}</div>}
                      {value instanceof File && <div className="my-2 text-sm text-gray-600">Selected file: {value.name}</div>}
                    </div>
                  </FormControl>
                  <FormMessage className="text-red-400" />
                </FormItem>
              )}
            /> */}


            <div className="flex justify-end space-x-2 mt-6">
              <Button type="button" variant="outline" onClick={onClose}>
                Cancel
              </Button>
              <Button type="submit" className="bg-primary-700">
                Save
              </Button>
            </div>
          </form>
        </Form>
      </div>
    </CustomModal>
  );
};

export default ChapterModal;
