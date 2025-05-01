import { useCallback, useState } from 'react';
import type { Area } from 'react-easy-crop';
import imageCompression from 'browser-image-compression';
import { getCroppedImg } from '@/lib/imageUtils';

type UseImageCropperType = {
  maxImgDimetion: number;
  maxImageSizeMB: number;
  imgOnDelete: string;
  initialImg: string;
};

export function useImageCropper({ maxImgDimetion, maxImageSizeMB, imgOnDelete, initialImg }: UseImageCropperType) {
  const [previewUrl, setPreviewUrl] = useState<string>(initialImg);
  const [rawFile, setRawFile] = useState<File | null>(null);
  const [cropping, setCropping] = useState(false);
  const [crop, setCrop] = useState({ x: 0, y: 0 });
  const [zoom, setZoom] = useState(1);
  const [croppedAreaPixels, setCroppedAreaPixels] = useState<Area | null>(null);

  const onCropComplete = useCallback((_: any, area: Area) => {
    setCroppedAreaPixels(area);
  }, []);

  const handleFileSelect = (file: File) => {
    setRawFile(file);
    setPreviewUrl(URL.createObjectURL(file));
    setCropping(true);
  };

  const handleCropConfirm = async (): Promise<{ file: File; url: string } | null> => {
    if (!rawFile || !croppedAreaPixels || !previewUrl) return null;

    const croppedBlob = await getCroppedImg(previewUrl, croppedAreaPixels);
    if (!croppedBlob) return null;

    const croppedFile = new File([croppedBlob], rawFile.name, {
      type: croppedBlob.type || 'image/jpeg',
      lastModified: rawFile.lastModified,
    });

    const compressedFile = await imageCompression(croppedFile, {
      maxWidthOrHeight: maxImgDimetion,
      maxSizeMB: maxImageSizeMB,
      useWebWorker: true,
    });

    const finalUrl = URL.createObjectURL(compressedFile);
    setPreviewUrl(finalUrl);
    setCropping(false);

    const compressedImg = new File([compressedFile], rawFile.name, {
      type: compressedFile.type || 'image/jpeg',
      lastModified: rawFile.lastModified,
    });
    return { file: compressedImg, url: finalUrl };
  };

  const reset = () => {
    setPreviewUrl(imgOnDelete);
    setRawFile(null);
    setCropping(false);
    setCrop({ x: 0, y: 0 });
    setZoom(1);
    setCroppedAreaPixels(null);
  };

  return {
    previewUrl,
    cropping,
    crop,
    zoom,
    onCropComplete,
    setCrop,
    setZoom,
    handleFileSelect,
    handleCropConfirm,
    reset,
  };
}
