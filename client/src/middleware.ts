import { NextRequest, NextResponse } from 'next/server';
import { cookies } from 'next/headers';
import { jwtDecode } from 'jwt-decode';
import { AuthToken } from './types/auth';

const AUTH_COOKIE_NAME = process.env.AUTH_COOKIE_NAME;
const JWT_SECRET = process.env.JWT_SECRET;

const AUTH_PREFIX = '/auth';
const USER_PREFIX = '/user';
const TEACHER_PREFIX = '/teacher';
const ADMIN_PREFIX = '/admin';
const CONFIRM_EMAIL_PATH = '/confirm-email';

const DEFAULT_LOGIN_REDIRECT = '/auth/login';
const DEFAULT_LOGGED_IN_REDIRECT = '/user/courses';

export async function middleware(req: NextRequest) {
  if (!AUTH_COOKIE_NAME || !JWT_SECRET) {
    console.error('Missing AUTH_COOKIE_NAME or JWT_SECRET environment variables.');
    return new NextResponse('Internal Server Error: Missing configuration', { status: 500 });
  }
  // get cookie
  const cookieStore = await cookies();
  const authToken = cookieStore.get(AUTH_COOKIE_NAME)?.value;

  const { pathname } = req.nextUrl;
  const isAuthRoute = pathname.startsWith(AUTH_PREFIX);
  const isUserRoute = pathname.startsWith(USER_PREFIX);
  const isTeacherRoute = pathname.startsWith(TEACHER_PREFIX);
  const isAdminRoute = pathname.startsWith(ADMIN_PREFIX);
  const isConfirmEmailRoute = pathname === CONFIRM_EMAIL_PATH;

  const isProtectedRoute = isUserRoute || isTeacherRoute || isAdminRoute;

  // --- Logic for NO Token (Not Logged In) ---
  if (!authToken) {
    // Allow access to auth routes if not logged in
    if (isAuthRoute) {
      return NextResponse.next();
    }
    // Redirect protected routes AND confirm-email route to login if not logged in
    if (isProtectedRoute || isConfirmEmailRoute) {
      console.log(`[Middleware] No token, redirecting protected route ${pathname} to login.`);
      return NextResponse.redirect(new URL(DEFAULT_LOGIN_REDIRECT, req.url));
    }
    // Allow other public routes
    return NextResponse.next();
  }

  let decoded: AuthToken | null = null;
  try {
    // Decode the token - Note: This does NOT verify the signature
    decoded = jwtDecode<AuthToken>(authToken);
  } catch (error) {
    console.error('[Middleware] Failed to decode token:', error);
    // If token is invalid/malformed, treat as unauthenticated
    // Clear the invalid cookie and redirect to login
    const response = NextResponse.redirect(new URL(DEFAULT_LOGIN_REDIRECT, req.url));
    response.cookies.delete(AUTH_COOKIE_NAME);
    console.log(`[Middleware] Invalid token, redirecting ${pathname} to login and clearing cookie.`);
    return response;
  }

  // --- Check Email Verification Status ---
  // Ensure your AuthToken type reliably has isEmailVerified
  if (decoded && decoded.isEmailVerified === false) {
    // If email is NOT verified, ONLY allow access to the confirm-email page
    if (isConfirmEmailRoute || !isProtectedRoute) {
      return NextResponse.next(); // Allow access
    }
    // Redirect ALL other pages (including auth, protected) to confirm-email
    console.log(`[Middleware] Email not verified, redirecting ${pathname} to ${CONFIRM_EMAIL_PATH}.`);
    return NextResponse.redirect(new URL(CONFIRM_EMAIL_PATH, req.url));
  }

  if (decoded && decoded.isEmailVerified === true) {
    // 1. Redirect verified users away from /auth pages
    if (isAuthRoute) {
      console.log(
        `[Middleware] Verified user accessing auth route ${pathname}, redirecting to ${DEFAULT_LOGGED_IN_REDIRECT}.`,
      );
      return NextResponse.redirect(new URL(DEFAULT_LOGGED_IN_REDIRECT, req.url));
    }

    // Ensure your AuthToken type reliably has roles array
    const roles = decoded.roles  || [];

    // 2. Protect teacher routes
    if (isTeacherRoute && !roles.includes('TEACHER')) {
      console.log(`[Middleware] User without TEACHER role accessing ${pathname}, redirecting.`);
      return NextResponse.redirect(new URL(DEFAULT_LOGGED_IN_REDIRECT, req.url)); // Or a specific "access denied" page
    }

    // 3. Protect admin routes
    if (isAdminRoute && !roles.includes('ADMIN')) {
      console.log(`[Middleware] User without ADMIN role accessing ${pathname}, redirecting.`);
      return NextResponse.redirect(new URL(DEFAULT_LOGGED_IN_REDIRECT, req.url)); // Or a specific "access denied" page
    }

    // If none of the above conditions match, allow access
    return NextResponse.next();
  }

  // --- Fallback (Should ideally not be reached if logic above is complete) ---
  console.warn('[Middleware] Reached unexpected fallback state.');
  // Default to redirecting to login as a safe measure
  const response = NextResponse.redirect(new URL(DEFAULT_LOGIN_REDIRECT, req.url));
  if (authToken) {
    response.cookies.delete(AUTH_COOKIE_NAME); // Clear potentially problematic cookie
  }
  return response;
}

export const config = {
  matcher: ['/((?!api|_next/static|_next/image|favicon.ico|images|assets).*)'],
};
