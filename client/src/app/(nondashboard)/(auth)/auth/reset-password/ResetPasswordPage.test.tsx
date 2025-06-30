import { render, screen, waitFor } from '@testing-library/react';
import { useRouter } from 'next/navigation';
import { useRegisterMutation } from '@/state/endpoints/auth/auth';
import userEvent, { UserEvent } from '@testing-library/user-event';
import { assert } from 'node:console';

test('ResetPasswordPage', () => {});