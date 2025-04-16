import Header from '@/components/Dashboard/Header';
import { useGetCartQuery, useRemoveCourseFromCartMutation } from '@/state/api';

const CartPage = ({}) => {
  const { data: cart, isLoading, error } = useGetCartQuery();
  const [removeCourseFromCart] = useRemoveCourseFromCartMutation();

  return (
    <>
      <Header title="Profile" subtitle="View your profile" />
      {cart?.courses?.map((course) => <div key={course.id}></div>)}
      {cart?.totalPrice} $
    </>
  );
};

export default CartPage;
