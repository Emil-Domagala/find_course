import React from 'react';
import { ControllerRenderProps, FieldValues, useFormContext, useFieldArray } from 'react-hook-form';
import { FormControl, FormField, FormItem, FormLabel, FormMessage } from '@/components/ui/form';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { Textarea } from '@/components/ui/textarea';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Edit, X, Plus } from 'lucide-react';

type FormFieldProps = {
  name: string;
  label?: string;
  type?: 'text' | 'email' | 'textarea' | 'number' | 'select' | 'password' | 'multi-input';
  placeholder?: string;
  options?: { value: string; label: string }[];
  className?: string;
  labelClassName?: string;
  inputClassName?: string;
  value?: string;
  disabled?: boolean;
  isIcon?: boolean;
  initialValue?: string | number | boolean | string[];
};

export const CustomFormField: React.FC<FormFieldProps> = ({
  name,
  label,
  type = 'text',
  placeholder,
  options,
  className,
  inputClassName,
  labelClassName,
  disabled = false,
  isIcon = false,
  initialValue,
}) => {
  const { control } = useFormContext();

  const renderFormControl = (field: ControllerRenderProps<FieldValues, string>) => {
    switch (type) {
      case 'textarea':
        return <Textarea placeholder={placeholder} {...field} rows={3} className={`border-none bg-customgreys-darkGrey p-4 ${inputClassName}`} />;
      case 'select':
        return (
          <Select value={field.value} onValueChange={field.onChange}>
            <SelectTrigger className={`w-full border-none bg-customgreys-primarybg p-4 ${inputClassName}`}>
              <SelectValue placeholder={placeholder} />
            </SelectTrigger>
            <SelectContent className="w-full border-none mt-1 py-2 bg-customgreys-darkGrey rounded-md max-h-60">
              {options?.map((option) => (
                <SelectItem
                  key={option.value}
                  value={option.value}
                  className={`text-center cursor-pointer bg-customgreys-darkGrey min-w-[100%] p-2 hover:bg-customgreys-darkerGrey hover:!outline-none`}>
                  {option.label}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        );
      case 'number':
        return <Input type="number" placeholder={placeholder} {...field} className={`border-none bg-customgreys-darkGrey p-4 ${inputClassName}`} disabled={disabled} step={0.01} />;
      case 'multi-input':
        return <MultiInputField name={name} control={control} placeholder={placeholder} inputClassName={inputClassName} />;
      default:
        return <Input type={type} placeholder={placeholder} {...field} className={`border-none bg-customgreys-primarybg p-4 ${inputClassName}`} disabled={disabled} />;
    }
  };

  return (
    <FormField
      control={control}
      name={name}
      defaultValue={initialValue}
      render={({ field }) => (
        <FormItem className={`rounded-md relative pb-5 ${className}`}>
          {label && (
            <div className="flex justify-between items-center">
              <FormLabel className={`text-customgreys-dirtyGrey text-sm ${labelClassName}`}>{label}</FormLabel>

              {!disabled && isIcon && type !== 'multi-input' && <Edit className="size-4 text-customgreys-dirtyGrey" />}
            </div>
          )}
          <FormControl>
            {renderFormControl({
              ...field,
              value: field.value === undefined ? initialValue : type === 'number' ? +field.value : field.value,
            })}
          </FormControl>
          <FormMessage className="text-red-500 text-xs absolute bottom-0" />
        </FormItem>
      )}
    />
  );
};
type MultiInputFieldProps = {
  name: string;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  control: any;
  placeholder?: string;
  inputClassName?: string;
};

const MultiInputField: React.FC<MultiInputFieldProps> = ({ name, control, placeholder, inputClassName }) => {
  const { fields, append, remove } = useFieldArray({
    control,
    name,
  });

  return (
    <div className="space-y-2">
      {fields.map((field, index) => (
        <div key={field.id} className="flex items-center space-x-2">
          <FormField
            control={control}
            name={`${name}.${index}`}
            render={({ field }) => (
              <FormControl>
                <Input {...field} placeholder={placeholder} className={`flex-1 border-none bg-customgreys-darkGrey p-4 ${inputClassName}`} />
              </FormControl>
            )}
          />
          <Button type="button" onClick={() => remove(index)} variant="secondary" size="icon" className="text-customgreys-dirtyGrey">
            <X className="w-4 h-4" />
          </Button>
        </div>
      ))}
      <Button type="button" onClick={() => append('')} variant="outline" size="sm" className="mt-2 text-customgreys-dirtyGrey">
        <Plus className="w-4 h-4 mr-2" />
        Add Item
      </Button>
    </div>
  );
};
