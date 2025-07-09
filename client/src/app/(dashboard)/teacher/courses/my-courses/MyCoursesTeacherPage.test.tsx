import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import MyCoursesTeacherPage from './page';
import { createCourseDto } from '@/__test__/factories/courseFactory';
import { createPageResponse } from '@/__test__/factories/pageFactory';
import { useCreateCourseMutation, useDeleteCourseMutation, useLazyGetCoursesTeacherQuery } from '@/state/endpoints/course/courseTeacher';
import { toast } from 'sonner';

const replace = jest.fn();
const push = jest.fn();
const get = jest.fn();
const fetchCourses = jest.fn();
const createCourse = jest.fn();
const deleteCourse = jest.fn();

jest.mock('@/state/endpoints/course/courseTeacher', () => ({
  useCreateCourseMutation: jest.fn(),
  useDeleteCourseMutation: jest.fn(),
  useLazyGetCoursesTeacherQuery: jest.fn(),
}));

jest.mock('next/navigation', () => ({
  useRouter: () => ({ replace, push }),
  useSearchParams: () => ({ get }),
  usePathname: jest.fn(),
}));

jest.mock('sonner', () => ({
  toast: {
    success: jest.fn(),
    error: jest.fn(),
  },
}));
let user: ReturnType<typeof userEvent.setup>;
let course1: CourseDto;
let course2: CourseDto;
let page: Page<CourseDto>;

beforeEach(() => {
  user = userEvent.setup();
  course1 = createCourseDto();
  course2 = createCourseDto();
  page = createPageResponse<CourseDto>({ content: [course1, course2] });

  jest.spyOn(window, 'confirm').mockReturnValue(true);
  (useLazyGetCoursesTeacherQuery as jest.Mock).mockReturnValue([fetchCourses, { data: page, isLoading: false }]);
  (useCreateCourseMutation as jest.Mock).mockReturnValue([createCourse, { isLoading: false }]);
  (useDeleteCourseMutation as jest.Mock).mockReturnValue([deleteCourse]);
});

const getAllInputs = () => {
  const keywordInput = screen.getByLabelText('Search by title');
  const categorySelect = screen.getByTestId('category');
  const sortSelect = screen.getByTestId('order-by');
  const directionSelect = screen.getByTestId('direction');
  const sizeSelect = screen.getByTestId('size');
  return { keywordInput, categorySelect, sortSelect, directionSelect, sizeSelect };
};

const pickFromAllInputs = async () => {
  const { keywordInput, categorySelect, sortSelect, directionSelect, sizeSelect } = getAllInputs();
  await user.type(keywordInput, 'test');

  await user.click(categorySelect);
  const optionCat = screen.getByRole('option', { name: /data science/i });
  await user.click(optionCat);

  await user.click(sortSelect);
  const optionSort = screen.getByRole('option', { name: /title/i });
  await user.click(optionSort);

  await user.click(directionSelect);
  const optionDir = screen.getByRole('option', { name: /desc/i });
  await user.click(optionDir);

  await user.click(sizeSelect);
  const optionSize = screen.getByRole('option', { name: /24/i });
  await user.click(optionSize);

  const button = screen.getByRole('button', { name: 'Search' });
  await user.click(button);
};

afterEach(() => {
  jest.clearAllMocks();
});

describe('MyCoursesTeacherPage', () => {
  describe('Renders', () => {
    describe('useLazyGetCoursesTeacherQuery', () => {
      test('Renders CourseCards', async () => {
        render(<MyCoursesTeacherPage />);
        await waitFor(() => {
          expect(screen.getByText(course1.title)).toBeInTheDocument();
          expect(screen.getByText(course2.title)).toBeInTheDocument();
        });
      });
      test('Shows loading state', async () => {
        (useLazyGetCoursesTeacherQuery as jest.Mock).mockReturnValue([fetchCourses, { data: page, isLoading: true }]);
        render(<MyCoursesTeacherPage />);
        await waitFor(() => {
          expect(screen.getByTestId('course-skeleton-0')).toBeInTheDocument();
        });
      });
      test('Shows no results when no data', async () => {
        page = createPageResponse<CourseDto>({ content: [] });
        (useLazyGetCoursesTeacherQuery as jest.Mock).mockReturnValue([fetchCourses, { data: page, isLoading: false }]);
        render(<MyCoursesTeacherPage />);
        await waitFor(() => {
          expect(screen.getByLabelText('no courses found')).toBeInTheDocument();
        });
      });
    });
  });
  describe('Api Interaction', () => {
    describe('useLazyGetCoursesTeacherQuery', () => {
      test('On page load fetch courses', async () => {
        render(<MyCoursesTeacherPage />);
        await waitFor(() => {
          expect(fetchCourses).toHaveBeenCalled();
        });
      });
      test('On page change  calls api again', async () => {
        page = createPageResponse<CourseDto>({ totalPages: 5, size: 2, content: [createCourseDto(), createCourseDto()] });
        (useLazyGetCoursesTeacherQuery as jest.Mock).mockReturnValue([fetchCourses, { data: page, isLoading: false }]);
        render(<MyCoursesTeacherPage />);
        const pageNextButton = screen.getByRole('button', { name: /Next/i });
        await user.click(pageNextButton);
        await waitFor(() => {
          expect(fetchCourses).toHaveBeenCalledTimes(2);
        });
      });
      test('Calls with default values', async () => {
        render(<MyCoursesTeacherPage />);
        await waitFor(() => {
          expect(fetchCourses).toHaveBeenCalledWith({
            page: 0,
            size: 12,
            sortField: undefined,
            direction: undefined,
            keyword: undefined,
            category: undefined,
          });
        });
      });
      test('Calls with changed values', async () => {
        render(<MyCoursesTeacherPage />);
        await pickFromAllInputs();
        await waitFor(() => {
          expect(fetchCourses).toHaveBeenCalledWith({
            page: 0,
            size: 24,
            sortField: 'title',
            direction: 'DESC',
            keyword: 'test',
            category: 'DATA_SCIENCE',
          });
        });
      });
      test('Saves props in URL', async () => {
        render(<MyCoursesTeacherPage />);
        await pickFromAllInputs();

        await waitFor(() => {
          expect(replace).toHaveBeenCalledWith(expect.stringContaining('keyword=test'));
          expect(replace).toHaveBeenCalledWith(expect.stringContaining('category=DATA_SCIENCE'));
          expect(replace).toHaveBeenCalledWith(expect.stringContaining('sortField=title'));
          expect(replace).toHaveBeenCalledWith(expect.stringContaining('direction=DESC'));
          expect(replace).toHaveBeenCalledWith(expect.stringContaining('size=24'));
        });
      });
    });
    describe('useCreateCourseMutation', () => {
      test('Creates and redirect to edit page', async () => {
        const createdCourse = createCourseDto();
        createCourse.mockReturnValue({
          unwrap: () => Promise.resolve(createdCourse),
        });
        render(<MyCoursesTeacherPage />);
        const button = screen.getByRole('button', { name: 'Create Course' });
        await user.click(button);

        await waitFor(() => {
          expect(toast.success).toHaveBeenCalledWith('Course created successfully');
          expect(createCourse).toHaveBeenCalled();
          expect(push).toHaveBeenCalledWith(`/teacher/courses/edit/${createdCourse.id}`);
        });
      });
      test('Shows error message when avaible', async () => {
        createCourse.mockReturnValue({ unwrap: () => Promise.reject({ data: { message: 'error message' } }) });
        render(<MyCoursesTeacherPage />);
        const button = screen.getByRole('button', { name: 'Create Course' });
        await user.click(button);
        await waitFor(() => {
          expect(toast.error).toHaveBeenCalledWith('error message');
        });
      });
      test('Shows default error message', async () => {
        createCourse.mockReturnValue({ unwrap: () => Promise.reject() });
        render(<MyCoursesTeacherPage />);
        const button = screen.getByRole('button', { name: 'Create Course' });
        await user.click(button);
        await waitFor(() => {
          expect(toast.error).toHaveBeenCalledWith('Something went wrong, try again later');
        });
      });
    });
    describe('useDeleteCourseMutation', () => {
      test('Deletes correct course ', async () => {
        jest.spyOn(window, 'confirm').mockReturnValue(true);
        deleteCourse.mockReturnValue({ unwrap: () => Promise.resolve() });
        render(<MyCoursesTeacherPage />);
        const deleteButton = screen.getByLabelText('Delete ' + course1.title);
        await user.click(deleteButton);
        await waitFor(() => {
          expect(toast.success).toHaveBeenCalledWith('Course deleted successfully');
          expect(deleteCourse).toHaveBeenCalledWith({ courseId: course1.id });
        });
      });
      test('Do not call  delete if not confirmed', async () => {
        jest.spyOn(window, 'confirm').mockReturnValue(false);

        render(<MyCoursesTeacherPage />);
        const deleteButton = screen.getByLabelText('Delete ' + course1.title);
        await user.click(deleteButton);
        await waitFor(() => {
          expect(deleteCourse).not.toHaveBeenCalled();
        });
      });
      test('Shows error message when avaible', async () => {
        jest.spyOn(window, 'confirm').mockReturnValue(true);
        deleteCourse.mockReturnValue({ unwrap: () => Promise.reject({ data: { message: 'error message' } }) });
        render(<MyCoursesTeacherPage />);
        const deleteButton = screen.getByLabelText('Delete ' + course1.title);
        await user.click(deleteButton);
        await waitFor(() => {
          expect(toast.error).toHaveBeenCalledWith('Deleting Course: ' + 'error message');
        });
      });
      test('Shows default error message', async () => {
        jest.spyOn(window, 'confirm').mockReturnValue(true);
        deleteCourse.mockReturnValue({ unwrap: () => Promise.reject() });
        render(<MyCoursesTeacherPage />);
        const deleteButton = screen.getByLabelText('Delete ' + course1.title);
        await user.click(deleteButton);
        await waitFor(() => {
          expect(toast.error).toHaveBeenCalledWith('Deleting Course: ' + 'Something went wrong, try again later');
        });
      });
    });
  });
});
