'use client';
import { motion } from 'framer-motion';
import { Skeleton } from '@/components/ui/skeleton';
import Tag, { TagSkeleton } from './Tag';
import { CourseCategory } from '@/types/courses-enum';
import { useEffect, useState } from 'react';

export const TagsSkeleton = () => {
  return (
    <div className="mx-auto py-12 mt-10">
      <Skeleton className="h-6 w-48 mb-4"></Skeleton>
      <Skeleton className="h-4 w-full max-w-2xl mb-8"></Skeleton>
      <div className="flex flex-wrap gap-4 mb-8">
        {[...Array(4)].map((_, index) => (
          <TagSkeleton key={index} />
        ))}
      </div>
    </div>
  );
};

const getRandomCategories = (count: number = 4): CourseCategory[] => {
  const categories = Object.values(CourseCategory);
  return categories.sort(() => Math.random() - 0.5).slice(0, count);
};

const Tags = () => {
  const [categories, setCategories] = useState<string[]>([]);

  useEffect(() => {
    setCategories(getRandomCategories(4)); 
  }, []);
  
  return (
    <motion.div
      initial={{ y: 20, opacity: 0 }}
      whileInView={{ y: 0, opacity: 1 }}
      transition={{ duration: 0.3 }}
      viewport={{ amount: 0.3, once: true }}
      className="mx-auto py-12 mt-10">
      <h2 className="text-2xl font-semibold mb-4">Fetured Courses</h2>
      <p className="text-customgreys-dirtyGrey mb-8">
        From begginer to advance in no time! We have courses just for you.
      </p>
      <div className="flex flex-wrap gap-4 mb-8">
        {categories.map((item) => (
          <Tag key={item}>{item}</Tag>
        ))}
      </div>
    </motion.div>
  );
};

export default Tags;
