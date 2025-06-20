import { api } from '../../api';
import { CartDto, CartResponse } from '@/types/cart';
// TODO: change types

export const cartApi = api.injectEndpoints({
  endpoints: (build) => ({
    getCart: build.query<CartResponse, void>({
      query: () => ({ url: 'cart' }),
      keepUnusedDataFor: 0,
      providesTags: ['Cart'],
    }),

    addCourseToCart: build.mutation<CartDto, { courseId: string }>({
      query: ({ courseId }) => ({ url: `cart/${courseId}`, method: 'POST' }),
      invalidatesTags: ['Cart'],
    }),

    removeCourseFromCart: build.mutation<CartResponse, { courseId: string }>({
      query: ({ courseId }) => ({ url: `cart/${courseId}`, method: 'DELETE' }),
      async onQueryStarted({ courseId }, { dispatch, queryFulfilled }) {
        const patchResult = dispatch(
          // @ts-expect-error Code is fine redux problem
          api.util.updateQueryData('getCart', undefined, (draft: CartDto) => {
            if (!draft || !draft.courses || !Array.isArray(draft.courses)) {
              console.warn('Draft cart data is not in the expected format for optimistic update.');
              return;
            }
            const courseIndex = draft.courses.findIndex((course) => course.id === courseId);
            if (courseIndex !== -1) {
              const courseToRemove = draft.courses[courseIndex];
              if (typeof courseToRemove.price === 'number') {
                draft.totalPrice -= courseToRemove.price;
              }
              draft.courses.splice(courseIndex, 1);
            }
          }),
        );
        try {
          await queryFulfilled;
        } catch (err) {
          patchResult.undo();
          console.error('Error removing course from cart, reverting optimistic update:', err);
        }
      },
      invalidatesTags: ['Cart'],
    }),
  }),
});

export const { useGetCartQuery, useAddCourseToCartMutation, useRemoveCourseFromCartMutation } = cartApi;
