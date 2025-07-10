import { CustomFormField } from '@/components/Common/CustomFormField';
import CustomModal from '@/components/Common/CustomModal';

import { Button } from '@/components/ui/button';
import { Form } from '@/components/ui/form';
import { SectionFormData, sectionSchema } from '@/lib/validation/course';
import { addSection, closeSectionModal, editSection } from '@/state';
import { useAppDispatch, useAppSelector } from '@/state/redux';
import { zodResolver } from '@hookform/resolvers/zod';
import { X } from 'lucide-react';
import React, { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { toast } from 'sonner';
import { v4 as uuidv4 } from 'uuid';

const SectionModal = () => {
  const dispatch = useAppDispatch();
  const { isSectionModalOpen, selectedSectionIndex, sections } = useAppSelector((state) => state.global.courseEditor);

  const section = selectedSectionIndex !== null ? sections[selectedSectionIndex] : null;

  const methods = useForm<SectionFormData>({
    resolver: zodResolver(sectionSchema),
    defaultValues: {
      title: '',
      description: '',
    },
  });

  useEffect(() => {
    if (section) {
      methods.reset({
        title: section.title,
        description: section.description,
      });
    } else {
      methods.reset({
        title: '',
        description: '',
      });
    }
  }, [section, methods]);

  const onClose = () => {
    dispatch(closeSectionModal());
  };

  const onSubmit = (data: SectionFormData) => {
    const newSection = {
      id: section?.id || undefined,
      tempId: section?.id ? undefined : uuidv4(),
      title: data.title,
      description: data.description,
      chapters: section?.chapters || [],
    };

    if (selectedSectionIndex === null) {
      dispatch(addSection(newSection));
    } else {
      dispatch(
        editSection({
          index: selectedSectionIndex,
          section: newSection,
        }),
      );
    }

    toast.success(`Section added/updated successfully but you need to save the course to apply the changes`);
    onClose();
  };

  return (
    <CustomModal isOpen={isSectionModalOpen} onClose={onClose}>
      <div className="flex flex-col">
        <div className="flex justify-between items-center mb-4">
          <h2 className="text-2xl font-bold">Add/Edit Section</h2>
          <button onClick={onClose} className="text-gray-500 hover:text-gray-700">
            <X className="w-6 h-6" />
          </button>
        </div>

        <Form {...methods}>
          <form onSubmit={methods.handleSubmit(onSubmit)} className="space-y-4">
            <CustomFormField name="title" label="Section Title" placeholder="Write section title here" />

            <CustomFormField name="description" label="Section Description" type="textarea" placeholder="Write section description here" />

            <div className="flex justify-end space-x-2 mt-6">
              <Button type="button" variant="outline" onClick={onClose}>
                Cancel
              </Button>
              <Button type="submit" className="bg-primary-700">
                Add
              </Button>
            </div>
          </form>
        </Form>
      </div>
    </CustomModal>
  );
};

export default SectionModal;
