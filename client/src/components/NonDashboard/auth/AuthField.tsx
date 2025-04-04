import { Skeleton } from '@/components/ui/skeleton';
import { FormControl, FormDescription, FormField, FormItem, FormLabel, FormMessage } from '../../ui/form';
import { Input } from '../../ui/input';

type Props = {
  form: any;
  showDesc?: boolean;
  description?: string;
  label: string;
  placeholder?: string;
  type: string;
  name: string;
};

export const AuthFieldSkeleton = () => {
  return (
    <div className="grid gap-2 relative pb-5 mb-4 w-full">
      <Skeleton className="h-4 w-24 mb-2" />
      <Skeleton className="h-9 w-full" />
    </div>
  );
};

const AuthField = ({ form, showDesc = false, name, description, label, placeholder, type }: Props) => {
  return (
    <FormField
      control={form.control}
      name={name}
      render={({ field }) => (
        <FormItem className="relative pb-5 mb-4 w-full">
          <FormLabel className="text-white-50 font-medium text-md">{label}</FormLabel>
          <FormControl>
            <Input
              type={type}
              {...field}
              placeholder={placeholder}
              className="bg-customgreys-primarybg text-white-50 !shadow-none border-none font-medium text-md md:text-lg selection:bg-primary-750"
            />
          </FormControl>
          {description && showDesc && <FormDescription>{description}</FormDescription>}
          <FormMessage className="text-red-500 text-xs absolute bottom-0 " />
        </FormItem>
      )}
    />
  );
};

export default AuthField;
