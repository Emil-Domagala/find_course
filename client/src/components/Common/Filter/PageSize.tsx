import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@radix-ui/react-select';
import { SetStateAction } from 'react';

type Props = { setSize: React.Dispatch<SetStateAction<number>>; size: number };

const PageSize = ({ setSize, size }: Props) => {
  return (
    <Select value={String(size)} onValueChange={(value) => setSize(Number(value))}>
      <SelectTrigger className="px-2 ml-5 min-w-10 bg-customgreys-secondarybg rounded-md">
        <SelectValue placeholder="Select page size">{String(size)}</SelectValue>
      </SelectTrigger>
      <SelectContent className="rounded-md overflow-hidden" position="popper">
        {[12, 24, 48, 100].map((option) => (
          <SelectItem
            className="text-center cursor-pointer bg-customgreys-secondarybg min-w-[100%] py-2 px-2 hover:bg-customgreys-darkGrey hover:!outline-none"
            key={option}
            value={String(option)}>
            {option}
          </SelectItem>
        ))}
      </SelectContent>
    </Select>
  );
};

export default PageSize;
