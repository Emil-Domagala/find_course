import { NextRequest, NextResponse } from 'next/server';
import { cookies } from 'next/headers';
import { jwtDecode } from 'jwt-decode';
import { AuthToken } from './types/auth';

const notLoggedIn = '/auth';
const reqUser = '/user';
const reqTeacher = '/teacher';
const reqAdmin = '/admin';

export async function middleware(req: NextRequest) {
  // Make sure .env is setted
  if (!process.env.AUTH_COOKIE_NAME || !process.env.JWT_SECRET) {
    throw new Error('AUTH_COOKIE_NAME and JWT_SECRET are required');
  }
  // get cookie
  const cookieStore = await cookies();
  const authToken = cookieStore.get(process.env.AUTH_COOKIE_NAME)?.value;

  // protect dashboard
  if (
    !authToken &&
    (req.nextUrl.pathname.includes(reqUser) ||
      req.nextUrl.pathname.includes(reqTeacher) ||
      req.nextUrl.pathname.includes(reqAdmin))
  )
    return NextResponse.redirect(new URL('/auth/login', req.url));

  // redirect auth users from auth
  if (authToken && req.nextUrl.pathname.includes(notLoggedIn))
    return NextResponse.redirect(new URL('/user/courses', req.url));

  // Do nothing if public routes
  if (!authToken) return NextResponse.next();

  // get roles from token
  const decoded = jwtDecode(authToken) as AuthToken;

  // Protect teacher routes
  if (req.nextUrl.pathname.includes(reqTeacher) && !decoded.roles.includes('TEACHER'))
    return NextResponse.redirect(new URL('/user/courses', req.url)); //Or not auth

  // Protect admin routes
  if (req.nextUrl.pathname.includes(reqAdmin) && !decoded.roles.includes('ADMIN'))
    return NextResponse.redirect(new URL('/user/courses', req.url)); //Or not auth

  // save switch it should be never reached
  return NextResponse.next();
}
