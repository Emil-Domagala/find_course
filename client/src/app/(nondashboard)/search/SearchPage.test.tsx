import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import SearchPage from './page';
import { useLazyGetCoursesPublicQuery } from '@/state/endpoints/course/coursePublic';
import { CourseCategory } from '@/types/courses-enum';
import { createCourseDto } from '@/__test__/factories/courseFactory';
import { createPageResponse } from '@/__test__/factories/pageFactory';

const replace = jest.fn();
const get = jest.fn();
const fetchCourses = jest.fn();

jest.mock('@/state/endpoints/course/coursePublic', () => ({
  useLazyGetCoursesPublicQuery: jest.fn(),
}));
jest.mock('next/navigation', () => ({
  useRouter: () => ({ replace }),
  useSearchParams: () => ({ get }),
  usePathname: jest.fn(),
}));

beforeEach(() => {
  (useLazyGetCoursesPublicQuery as jest.Mock).mockReturnValue([fetchCourses, { data: [], isLoading: false }]);
});

afterEach(() => {
  jest.clearAllMocks();
});

const basicSetup = () => {
  const user = userEvent.setup();
  const button = screen.getByRole('button', { name: 'Search' });
  return { user, button };
};

const getAllInputs = () => {
  const keywordInput = screen.getByLabelText('Search by title');
  const categorySelect = screen.getByTestId('category');
  const sortSelect = screen.getByTestId('order-by');
  const directionSelect = screen.getByTestId('direction');
  const sizeSelect = screen.getByTestId('size');
  return { keywordInput, categorySelect, sortSelect, directionSelect, sizeSelect };
};

const pickFromAllInputs = async () => {
  const { user, button } = basicSetup();
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
  await user.click(button);
};

describe('SearchPage', () => {
  describe('Renders', () => {
    test('Renders all components', () => {
      render(<SearchPage />);
      expect(screen.getByTestId('filter-component')).toBeInTheDocument();
      expect(screen.getByText('List of available courses')).toBeInTheDocument();
      expect(screen.getByText('0 courses available')).toBeInTheDocument();
      expect(screen.getByTestId('pagination-component')).toBeInTheDocument();
    });
  });
  describe('Api Interaction', () => {
    test('On page change fetch courses', async () => {
      render(<SearchPage />);
      const { user, button } = basicSetup();
      await user.click(button);
      await waitFor(() => {
        expect(fetchCourses).toHaveBeenCalled();
      });
    });
    test('Calls with default values', async () => {
      render(<SearchPage />);
      await waitFor(() => {
        expect(fetchCourses).toHaveBeenCalledWith({
          page: 0,
          size: 12,
          sortField: 'createdAt',
          direction: 'ASC',
          keyword: '',
          category: '',
        });
        expect(fetchCourses).toHaveBeenCalledTimes(1);
      });
    });
    test('Calls with changed values', async () => {
      render(<SearchPage />);
      await pickFromAllInputs();
      await waitFor(() => {
        expect(fetchCourses).toHaveBeenCalledWith({
          page: 0,
          size: 24,
          sortField: 'title',
          direction: 'DESC',
          keyword: 'test',
          category: CourseCategory.DATA_SCIENCE,
        });
        expect(fetchCourses).toHaveBeenCalledTimes(2);
      });
    });
    test('Shows loading state', async () => {
      fetchCourses.mockReturnValue({ data: [], isLoading: true });
      (useLazyGetCoursesPublicQuery as jest.Mock).mockReturnValue([fetchCourses, { data: [], isLoading: true }]);
      render(<SearchPage />);
      await waitFor(() => {
        expect(screen.getByTestId('spinner')).toBeInTheDocument();
        expect(screen.getByTestId('course-skeleton-0')).toBeInTheDocument();
      });
    });
    test('Renders courses', async () => {
      const courses = createCourseDto();
      const pagination = createPageResponse<CourseDto>({ content: [courses] });
      (useLazyGetCoursesPublicQuery as jest.Mock).mockReturnValue([fetchCourses, { data: pagination, isLoading: false }]);
      render(<SearchPage />);
      await waitFor(() => {
        expect(screen.getByText('List of available courses')).toBeInTheDocument();
        expect(screen.getByText('1 courses available')).toBeInTheDocument();
        expect(screen.getByText(courses.title)).toBeInTheDocument();
      });
    });
    test('Shows no results when no data', async () => {
      const pagination = createPageResponse<CourseDto>({ content: [] });
      (useLazyGetCoursesPublicQuery as jest.Mock).mockReturnValue([fetchCourses, { data: pagination, isLoading: false }]);
      render(<SearchPage />);
      await waitFor(() => {
        expect(screen.getByText('There are no courses yet')).toBeInTheDocument();
      });
    });
    test('Updates URL on search button click', async () => {
      render(<SearchPage />);
      await pickFromAllInputs();

      await waitFor(() => {
        expect(replace).toHaveBeenCalledWith(expect.stringContaining('keyword=test'));
        expect(replace).toHaveBeenCalledWith(expect.stringContaining('category=DATA_SCIENCE'));
        expect(replace).toHaveBeenCalledWith(expect.stringContaining('sortField=title'));
        expect(replace).toHaveBeenCalledWith(expect.stringContaining('direction=DESC'));
        expect(replace).toHaveBeenCalledWith(expect.stringContaining('size=24'));
      });
    });
    test('On page change calls api again', async () => {
      const courses: CourseDto[] = [createCourseDto(), createCourseDto(), createCourseDto(), createCourseDto()];
      const pagination = createPageResponse<CourseDto>({ content: courses, size: 2 });
      (useLazyGetCoursesPublicQuery as jest.Mock).mockReturnValue([fetchCourses, { data: pagination, isLoading: false }]);
      render(<SearchPage />);
      const pageNextButton = screen.getByRole('button', { name: /Next/i });
      await userEvent.click(pageNextButton);
      await waitFor(() => {
        expect(fetchCourses).toHaveBeenCalledTimes(2);
      });
    });
  });
});
