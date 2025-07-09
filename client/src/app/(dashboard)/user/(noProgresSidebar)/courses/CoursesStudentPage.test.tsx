import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import MyCoursesTeacherPage from './page';
import { useLazyGetEnrolledCoursesQuery } from '@/state/endpoints/course/courseStudent';
import { createCoursesWithFirstChapter } from '@/__test__/factories/courseFactory';
import { createPageResponse } from '@/__test__/factories/pageFactory';

jest.mock('@/state/endpoints/course/courseStudent', () => ({
  useLazyGetEnrolledCoursesQuery: jest.fn(),
}));

const getEnrolledCourses = jest.fn();

let course1: CourseDto;
let course2: CourseDto;
let page: Page<CourseDto>;
let user: ReturnType<typeof userEvent.setup>;

beforeEach(() => {
  course1 = createCoursesWithFirstChapter();
  course2 = createCoursesWithFirstChapter();
  page = createPageResponse<CourseDto>({ content: [course1, course2] });
  user = userEvent.setup();

  (useLazyGetEnrolledCoursesQuery as jest.Mock).mockReturnValue([getEnrolledCourses, { data: page, isLoading: false }]);
});

afterEach(() => {
  jest.clearAllMocks();
});

describe('MyCoursesTeacherPage', () => {
  describe('Renders', () => {
    test('Renders CourseCards', async () => {
      render(<MyCoursesTeacherPage />);
      await waitFor(() => {
        expect(screen.getByText(course1.title)).toBeInTheDocument();
        expect(screen.getByText(course2.title)).toBeInTheDocument();
      });
    });

    //
    test('Shows loading state', async () => {
      (useLazyGetEnrolledCoursesQuery as jest.Mock).mockReturnValue([getEnrolledCourses, { data: page, isLoading: true }]);
      render(<MyCoursesTeacherPage />);
      await waitFor(() => {
        expect(screen.getByTestId('course-skeleton-0')).toBeInTheDocument();
      });
    });

    //
    test('Shows no results when no data', async () => {
      page = createPageResponse<CourseDto>({ content: [] });
      (useLazyGetEnrolledCoursesQuery as jest.Mock).mockReturnValue([getEnrolledCourses, { data: page, isLoading: false }]);
      render(<MyCoursesTeacherPage />);
      await waitFor(() => {
        expect(screen.getByLabelText('no courses found')).toBeInTheDocument();
      });
    });
  });
  describe('Api Interaction', () => {
    test('On page load fetch courses with def values', async () => {
      render(<MyCoursesTeacherPage />);

      expect(getEnrolledCourses).toHaveBeenCalledWith({ page: 0, size: 10 });
    });
    test('On page change calls api again', async () => {
      page = createPageResponse<CourseDto>({ totalPages: 5, size: 0, content: [course1, course2] });
      (useLazyGetEnrolledCoursesQuery as jest.Mock).mockReturnValue([getEnrolledCourses, { data: page, isLoading: false }]);
      render(<MyCoursesTeacherPage />);
      expect(getEnrolledCourses).toHaveBeenCalledTimes(1);
      const pageNextButton = screen.getByRole('button', { name: /Next/i });
      await user.click(pageNextButton);
      await waitFor(() => {
        expect(getEnrolledCourses).toHaveBeenCalledTimes(2);
        expect(getEnrolledCourses).toHaveBeenCalledWith({ page: 1, size: 10 });
      });
    });
    
  });
});
