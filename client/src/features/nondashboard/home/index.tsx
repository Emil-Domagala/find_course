'use client';
import HomeHero from '@/features/nondashboard/home/components/HomeHero';
import { motion } from 'framer-motion';
import Tags from '@/features/nondashboard/home/components/Tags';
import Courses from '@/features/nondashboard/home/components/Courses';
import RenderDialog from '@/features/nondashboard/home/components/RenderDialog';

const Home = () => {
  return (
    <main className="container">
      <RenderDialog />
      <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ duration: 0.3 }}>
        <HomeHero />
        <Tags />
        <Courses />
      </motion.div>
    </main>
  );
};

export default Home;
