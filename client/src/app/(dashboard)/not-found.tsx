'use server';

import Link from 'next/link';

export default async function PageNotFound() {
  return (
    <div className="relative flex flex-col items-center justify-center ">
      <div className="px-6 bg-[url('/galaxy.jpg')] bg-cover bg-[position:0%_60%] bg-no-repeat text-transparent bg-clip-text text-center">
        <h1 className="text-[19vw] md:text-[11cqi] font-bold leading-none">Error 404</h1>
        <p className="text-[9vw] md:text-[4.5cqi] font-semibold leading-tight pb-4">Page not found</p>
      </div>
      <p>Unfortunatly this page does not exist</p>
      <p>
        Please go back to{' '}
        <Link href="/" className="text-primary-700">
          Home page.
        </Link>
      </p>
    </div>
  );
}
