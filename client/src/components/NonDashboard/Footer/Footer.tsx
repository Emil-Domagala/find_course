
const Footer = () => {
  return (
    <footer className="bg-customgreys-secondarybg bottom-0 w-full py-8 mt-10 text-center text-sm ">
      <p>&copy; {new Date().getFullYear()} Emil Domagala. All Rights Reserved.</p>
      <div className="mt-2 ">
        <span className="text-primary-500 mx-2">About</span>
        <span className="text-primary-500 mx-2">Privacy Policy</span>
        <span className="text-primary-500 mx-2">Licensing</span>
        <span className="text-primary-500 mx-2">Contact</span>
      </div>
    </footer>
  );
};

export default Footer;
