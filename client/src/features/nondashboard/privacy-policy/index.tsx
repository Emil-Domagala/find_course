'use server';
const h2Class = 'text-lg text-primary-700';
const subtitleClass = 'mb-4 text-customgreys-dirtyGrey';
const ulClass = 'list-disc list-inside';

const PrivacyPolicy = () => {
  return (
    <section className="container text-md mt-7 text-balance">
      <div className="mb-4">
        <h1 className="text-2xl font-medium text-primary-700 mb-2">Privacy Policy</h1>
        <p>
          Effective date: <span className="text-primary-700">16.04.2025r.</span>
        </p>
        <p>
          Thank you for using our platform. Your privacy is important, and this Privacy Policy outlines how your data is collected, stored, used, and protected
          when using the &quot;Find Course&quot; feature and other parts of the platform.
        </p>
      </div>
      <div className="mb-4 ">
        <h2 className={h2Class}>1. Data We Collect</h2>
        <p className={subtitleClass}>We may collect and store the following personal information when you use our service:</p>
        <ul className={ulClass}>
          <li>Email address</li>
          <li>Password (stored securely and never in plain text)</li>
          <li>Any other data you voluntarily provide (e.g., profile information, uploaded content)</li>
          <li>Cart data (automatically removed every Sunday after one week)</li>
        </ul>
      </div>
      <div className="mb-4 ">
        <h2 className={h2Class}>2. Authentication & Cookies</h2>
        <p className={subtitleClass}>We use cookies for:</p>
        <ul className={ulClass}>
          <li>Authentication (using secure, HTTP-only, SameSite=Strict cookies)</li>
          <li>Session management</li>
        </ul>
        <p>Cookies are required for proper functioning of the platform and are not used for marketing or tracking purposes.</p>
      </div>
      <div className="mb-4 ">
        <h2 className={h2Class}>3. Third-Party Services</h2>
        <p className={subtitleClass}>We use the following services to operate our platform:</p>
        <ul className={ulClass}>
          <li>
            <a href="https://stripe.com/" target="_blank" rel="noreferrer noopener nofollow" className="font-semibold text-primary-700 hover:underline">
              Stripe
            </a>
            – for handling payments securely. Your payment details are processed by Stripe and never stored on our servers.
          </li>
          <li>
            <a href="https://sendgrid.com/" target="_blank" rel="noreferrer noopener nofollow" className="font-semibold text-primary-700 hover:underline">
              SendGrid
            </a>{' '}
            – for transactional emails (e.g., verification, password resets)
          </li>
          <li>
            <a href="https://cloudinary.com/" target="_blank" rel="noreferrer noopener nofollow" className="font-semibold text-primary-700 hover:underline">
              DATABASE
            </a>{' '}
            – for storing all provided files (video and images)
          </li>
          <li>
            <a href="https://neon.tech/" target="_blank" rel="noreferrer noopener nofollow" className="font-semibold text-primary-700 hover:underline">
              DATABASE
            </a>{' '}
            – for storing all provided information
          </li>
        </ul>
        <p>These services have their own privacy policies and are GDPR-compliant.</p>
      </div>
      <div className="mb-4 ">
        <h2 className={h2Class}>4. Data Deletion</h2>
        <p>
          You can request deletion of your data at any time by contacting us at:{' '}
          <a className="text-primary-700 hover:underline" href={`mailto:${process.env.NEXT_PUBLIC_SUPPORT_EMAIL || 'support@example.com'}`}>
            {process.env.NEXT_PUBLIC_SUPPORT_EMAIL || 'support@example.com'}
          </a>
        </p>
        <p>
          We also reserve the right to delete any or all user data at any time, without prior notice, particularly for maintenance or demo-related purposes.
        </p>
      </div>
      <div className="mb-4 ">
        <h2 className={h2Class}>5. Data Security</h2>
        <p>
          We take appropriate technical and organizational measures to protect your data. However, as this is a demo platform, we recommend you avoid using
          sensitive personal data.
        </p>
      </div>
      <div className="mb-4 ">
        <h2 className={h2Class}>6. Your Rights</h2>
        <p className={subtitleClass}>You have the right to:</p>
        <ul className={ulClass}>
          <li>Request access to your data</li>
          <li>Request deletion of your data</li>
          <li>Withdraw consent at any time</li>
          <li>Contact us directly at the email above to exercise these rights.</li>
        </ul>
      </div>
      <div className="mb-4 ">
        <h2 className={h2Class}>7. Disclaimer</h2>
        <p>
          This platform is a demo application and does not offer real, purchasable courses. Data collected is used solely for testing and demonstration
          purposes.
        </p>
      </div>
    </section>
  );
};

export default PrivacyPolicy;
