import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import CartPage from './page';
import { useGetCartQuery, useRemoveCourseFromCartMutation } from '@/state/endpoints/cart/cart';
import { toast } from 'sonner';
import { createCartDto, createCartResponse } from '@/__test__/factories/cartFactory';
import { createCourseDto } from '@/__test__/factories/courseFactory';

const push = jest.fn();
const removeCourseFromCart = jest.fn();

jest.mock('next/navigation', () => ({
  useRouter: () => ({ push }),
}));
jest.mock('@/state/endpoints/cart/cart', () => ({
  useGetCartQuery: jest.fn(),
  useRemoveCourseFromCartMutation: jest.fn(),
}));
jest.mock('sonner', () => ({
  toast: {
    warning: jest.fn(),
  },
}));

beforeEach(() => {
  (useGetCartQuery as jest.Mock).mockReturnValue({ data: [], isLoading: false });
  (useRemoveCourseFromCartMutation as jest.Mock).mockReturnValue([removeCourseFromCart]);
});

afterEach(() => {
  jest.clearAllMocks();
});

describe('CartPage', () => {
  describe('Renders', () => {
    test('renders loading spinner', async () => {
      (useGetCartQuery as jest.Mock).mockReturnValue({ data: [], isLoading: true });
      render(<CartPage />);
      await waitFor(() => screen.getByTestId('loading-spinner'));
    });
    test('renders empty cart message', async () => {
      (useGetCartQuery as jest.Mock).mockReturnValue({ data: [], isLoading: false });
      render(<CartPage />);
      await waitFor(() => screen.getByText('Your cart is empty.'));
    });
    test('renders cart items total price, items length', async () => {
      const courseDto = createCourseDto();
      const courseDto2 = createCourseDto();

      const cartDto = createCartDto({ courses: [courseDto, courseDto2] });
      const cartResponse = createCartResponse({ cart: cartDto });
      (useGetCartQuery as jest.Mock).mockReturnValue({ data: cartResponse, isLoading: false });
      render(<CartPage />);
      await waitFor(() => {
        screen.getByText('$' + ((courseDto.price + courseDto2.price) / 100).toFixed(2));
        screen.getByText('Items: 2');
        screen.getByText(courseDto.title);
        screen.getByText(courseDto2.title);
      });
    });
  });
  describe('Interactions', () => {
    test('calls checkout route on button click', async () => {
      const courseDto = createCourseDto();
      const cartDto = createCartDto({ courses: [courseDto] });
      const cartResponse = createCartResponse({ cart: cartDto });
      (useGetCartQuery as jest.Mock).mockReturnValue({ data: cartResponse, isLoading: false });
      render(<CartPage />);
      const checkoutButton = screen.getByRole('button', { name: /Checkout/i });
      await userEvent.click(checkoutButton);
      expect(push).toHaveBeenCalledWith('/user/checkout');
    });
    test('calls sonner with warnings on response', async () => {
      const cartResponse = createCartResponse({ warnings: ['Warning 1', 'Warning 2'] });
      (useGetCartQuery as jest.Mock).mockReturnValue({ data: cartResponse, isLoading: false });
      render(<CartPage />);
      await waitFor(() => {
        expect(toast.warning).toHaveBeenCalledTimes(2);
        expect(toast.warning).toHaveBeenCalledWith('Warning 1');
        expect(toast.warning).toHaveBeenCalledWith('Warning 2');
      });
    });
  });
});
