import type { Metadata } from 'next';
import { DM_Sans } from 'next/font/google';
import './globals.css';
import Providers from './providers';
import { Toaster } from '@/components/ui/sonner';

const dmSans = DM_Sans({
  subsets: ['latin'],
  display: 'swap',
  variable: '--font-dm-sans',
});

export const metadata: Metadata = {
  title: 'Find your course',
  description: 'Best platform for finding courses',
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body className={`${dmSans.className} antialiased bg-customgreys-primary`}>
        <Toaster position="bottom-right" richColors />
        <Providers>{children}</Providers>
      </body>
    </html>
  );
}
