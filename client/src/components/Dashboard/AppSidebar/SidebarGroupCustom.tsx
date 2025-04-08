'use client';
import {
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
} from '@/components/ui/sidebar';
import { LucideIcon } from 'lucide-react';

import Link from 'next/link';
import { usePathname } from 'next/navigation';

type Props = {
  links: { icon: LucideIcon; label: string; href: string }[];
  label?: string;
};

const SidebarGroupCustom = ({ links, label }: Props) => {
  const pathname = usePathname();

  return (
    <SidebarGroup className="p-0">
      {label && (
        <SidebarGroupLabel className="group-data-[collapsible=icon]:hidden">
          <p className="font-semibold !w-full !text-md text-white-50  pl-5 group-data-[collapsible=icon]:hidden">
            {label}
          </p>
        </SidebarGroupLabel>
      )}
      <SidebarGroupContent>
        <SidebarMenu className=" gap-0">
          {links.map((link) => {
            const isActive = pathname.startsWith(link.href);
            return (
              <SidebarMenuItem
                key={link.label}
                className={`group-data-[collapsible=icon]:flex group-data-[collapsible=icon]:justify-center  ${
                  isActive ? 'bg-gray-800' : ''
                }`}>
                <SidebarMenuButton
                  asChild
                  size={'lg'}
                  className={`gap-4 p-3 rounded-none hover:bg-customgreys-secondarybg group-data-[collapsible=icon]:flex group-data-[collapsible=icon]:justify-center transition-colors duration-300 ${
                    !isActive ? 'text-customgreys-dirtyGrey' : ''
                  }`}>
                  <Link href={link.href} className="flex items-center">
                    <link.icon className={!isActive ? 'text-white-50' : 'text-gray-500'} />
                    <span
                      className={`font-medium text-md ml-4 group-data-[collapsible=icon]:hidden ${
                        !isActive ? 'text-white-50' : ''
                      }`}>
                      {link.label}
                    </span>
                  </Link>
                </SidebarMenuButton>
                {isActive && <div className="absolute right-0 top-0 h-full w-[4px] bg-primary-750" />}
              </SidebarMenuItem>
            );
          })}
        </SidebarMenu>
      </SidebarGroupContent>
    </SidebarGroup>
  );
};

export default SidebarGroupCustom;
