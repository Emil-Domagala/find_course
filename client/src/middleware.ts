import { NextRequest, NextResponse } from 'next/server';
import { cookies } from 'next/headers';
import { jwtDecode } from 'jwt-decode';

export async function middleware(req: NextRequest) {
  const cookieStore = await cookies();
  if (!process.env.AUTH_COOKIE_NAME || !process.env.JWT_SECRET) {
    throw new Error('AUTH_COOKIE_NAME and JWT_SECRET are required');
  }

  const authToken = cookieStore.get(process.env.AUTH_COOKIE_NAME)?.value;

  if (!authToken) return;

  try {
    const decoded = jwtDecode(authToken) as any;
  } catch (err) {
    console.error('JWT Verification Failed:', err);
  }
}
