import { CartDto, CartResponse } from '@/features/nondashboard/user/cart/cart';
import { v4 as uuid } from 'uuid';
import { createCourseDto } from './courseFactory';

export function createCartDto(overrides: Partial<CartDto> = {}): CartDto {
  return {
    id: overrides.id ?? uuid(),
    courses: overrides.courses ?? [createCourseDto()],
    totalPrice: overrides.courses?.reduce((acc, course) => acc + course.price, 0) ?? 0,
  };
}

export function createCartResponse(overrides: Partial<CartResponse> = {}): CartResponse {
  return {
    cart: overrides.cart ?? createCartDto(),
    warnings: overrides.warnings ?? [],
  };
}
