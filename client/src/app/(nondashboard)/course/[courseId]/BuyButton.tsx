'use client';
import { Button } from '@/components/ui/button';
import { useAddCourseToCartMutation } from '@/state/endpoints/cart/cart';

import { ApiErrorResponse } from '@/types/apiError';
import { useRouter } from 'next/navigation';
import { toast } from 'sonner';

type Props = { courseId: string; authToken?: string };

const BuyButton = ({ courseId, authToken }: Props) => {
  const router = useRouter();
  const [addCourseToCart] = useAddCourseToCartMutation();

  const handleAddToCart = async () => {

    if (!authToken) {
      toast.info('Please login to add to cart');
      return router.push('/auth/login?redirect=/course/' + courseId);
    }

    try {
      await addCourseToCart({ courseId }).unwrap();
      toast.success('Added to cart');
    } catch (e) {
      const errorFull = e as ApiErrorResponse;
      const error = errorFull.data;
      let message = 'Something went wrong';
      if (error.message) {
        message = error.message;
      }
      toast.error(message);
    }
  };

  return (
    <Button variant="primary" onClick={handleAddToCart}>
      Add to Cart!
    </Button>
  );
};

export default BuyButton;
