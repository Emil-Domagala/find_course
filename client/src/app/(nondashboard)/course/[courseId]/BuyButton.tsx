'use client';
import { Button } from '@/components/ui/button';
import { useRouter } from 'next/navigation';
import { toast } from 'sonner';

type Props = { courseId: string; authToken?: string };

const BuyButton = ({ courseId, authToken }: Props) => {
  const router = useRouter();

  const handleAddToCart = async () => {
    console.log(courseId);

    if (!authToken) {
      toast.info('Please login to add to cart');
      return router.push('/auth/login?redirect=/course/' + courseId);
    }

    try {
      toast.success('Added to cart');
    } catch (e) {
      console.log(e);
      toast.error('Something went wrong');
    }
  };

  return (
    <Button variant="primary" onClick={handleAddToCart}>
      Buy Now!
    </Button>
  );
};

export default BuyButton;
