import { createSlice, PayloadAction } from '@reduxjs/toolkit';

type InitialStateTypes = {
  courseEditor: {
    sections: [];
    isChapterModalOpen: boolean;
    isSectionModalOpen: boolean;
    selectedSectionIndex: number | null;
    selectedChapterIndex: number | null;
  };
};

const initialState: InitialStateTypes = {
  courseEditor: {
    sections: [],
    isChapterModalOpen: false,
    isSectionModalOpen: false,
    selectedSectionIndex: null,
    selectedChapterIndex: null,
  },
};

export const globalSlice = createSlice({
  name: 'global',
  initialState,
  reducers: {
    // Set sections
    setSections: (state, action: PayloadAction<[]>) => {
      state.courseEditor.sections = action.payload;
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
      state.courseEditor.isChapterModalOpen = true;
      state.courseEditor.selectedSectionIndex = action.payload.sectionIndex;
      state.courseEditor.selectedChapterIndex = action.payload.chapterIndex;
    },
    // Close chapter modal
    closeChapterModal: (state) => {
      state.courseEditor.isChapterModalOpen = false;
      state.courseEditor.selectedSectionIndex = null;
      state.courseEditor.selectedChapterIndex = null;
    },
    // Open Section modal
    openSectionModal: (state, action: PayloadAction<{ sectionIndex: number | null }>) => {
      state.courseEditor.isSectionModalOpen = true;
      state.courseEditor.selectedSectionIndex = action.payload.sectionIndex;
    },
    // Close Section modal
    closeSectionModal: (state) => {
      state.courseEditor.isSectionModalOpen = false;
      state.courseEditor.selectedSectionIndex = null;
    },

    //***************
    //----Section----
    //***************

    // add section ADD SECTION TYPE
    addSection: (state, action: PayloadAction<any>) => {
      state.courseEditor.sections.push(action.payload);
    },
    // edit section ADD SECTION TYPE
    editSection: (state, action: PayloadAction<{ index: number; section: any }>) => {
      state.courseEditor.sections[action.payload.index] = action.payload.section;
    },
    // Delete Section
    deleteSection: (state, action: PayloadAction<number>) => {
      state.courseEditor.sections.splice(action.payload, 1);
    },

    //***************
    //----Chapter----
    //***************

    // Add chapter
    addChapter: (state, action: PayloadAction<{ sectionIndex: number; chapter: any }>) => {
      state.courseEditor.sections[action.payload.sectionIndex].chapters.push(action.payload.chapter);
    },
    // Edit chaoter
    editChapter: (
      state,
      action: PayloadAction<{
        sectionIndex: number;
        chapterIndex: number;
        chapter: any;
      }>,
    ) => {
      state.courseEditor.sections[action.payload.sectionIndex].chapters[action.payload.chapterIndex] =
        action.payload.chapter;
    },
    // Delete chapter
    deleteChapter: (state, action: PayloadAction<{ sectionIndex: number; chapterIndex: number }>) => {
      state.courseEditor.sections[action.payload.sectionIndex].chapters.splice(action.payload.chapterIndex, 1);
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
} = globalSlice.actions;
export default globalSlice.reducer;
