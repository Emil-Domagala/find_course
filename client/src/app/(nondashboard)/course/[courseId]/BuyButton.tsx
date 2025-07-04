'use client';
import ButtonWithSpinner from '@/components/Common/ButtonWithSpinner';
import { useAddCourseToCartMutation } from '@/state/endpoints/cart/cart';
import { ApiErrorResponse } from '@/types/apiError';
import { useRouter } from 'next/navigation';
import { toast } from 'sonner';

type Props = { courseId: string; accessToken?: string };

const BuyButton = ({ courseId, accessToken }: Props) => {
  const router = useRouter();
  const [addCourseToCart, { isLoading }] = useAddCourseToCartMutation();

  const handleAddToCart = async () => {
    if (!accessToken) {
      toast.info('Please login to add to cart');
      return router.push('/auth/login?redirect=/course/' + courseId);
    }

    try {
      await addCourseToCart({ courseId }).unwrap();
      toast.success('Added to cart');
    } catch (e) {
      const errorMessage = (e as ApiErrorResponse)?.data?.message || (e instanceof Error ? e.message : 'An unexpected error occurred.');
      toast.error(errorMessage);
    }
  };

  return (
    <ButtonWithSpinner variant="primary" onClick={handleAddToCart} isLoading={isLoading}>
      Add to Cart!
    </ButtonWithSpinner>
  );
};

export default BuyButton;
