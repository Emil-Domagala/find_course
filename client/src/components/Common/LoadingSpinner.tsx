import { Loader } from 'lucide-react';

const LoadingSpinner = () => {
  return (
    <div className="flex w-full h-full items-center justify-center">
      <Loader size={40} className="animate-[spin_2s_linear_infinite]" />
    </div>
  );
};

export default LoadingSpinner;
