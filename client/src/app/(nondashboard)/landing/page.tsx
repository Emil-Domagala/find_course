'use client';
import LandingHero, { LandingHeroSkeleton } from '@/components/NonDashboard/Landing/LandingHero';
import { motion } from 'framer-motion';
import Tag from '@/components/NonDashboard/Landing/Tag';
import Tags from '@/components/NonDashboard/Landing/Tags';

const LoadingSkeleton = () => {
  return (
    <>
      <LandingHeroSkeleton />
    </>
  );
};

const Landing = () => {
  return (
    <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ duration: 0.3 }}>
      <LandingHero />
      <Tags />
    </motion.div>
  );
};

export default Landing;
