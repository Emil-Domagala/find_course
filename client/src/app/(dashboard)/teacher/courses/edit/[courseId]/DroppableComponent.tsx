'use client';

import { DragDropContext, Droppable, Draggable } from '@hello-pangea/dnd';
import { Button } from '@/components/ui/button';
import { Trash2, Edit, Plus, GripVertical } from 'lucide-react';
import { useAppDispatch, useAppSelector } from '@/state/redux';
import { setSections, deleteSection, deleteChapter, openSectionModal, openChapterModal } from '@/state';
import { ChapterDetailsProtectedDto, SectionDetailsProtectedDto } from '@/types/courses';

export default function DroppableComponent() {
  const dispatch = useAppDispatch();
  const { sections } = useAppSelector((state) => state.global.courseEditor);

  const handleSectionDragEnd = (result: any) => {
    if (!result.destination) return;

    const startIndex = result.source.index;
    const endIndex = result.destination.index;

    const updatedSections = [...sections];
    const [reorderedSection] = updatedSections.splice(startIndex, 1);
    updatedSections.splice(endIndex, 0, reorderedSection);
    dispatch(setSections(updatedSections));
  };

  const handleChapterDragEnd = (result: any, sectionIndex: number) => {
    if (!result.destination) return;

    const startIndex = result.source.index;
    const endIndex = result.destination.index;

    const updatedSections = [...sections];
    const updatedChapters = [...(updatedSections[sectionIndex].chapters || [])];
    const [reorderedChapter] = updatedChapters.splice(startIndex, 1);
    updatedChapters.splice(endIndex, 0, reorderedChapter);
    updatedSections[sectionIndex].chapters = updatedChapters;
    dispatch(setSections(updatedSections));
  };

  return (
    <DragDropContext onDragEnd={handleSectionDragEnd}>
      <Droppable droppableId="sections">
        {(provided) => (
          <div ref={provided.innerRef} {...provided.droppableProps}>
            {sections.map((section: SectionDetailsProtectedDto & { tempId?: string }, sectionIndex: number) => (
              <Draggable key={(section.id || section.tempId)!} draggableId={(section.id || section.tempId)!} index={sectionIndex}>
                {(draggableProvider) => (
                  <div
                    ref={draggableProvider.innerRef}
                    {...draggableProvider.draggableProps}
                    className={`mb-4 p-2 rounded ${sectionIndex % 2 === 0 ? 'bg-customgreys-dirtyGrey/30' : 'bg-customgreys-secondarybg'}`}>
                    <SectionHeader section={section} sectionIndex={sectionIndex} dragHandleProps={draggableProvider.dragHandleProps} />

                    <DragDropContext onDragEnd={(result) => handleChapterDragEnd(result, sectionIndex)}>
                      <Droppable droppableId={`chapters-${section.id}`}>
                        {(droppableProvider) => (
                          <div ref={droppableProvider.innerRef} {...droppableProvider.droppableProps}>
                            {section.chapters?.map((chapter: ChapterDetailsProtectedDto & { tempId?: string }, chapterIndex: number) => (
                              <Draggable key={(chapter.id || chapter.tempId)!} draggableId={(chapter.id || chapter.tempId)!} index={chapterIndex}>
                                {(draggableProvider) => (
                                  <ChapterItem chapter={chapter} chapterIndex={chapterIndex} sectionIndex={sectionIndex} draggableProvider={draggableProvider} />
                                )}
                              </Draggable>
                            ))}
                            {droppableProvider.placeholder}
                          </div>
                        )}
                      </Droppable>
                    </DragDropContext>

                    <Button
                      type="button"
                      variant="outline"
                      size="sm"
                      onClick={() =>
                        dispatch(
                          openChapterModal({
                            sectionIndex,
                            chapterIndex: null,
                          }),
                        )
                      }
                      className="border-none text-primary-700 group">
                      <Plus className="mr-1 h-4 w-4 text-primary-700" />
                      <span className="text-primary-700">Add Chapter</span>
                    </Button>
                  </div>
                )}
              </Draggable>
            ))}
            {provided.placeholder}
          </div>
        )}
      </Droppable>
    </DragDropContext>
  );
}

const SectionHeader = ({ section, sectionIndex, dragHandleProps }: { section: SectionDetailsProtectedDto; sectionIndex: number; dragHandleProps: any }) => {
  const dispatch = useAppDispatch();

  return (
    <div className="flex justify-between items-center mb-2 bg-black/30 p-1 rounded" {...dragHandleProps}>
      <div className="w-full flex flex-col gap-1">
        <div className="w-full flex items-center justify-between">
          <div className="flex items-center">
            <GripVertical className="h-6 w-6 mb-1" />
            <h3 className="text-lg font-medium">{section.title}</h3>
          </div>
          <div className="flex items-center gap-[1px]">
            <Button type="button" variant="secondary" size="sm" className="p-0" onClick={() => dispatch(openSectionModal({ sectionIndex }))}>
              <Edit className="h-5 w-5" />
            </Button>
            <Button type="button" variant="secondary" size="sm" className="p-0" onClick={() => dispatch(deleteSection(sectionIndex))}>
              <Trash2 className="h-5 w-5" />
            </Button>
          </div>
        </div>
        {section.description && <p className="text-sm text-customgreys-dirtyGrey ml-6">{section.description}</p>}
      </div>
    </div>
  );
};

const ChapterItem = ({
  chapter,
  chapterIndex,
  sectionIndex,
  draggableProvider,
}: {
  chapter: ChapterDetailsProtectedDto;
  chapterIndex: number;
  sectionIndex: number;
  draggableProvider: any;
}) => {
  const dispatch = useAppDispatch();

  return (
    <div
      ref={draggableProvider.innerRef}
      {...draggableProvider.draggableProps}
      {...draggableProvider.dragHandleProps}
      className={`flex justify-between items-center ml-4 mb-1 rounded px-1 ${chapterIndex % 2 === 1 ? 'bg-black/20' : 'bg-black/40'}`}>
      <div className="flex items-center">
        <GripVertical className="h-4 w-4 mb-[2px]" />
        <p className="text-sm">{`${chapterIndex + 1}. ${chapter.title}`}</p>
      </div>
      <div className="flex items-center gap-[1px]">
        <Button
          type="button"
          variant="secondary"
          size="sm"
          className="p-1"
          onClick={() =>
            dispatch(
              openChapterModal({
                sectionIndex,
                chapterIndex,
              }),
            )
          }>
          <Edit className="h-4 w-4" />
        </Button>
        <Button
          type="button"
          variant="secondary"
          size="sm"
          className="p-1"
          onClick={() =>
            dispatch(
              deleteChapter({
                sectionIndex,
                chapterIndex,
              }),
            )
          }>
          <Trash2 className="h-4 w-4" />
        </Button>
      </div>
    </div>
  );
};
