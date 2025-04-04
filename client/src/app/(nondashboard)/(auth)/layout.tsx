const AuthLayout = ({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) => {
  return (
    <div className="flex justify-center items-center mt-10">
      <div className="rounded-xl flex flex-col w-full md:w-[35rem] sm:w-[30rem] mx-[5vw] sm:mx-auto shadow-none bg-customgreys-secondarybg border-none px-6 py-10 gap-0">
        {children}
      </div>
    </div>
  );
};

export default AuthLayout;
