import { v4 as uuid } from 'uuid';

export function createUserDto(overrides: Partial<UserDto> = {}): UserDto {
  const genUUID = uuid();

  return {
    id: genUUID,
    email: genUUID.substring(5, 15) + '@example.com',
    username: genUUID.substring(0, 10),
    userLastname: genUUID.substring(10, 15),
    imageUrl: 'https://via.placeholder.com/150',
    ...overrides,
  };
}
