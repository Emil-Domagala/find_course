import { clsx, type ClassValue } from 'clsx';
import { twMerge } from 'tailwind-merge';

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

export function transformToFrontendFormat(enumValue: string): string {
  return enumValue
    .replace(/_/g, ' ')
    .toLowerCase()
    .replace(/\b\w/g, (char) => char.toUpperCase());
}

export const transformKey = (key: string): string => {
  return key
    .replace(/([a-z])([A-Z])/g, '$1 $2')
    .split(' ') // Split into words
    .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
    .join(' ');
};

export function formatPrice(cents: number | undefined): string {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
  }).format((cents || 0) / 100);
}

// Convert dollars to cents (e.g., "49.99" -> 4999)
export function dollarsToCents(dollars: string | number): number {
  const amount = typeof dollars === 'string' ? parseFloat(dollars) : dollars;
  return Math.round(amount * 100);
}

// Convert cents to dollars (e.g., 4999 -> "49.99")
export function centsToDollars(cents: number | undefined): string {
  return ((cents || 0) / 100).toFixed(2).toString();
}

