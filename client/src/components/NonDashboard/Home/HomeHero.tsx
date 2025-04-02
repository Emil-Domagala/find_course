'use client';
import Link from 'next/link';

import Image from 'next/image';
import useCarosel from '@/hooks/useCarosel';
import { motion } from 'framer-motion';
import { Skeleton } from '../../ui/skeleton';

export const HomeHeroSkeleton = () => {
  return (
    <div className="mt-12 flex h-[500px] items-center justify-between rounded-lg bg-customgreys-secondarybg">
      <div className="mx-auto basis-1/2 px-16">
        <Skeleton className="mb-4 h-8 w-48"></Skeleton>
        <Skeleton className="mb-2 h-4 w-96"></Skeleton>
        <Skeleton className="mb-8 h-4 w-72"></Skeleton>
        <Skeleton className="h-10 w-40"></Skeleton>
      </div>
      <Skeleton className="h-full basis-1/2 rounded-r-lg"></Skeleton>
    </div>
  );
};

const HomeHero = () => {
  const currentImage = useCarosel({ totalImages: 3, interval: 5000 });
  return (
    <motion.div
      initial={{ y: 20, opacity: 0 }}
      animate={{ y: 0, opacity: 1 }}
      transition={{ duration: 0.3 }}
      className="mt-12 flex h-[500px] items-center justify-between rounded-lg bg-customgreys-secondarybg">
      {/* Course text  */}
      <div className="mx-auto basis-1/2 px-16">
        <h1 className="mb-4 text-4xl font-bold">Courses</h1>
        <p className="mb-8 text-lg text-gray-400">
          Find the best courses online and enroll in them.
          <br />
          Everyday learn something new!
        </p>
        <Link href={'/search'}>
          <div className="w-fit rounded-md bg-primary-700 px-4 py-2 duration-300 hover:bg-primary-600">
            Search for courses
          </div>
        </Link>
      </div>
      {/* Fading images */}
      <div className="relative h-full basis-1/2 overflow-hidden rounded-r-lg">
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

export default HomeHero;
