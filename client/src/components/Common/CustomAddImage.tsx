'use client';

import { cn } from '@/lib/utils';
import { Avatar, AvatarFallback, AvatarImage } from '@radix-ui/react-avatar';
import { Camera, Trash } from 'lucide-react';
import { useEffect, useRef, useState } from 'react';

type Props = {
  fallbackText: string;
  imageUrl: string;
  methods: any;
  className: string;
  avatarClassName: string;
};

const CustomAddImage = ({ methods, imageUrl, fallbackText, className, avatarClassName }: Props) => {
  const [previewUrl, setPreviewUrl] = useState<string | null>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);
  const imageSource = previewUrl || imageUrl || '/placeholder.png';

  fallbackText.charAt(0).toUpperCase();

  useEffect(() => {
    return () => {
      if (previewUrl && previewUrl.startsWith('blob:')) {
        URL.revokeObjectURL(previewUrl);
        console.log('Revoked blob URL:', previewUrl);
      }
    };
  }, [previewUrl]);

  const handleDeleteImg = () => {
    methods.setValue('image', undefined, { shouldValidate: true, shouldDirty: true });
    setPreviewUrl('/placeholder.png');
  };

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (file) {
      methods.setValue('image', file, { shouldValidate: true, shouldDirty: true });
      const newPreviewUrl = URL.createObjectURL(file);
      if (previewUrl && previewUrl.startsWith('blob:')) {
        URL.revokeObjectURL(previewUrl);
      }
      setPreviewUrl(newPreviewUrl);
    }
    if (event.target) {
      event.target.value = '';
    }
  };

  return (
    <div className="group flex items-center  relative">
      <button
        onClick={handleDeleteImg}
        className=" absolute z-50 bg-customgreys-primarybg p-4 opacity-0 group-hover:opacity-100 delay-200  group-hover:translate-x-[-4.5rem] hover:bg-customgreys-darkGrey rounded-lg transition-[background-color,transform,opacity] duration-500 cursor-pointer ">
        <Trash className="size-5 text-white" />
      </button>
      <div className={cn(`flex flex-col items-center space-y-2 overflow-hidden bg-red-300`, className)}>
        <label htmlFor="image-upload" className="relative cursor-pointer  w-full h-full">
          <Avatar className={cn('w-full h-full', avatarClassName)}>
            <AvatarImage src={imageSource} alt="Profile Picture" className="w-full h-full" />
            <AvatarFallback className="bg-customgreys-darkGrey text-white-100">{fallbackText}</AvatarFallback>
          </Avatar>
          <div className="absolute w-full h-full inset-0 bg-black bg-opacity-0 group-hover:bg-opacity-40 flex items-center justify-center transition-opacity duration-200">
            <Camera className="w-6 h-6 text-white opacity-0 group-hover:opacity-100 transition-opacity duration-200" />
          </div>
        </label>
        <input
          id="image-upload"
          ref={fileInputRef}
          type="file"
          accept="image/png, image/jpeg, image/gif"
          onChange={handleFileChange}
          className="hidden"
          aria-hidden="true"
        />
        {methods.formState.errors.image && (
          <p className="text-sm text-red-400 mt-1">{methods.formState.errors.image.message}</p>
        )}
      </div>
    </div>
  );
};

export default CustomAddImage;
