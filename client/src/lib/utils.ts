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