import { render, screen, waitFor } from '@testing-library/react';
import Success from '.';

describe('SuccessPage', () => {
  test('renders success message', async () => {
    render(<Success />);
    await waitFor(() => screen.getByText('COMPLETED'));
  });
  test('Link has proper href attribute', async () => {
    render(<Success />);
    const link = screen.getByLabelText('Go to Courses');
    expect(link).toHaveAttribute('href', '/user/courses');
  });
  test('Customer support link has proper href attribute', async () => {
    render(<Success />);
    const link = screen.getByLabelText('customer support');
    expect(link).toHaveAttribute('href', `mailto:${process.env.NEXT_PUBLIC_SUPPORT_EMAIL || 'support@example.com'}`);
  });
});
