import { api } from '@/state/api';
import { CartDto } from '@/features/nondashboard/user/cart/cart';
// TODO: change types

export const addCourseToCart = api.injectEndpoints({
  endpoints: (build) => ({
    addCourseToCart: build.mutation<CartDto, { courseId: string }>({
      query: ({ courseId }) => ({ url: `cart/${courseId}`, method: 'POST' }),
      invalidatesTags: ['Cart'],
    }),
  }),
});

export const { useAddCourseToCartMutation } = addCourseToCart;
