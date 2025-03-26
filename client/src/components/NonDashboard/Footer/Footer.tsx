
const Footer = () => {
  return (
    <footer className="bg-customgreys-secondarybg bottom-0 w-full py-8 mt-10 text-center text-sm">
      <p>&copy; {new Date().getFullYear()} Emil Domagala. All Rights Reserved.</p>
      <div className="mt-2">
        <p className="text-primary-500 mx-2">About</p>
        <p className="text-primary-500 mx-2">Privacy Policy</p>
        <p className="text-primary-500 mx-2">Licensing</p>
        <p className="text-primary-500 mx-2">Contact</p>
      </div>
    </footer>
  );
};

export default Footer;
