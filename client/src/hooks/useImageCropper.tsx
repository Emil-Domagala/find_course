import { useCallback, useState } from 'react';
import type { Area } from 'react-easy-crop';
import imageCompression from 'browser-image-compression';
import { getCroppedImg } from '@/lib/imageUtils';

export function useImageCropper({
  maxImgDimetion,
  maxImageSizeMB,
}: {
  maxImgDimetion: number;
  maxImageSizeMB: number;
}) {
  const [previewUrl, setPreviewUrl] = useState<string | null>(null);
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

    return { file: compressedFile, url: finalUrl };
  };

  const reset = () => {
    setPreviewUrl(null);
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
