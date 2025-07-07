import { Button } from '@/components/ui/button';
import { Check } from 'lucide-react';
import Link from 'next/link';
import React from 'react';

const SuccessPage = () => {
  return (
    <div className="flex flex-col h-full items-center justify-center bg-background text-foreground">
      <div className="text-center">
        <div className="mb-4 rounded-full bg-green-500 p-3 inline-flex items-center justify-center">
          <Check className="w-16 h-16" />
        </div>
        <h1 className="text-4xl font-bold mb-3">COMPLETED</h1>
        <p className="mb-1">🎉 You have made a course purchase successfully! 🎉</p>
      </div>
      <div>
        <p>
          Need help? Contact our{' '}
          <Button variant="link" asChild className="p-0 m-0 text-primary-700">
            <a aria-label="customer support" target="_blank" rel="noopener noreferrer"  href={`mailto:${process.env.NEXT_PUBLIC_SUPPORT_EMAIL || 'support@example.com'}`}>customer support</a>
          </Button>
          .
        </p>
      </div>
      <Link aria-label="Go to Courses" href="/user/courses" scroll={false}>
        <div className="mt-4 flex justify-center bg-secondary-700 rounded-lg px-4 py-2 hover:bg-secondary-600 cursor-pointer text-customgreys-primarybg font-medium">
          Go to Courses
        </div>
      </Link>
    </div>
  );
};

export default SuccessPage;
