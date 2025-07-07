import { Loader } from 'lucide-react';
import { Button, buttonVariants } from '../ui/button'; // Import buttonVariants
import { ButtonHTMLAttributes } from 'react';
import { VariantProps } from 'class-variance-authority'; // Import VariantProps
import { cn } from '@/lib/utils';

type ButtonProps = ButtonHTMLAttributes<HTMLButtonElement> & VariantProps<typeof buttonVariants>;

type ButtonWithSpinnerProps = ButtonProps & {
  isLoading: boolean;
  children: React.ReactNode;
  className?: string;
};

const ButtonWithSpinner = ({ isLoading, children, className, onClick, ...props }: ButtonWithSpinnerProps) => {
  return (
    <Button variant="primary" className={cn('h-12 text-md w-full', className)} onClick={onClick} disabled={isLoading} {...props}>
      {children} {isLoading && <Loader data-testid="spinner" size={20} className="animate-[spin_2s_linear_infinite]" />}
    </Button>
  );
};

export default ButtonWithSpinner;
