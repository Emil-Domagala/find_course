'use client';

import { useFormContext } from 'react-hook-form';
import { FormControl, FormField, FormItem, FormLabel, FormMessage } from '../ui/form';
import { cn } from '@/lib/utils';
import { Camera, Trash } from 'lucide-react';
import { Button } from '../ui/button';
import Cropper from 'react-easy-crop';
import { useImageCropper } from '@/hooks/useImageCropper';
import Image from 'next/image';

type Props = {
  name: string;
  imageUrl: string;
  className: string;
  cropShape: 'round' | 'rect';
  aspect: number;
  maxImgDimetion: number;
  maxImageSizeMB: number;
  imgOnDelete: string;
  deletable?: boolean;
};

const CustomAddImg: React.FC<Props> = ({ name, imageUrl, className, cropShape, aspect, maxImgDimetion, maxImageSizeMB, imgOnDelete, deletable = true }) => {
  const { control, setValue } = useFormContext();
  const { previewUrl, cropping, crop, zoom, setCrop, setZoom, onCropComplete, handleFileSelect, handleCropConfirm, reset } = useImageCropper({
    maxImgDimetion,
    maxImageSizeMB,
    imgOnDelete,
    initialImg: imageUrl,
  });

  const handleDeleteImg = () => {
    setValue(name, undefined, { shouldValidate: true, shouldDirty: true });
    setValue('deleteImage', true, { shouldValidate: true, shouldDirty: true });
    reset();
  };

  const onFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) handleFileSelect(file);
  };

  const onCropConfirm = async () => {
    const result = await handleCropConfirm();
    if (!result) return;

    setValue(name, result.file, { shouldValidate: true, shouldDirty: true });
  };

  return (
    <>
      <FormField
        control={control}
        name={name}
        render={({ field: { ref, name: fieldName, onBlur } }) => (
          <FormItem className="group relative flex items-center w-full">
            {previewUrl !== imgOnDelete && deletable ? (
              <button
                type="button"
                onClick={handleDeleteImg}
                className="absolute z-50 bg-customgreys-primarybg p-4 opacity-0 group-hover:opacity-100 group-hover:translate-x-[-4.5rem] hover:bg-customgreys-darkGrey rounded-lg transition-all duration-500 cursor-pointer">
                <Trash className="size-5 text-white" />
              </button>
            ) : (
              ''
            )}
            <div className={cn(`flex flex-col items-center space-y-2 overflow-hidden`, className)}>
              <FormLabel className="relative cursor-pointer w-full h-full">
                <Image src={previewUrl} alt="Preview" width={0} height={0} sizes="100vw" className="w-full h-auto rounded-lg" />
                <div className="absolute inset-0 bg-black bg-opacity-0 group-hover:bg-opacity-40 flex items-center justify-center transition-opacity duration-200">
                  <Camera className="w-6 h-6 text-white opacity-0 group-hover:opacity-100 transition-opacity duration-200" />
                </div>
              </FormLabel>
            </div>
            <FormControl>
              <input ref={ref} name={fieldName} type="file" onBlur={onBlur} onChange={onFileChange} className="hidden" accept="image/png, image/jpeg, image/jpg, image/webp" />
            </FormControl>
            <FormMessage className="text-red-500 text-xs absolute bottom-0" />
          </FormItem>
        )}
      />

      {cropping && previewUrl && (
        <div className="fixed inset-0 z-50 bg-black flex justify-center items-center flex-col">
          <div className="relative w-full max-w-[90vw] h-[60vh] bg-black">
            <Cropper
              image={previewUrl}
              crop={crop}
              zoom={zoom}
              aspect={aspect}
              cropShape={cropShape}
              onCropChange={setCrop}
              onZoomChange={setZoom}
              onCropComplete={onCropComplete}
            />
          </div>
          <div className="flex gap-4 mt-6">
            <Button type="button" variant="primary" onClick={onCropConfirm}>
              Confirm Crop
            </Button>
          </div>
        </div>
      )}
    </>
  );
};

export default CustomAddImg;
