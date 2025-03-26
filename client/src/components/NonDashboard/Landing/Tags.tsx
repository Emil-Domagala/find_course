'use client';
import { motion } from 'framer-motion';
import { Skeleton } from '../../ui/skeleton';
import Tag, { TagSkeleton } from './Tag';

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

const Tags = () => {
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
        {['web development', 'enterprise IT', 'react', 'next', 'java'].map((item) => (
          <Tag key={item}>{item}</Tag>
        ))}
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">{/* Courses will be displayed here */}</div>
    </motion.div>
  );
};

export default Tags;
