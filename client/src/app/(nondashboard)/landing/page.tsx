'use client';

import { motion } from 'framer-motion';

const Landing = () => {
  return (
    <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ duration: 0.3 }}>
      <motion.div
        initial={{ y: 20, opacity: 0 }}
        animate={{ y: 0, opacity: 1 }}
        transition={{ duration: 0.3 }}
        className="flex justify-between items-center mt-12 h-[500px] rounded-lg bg-customgreys-secondarybg">
        <div className="basis-1/2 px-16 mx-auto">
          <h1 className="text-4xl font-bold mb-4">Courses</h1>
          <p className="text-lg text-gray-400 mb-8">
            Find the best courses online and enroll in them.
            <br />
            Everyday learn something new!
          </p>
        </div>
      </motion.div>
    </motion.div>
  );
};

export default Landing;
