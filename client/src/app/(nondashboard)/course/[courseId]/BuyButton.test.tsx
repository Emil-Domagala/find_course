import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import BuyButton from './BuyButton';
import { useAddCourseToCartMutation } from '@/state/endpoints/cart/cart';
import { toast } from 'sonner';

const push = jest.fn();
const addCourseToCart = jest.fn();

jest.mock('next/navigation', () => ({
  useRouter: () => ({ push: push }),
}));

jest.mock('sonner', () => ({
  toast: { info: jest.fn(), success: jest.fn(), error: jest.fn() },
}));

jest.mock('@/state/endpoints/cart/cart', () => ({
  useAddCourseToCartMutation: jest.fn(),
}));

beforeEach(() => {
  (useAddCourseToCartMutation as jest.Mock).mockReturnValue([addCourseToCart, { isLoading: false }]);
});

afterEach(() => {
  jest.clearAllMocks();
});

const basicSetup = () => {
  const user = userEvent.setup();
  const button = screen.getByRole('button', { name: 'Add to Cart!' });
  return { user, button };
};

describe('BuyButton', () => {
  describe('Renders', () => {
    test('Renders button when all props passed', () => {
      render(<BuyButton courseId="123" accessToken="token" />);
      const button = screen.getByRole('button', { name: 'Add to Cart!' });
      expect(button).toBeInTheDocument();
    });
    test('Renders button when accessToken is undefined', () => {
      render(<BuyButton courseId="123" />);
      const button = screen.getByRole('button', { name: 'Add to Cart!' });
      expect(button).toBeInTheDocument();
    });
  });
  describe('Api Interaction', () => {
    test('Sucessfully adds course to cart', async () => {
      addCourseToCart.mockReturnValue({ unwrap: () => Promise.resolve() });
      render(<BuyButton courseId="123" accessToken="token" />);
      const { user, button } = basicSetup();
      await user.click(button);
      await waitFor(() => {
        expect(toast.success).toHaveBeenCalledWith('Added to cart');
        expect(addCourseToCart).toHaveBeenCalledWith({ courseId: '123' });
      });
    });
    test('Shows loading spinner', async () => {
      (useAddCourseToCartMutation as jest.Mock).mockReturnValue([addCourseToCart, { isLoading: true }]);
      render(<BuyButton courseId="123" accessToken="token" />);
      const button = screen.getByRole('button', { name: 'Add to Cart!' });
      await waitFor(() => {
        expect(button).toBeDisabled();
        expect(screen.getByTestId('spinner')).toBeInTheDocument();
      });
    });

    describe('Error cases', () => {
      test('Shows error msg from API when availble', async () => {
        addCourseToCart.mockReturnValue({ unwrap: () => Promise.reject({ data: { message: 'Error message' } }) });
        render(<BuyButton courseId="123" accessToken="token" />);
        const { user, button } = basicSetup();
        await user.click(button);
        await waitFor(() => {
          expect(toast.error).toHaveBeenCalledWith('Error message');
        });
      });
      test('Shows default error msg if not availble', async () => {
        addCourseToCart.mockReturnValue({ unwrap: () => Promise.reject() });
        render(<BuyButton courseId="123" accessToken="token" />);
        const { user, button } = basicSetup();
        await user.click(button);
        await waitFor(() => {
          expect(toast.error).toHaveBeenCalledWith('An unexpected error occurred.');
        });
      });
      test('Redirect if user is not logged in', async () => {
        render(<BuyButton courseId="123" />);
        const { user, button } = basicSetup();
        await user.click(button);
        await waitFor(() => {
          expect(toast.info).toHaveBeenCalledWith('Please login to add to cart');
          expect(push).toHaveBeenCalledWith('/auth/login?redirect=/course/123');
        });
      });
    });
  });
});
