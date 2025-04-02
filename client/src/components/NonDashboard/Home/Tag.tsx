import { cn } from '@/lib/utils';
import { Skeleton } from '../../ui/skeleton';
import { cva, VariantProps } from 'class-variance-authority';

export const TagSkeleton = () => {
  return <Skeleton className="w-24 h-6 rounded-full"></Skeleton>;
};

const tagVariants = cva('bg-customgreys-secondarybg rounded-full text-sm', {
  variants: {
    size: {
      default: 'px-3 py-1',
      small: 'px-2 py-0.5 text-xs',
    },
  },
  defaultVariants: {
    size: 'default',
  },
});

interface TagProps extends VariantProps<typeof tagVariants> {
  children: React.ReactNode;
  className?: string;
}

const Tag = ({ children, size, className }: TagProps) => {
  return <div className={cn(tagVariants({ size }), className)}>{children}</div>;
};

export default Tag;
