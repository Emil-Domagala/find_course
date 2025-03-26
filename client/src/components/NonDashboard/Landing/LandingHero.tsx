'use client';
import Link from 'next/link';

import Image from 'next/image';
import useCarosel from '@/hooks/useCarosel';
import { motion } from 'framer-motion';
import { Skeleton } from '../../ui/skeleton';

export const LandingHeroSkeleton = () => {
  return (
    <div className="flex justify-between items-center mt-12 h-[500px] rounded-lg bg-customgreys-secondarybg">
      <div className="basis-1/2 px-16 mx-auto">
        <Skeleton className="h-8 w-48 mb-4"></Skeleton>
        <Skeleton className="h-4 w-96 mb-2"></Skeleton>
        <Skeleton className="h-4 w-72 mb-8"></Skeleton>
        <Skeleton className="w-40 h-10"></Skeleton>
      </div>
      <Skeleton className="basis-1/2 h-full rounded-r-lg"></Skeleton>
    </div>
  );
};

const LandingHero = () => {
  const currentImage = useCarosel({ totalImages: 3, interval: 5000 });
  return (
    <motion.div
      initial={{ y: 20, opacity: 0 }}
      animate={{ y: 0, opacity: 1 }}
      transition={{ duration: 0.3 }}
      className="flex justify-between items-center mt-12 h-[500px] rounded-lg bg-customgreys-secondarybg">
      {/* Course text  */}
      <div className="basis-1/2 px-16 mx-auto">
        <h1 className="text-4xl font-bold mb-4">Courses</h1>
        <p className="text-lg text-gray-400 mb-8">
          Find the best courses online and enroll in them.
          <br />
          Everyday learn something new!
        </p>
        <Link href={'/search'}>
          <div className="bg-primary-700 w-fit hover:bg-primary-600 px-4 py-2 rounded-md duration-300">
            Search for courses
          </div>
        </Link>
      </div>
      {/* images */}
      <div className="basis-1/2 h-full relative overflow-hidden rounded-r-lg">
        {['/images/hero1.jpg', '/images/hero2.jpg', '/images/hero3.jpg'].map((src, index) => (
          <Image
            key={index}
            src={src}
            alt={`Hero banner`}
            fill
            priority={index === currentImage}
            sizes="(max-width: 768px) 100vw, (max-width:1200) 50vw, 33vw"
            className={`object-cover transition-opacity duration-500 ${
              index === currentImage ? 'opacity-100' : 'opacity-0'
            }`}
          />
        ))}
      </div>
    </motion.div>
  );
};

export default LandingHero;
