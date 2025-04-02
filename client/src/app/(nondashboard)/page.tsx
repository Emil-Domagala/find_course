'use client';
import HomeHero from '@/components/NonDashboard/Home/HomeHero';
import { motion } from 'framer-motion';
import Tags from '@/components/NonDashboard/Home/Tags';
import Courses from '@/components/NonDashboard/Home/Courses';

const Landing = () => {
  return (
    <main className="container">
      <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ duration: 0.3 }}>
        <HomeHero />
        <Tags />
        <Courses />
      </motion.div>
    </main>
  );
};

export default Landing;
