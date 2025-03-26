import { useEffect, useState } from 'react';

export default ({ totalImages, interval = 5000 }: { totalImages: number; interval?: number }) => {
  const [currentImage, setCurrentImage] = useState(0);

  useEffect(() => {
    const intervalId = setInterval(() => {
      setCurrentImage((prevImage) => (prevImage + 1) % totalImages);
    }, interval);

    return () => clearInterval(intervalId);
  }, [totalImages, interval]);

  return currentImage;
};
