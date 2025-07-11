import { ChapterFormDataId, SectionFormDataId } from '@/features/dashboard/teacher/courses/edit/validation';
import { createSlice, PayloadAction } from '@reduxjs/toolkit';

type InitialStateTypes = {
  sections: SectionFormDataId[];
  isChapterModalOpen: boolean;
  isSectionModalOpen: boolean;
  selectedSectionIndex: number | null;
  selectedChapterIndex: number | null;
};

const initialState: InitialStateTypes = {
  sections: [],
  isChapterModalOpen: false,
  isSectionModalOpen: false,
  selectedSectionIndex: null,
  selectedChapterIndex: null,
};

export const courseEditorSlice = createSlice({
  name: 'courseEditor',
  initialState,
  reducers: {
    // Set sections
    setSections: (state, action: PayloadAction<SectionFormDataId[]>) => {
      state.sections = action.payload;
    },

    //***************
    //-----Modal-----
    //***************

    // Open chapter modal
    openChapterModal: (
      state,
      action: PayloadAction<{
        sectionIndex: number | null;
        chapterIndex: number | null;
      }>,
    ) => {
      state.isChapterModalOpen = true;
      state.selectedSectionIndex = action.payload.sectionIndex;
      state.selectedChapterIndex = action.payload.chapterIndex;
    },
    // Close chapter modal
    closeChapterModal: (state) => {
      state.isChapterModalOpen = false;
      state.selectedSectionIndex = null;
      state.selectedChapterIndex = null;
    },
    // Open Section modal
    openSectionModal: (state, action: PayloadAction<{ sectionIndex: number | null }>) => {
      state.isSectionModalOpen = true;
      state.selectedSectionIndex = action.payload.sectionIndex;
    },
    // Close Section modal
    closeSectionModal: (state) => {
      state.isSectionModalOpen = false;
      state.selectedSectionIndex = null;
    },

    //***************
    //----Section----
    //***************

    // add section ADD SECTION TYPE
    addSection: (state, action: PayloadAction<SectionFormDataId>) => {
      const sectionToAdd = { ...action.payload, chapters: action.payload.chapters ?? [] };
      state.sections.push(sectionToAdd);
    },
    // edit section ADD SECTION TYPE
    editSection: (state, action: PayloadAction<{ index: number; section: SectionFormDataId }>) => {
      const sectionToEdit = { ...action.payload.section, chapters: action.payload.section.chapters ?? [] };
      state.sections[action.payload.index] = sectionToEdit;
    },
    // Delete Section
    deleteSection: (state, action: PayloadAction<number>) => {
      state.sections.splice(action.payload, 1);
    },

    //***************
    //----Chapter----
    //***************

    // Add chapter
    addChapter: (state, action: PayloadAction<{ sectionIndex: number; chapter: ChapterFormDataId }>) => {
      const section = state.sections[action.payload.sectionIndex];
      if (section) {
        if (!section.chapters) {
          section.chapters = [];
        }
        section.chapters.push(action.payload.chapter);
      }
    },
    // Edit chaoter
    editChapter: (
      state,
      action: PayloadAction<{
        sectionIndex: number;
        chapterIndex: number;
        chapter: ChapterFormDataId;
      }>,
    ) => {
      const section = state.sections[action.payload.sectionIndex];
      if (section?.chapters?.[action.payload.chapterIndex]) {
        section.chapters[action.payload.chapterIndex] = action.payload.chapter;
      }
    },
    // Delete chapter
    deleteChapter: (state, action: PayloadAction<{ sectionIndex: number; chapterIndex: number }>) => {
      const section = state.sections[action.payload.sectionIndex];
      if (section?.chapters) {
        section.chapters.splice(action.payload.chapterIndex, 1);
      }
    },
  },
});

export const {
  setSections,
  openChapterModal,
  closeChapterModal,
  openSectionModal,
  closeSectionModal,
  addSection,
  editSection,
  deleteSection,
  addChapter,
  editChapter,
  deleteChapter,
} = courseEditorSlice.actions;
export default courseEditorSlice.reducer;
