import Link from 'next/link';

const Footer = () => {
  return (
    <footer className="bg-customgreys-secondarybg bottom-0 w-full py-8 mt-10 text-center text-sm ">
      <p>&copy; {new Date().getFullYear()} Emil Domagala. All Rights Reserved.</p>
      <div className="mt-2 ">
        <Link href="/privacy-policy" className="text-primary-500 mx-2">
          Privacy Policy
        </Link>
        <Link href="/terms-of-use" className="text-primary-500 mx-2">
          Terms of Use
        </Link>
      </div>
    </footer>
  );
};

export default Footer;
