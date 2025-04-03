import { NextRequest, NextResponse } from 'next/server';
import { cookies } from 'next/headers';
import jwt from 'jsonwebtoken';

export async function middleware(req: NextRequest) {
  const cookieStore = await cookies();
  if (!process.env.AUTH_COOKIE_NAME || !process.env.JWT_SECRET) {
    throw new Error('AUTH COOKIE NAME AND JWT SECRET REQUIRED');
  }
  const authCookie = (await cookieStore.get(process.env.AUTH_COOKIE_NAME)) as any;

  console.log(authCookie);

  //   const delay = (ms = 3000) => new Promise((resolve) => setTimeout(resolve, ms));
  //   await delay();
  //   if (!authCookie) {
  //     return NextResponse.redirect(new URL('/login', req.url));
  //   }

  try {
    const decoded = jwt.verify(authCookie, process.env.JWT_SECRET!) as any;
    console.log(decoded);
  } finally {
    return NextResponse.next();
  }

  //   const accesRouts: Record<string, string[]> = {
  //     'user/': ['ROLE_USER'],
  //     'teacher/': ['ROLE_TEACHER'],
  //     'admin/': ['ROLE_ADMIN'],
  //   };
  //   const url = req.nextUrl.pathname;

  //   if (!roles) return NextResponse.redirect(new URL('/login', req.url));

  //   if (accesRouts[url] && !accesRouts[url].includes(roles)) {
  //     return NextResponse.redirect(new URL('/login', req.url));
  //   }
}
