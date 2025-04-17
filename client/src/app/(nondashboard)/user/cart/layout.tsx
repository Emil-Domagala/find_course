const CartLayout = ({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) => {
  return (
    <main className={'container'}>
      <h1 className="text-3xl font-bold text-white-50 my-7">Your Cart</h1>
      {children}
    </main>
  );
};

export default CartLayout;
