const h2Class = 'text-lg text-primary-700';
const ulClass = 'list-disc list-inside';
const subtitleClass = 'mb-2 text-customgreys-dirtyGrey';

const TermsOfUsePage = () => {
  return (
    <section className="container text-md mt-7 text-balance">
      <div className="mb-4 ">
        <h1 className="text-2xl font-medium text-primary-700 mb-2 mt-8">Terms of Use</h1>
        <p>
          Effective date: <span className="text-primary-700">16.04.2025r.</span>
        </p>
        <p>
          By accessing or using this platform, you agree to be bound by these Terms of Use. If you do not agree, please
          do not use the service.
        </p>
      </div>
      <div className="mb-4">
        <h2 className={h2Class}>1. Demo Platform</h2>
        <p>
          This application is for demonstration purposes only. It does not offer real products or services. Any content,
          courses, or transactions are fictional and for testing or presentation only.
        </p>
      </div>

      <div className="mb-4">
        <h2 className={h2Class}>2. User Conduct</h2>
        <p className={subtitleClass}>You agree not to:</p>
        <ul className={ulClass}>
          <li>Upload or share harmful, offensive, or illegal content.</li>
          <li>Attempt to gain unauthorized access to other accounts or server data</li>
          <li>Use this platform for any commercial or malicious purposes</li>
        </ul>
      </div>
      <div className="mb-4">
        <h2 className={h2Class}>3. Account & Data</h2>
        <p>
          Accounts you create may be deleted at any time without notice. Cart and user data are temporary and reset
          weekly.
        </p>
      </div>

      <div className="mb-4">
        <h2 className={h2Class}>4. Payments</h2>
        <p>
          Stripe is integrated into the platform for testing payment functionality. Any transactions made are not actual
          purchases and do not grant ownership or access to real services.
        </p>
      </div>

      <div className="mb-4">
        <h2 className={h2Class}>5. Uploaded Content</h2>
        <p>
          You retain ownership of the content you upload, but you grant us a non-exclusive right to use it within the
          application. We reserve the right to remove or modify content at our discretion.
        </p>
      </div>

      <div className="mb-4">
        <h2 className={h2Class}>6. No Warranty</h2>
        <p>
          The platform is provided "as is" with no warranties, guarantees, or promises of availability, reliability, or
          data persistence.
        </p>
      </div>
      <div className="mb-4">
        <h2 className={h2Class}>7. Changes</h2>
        <p>We may update these Terms at any time. Continued use after changes means you accept the new terms.</p>
      </div>

      <div className="mb-4">
        <h2 className={h2Class}>8. Contact</h2>
        <p>
          If you have any questions regarding these terms, you may contact us at:{' '}
          <a
            className="text-primary-700 hover:underline"
            href={`mailto:${process.env.NEXT_PUBLIC_SUPPORT_EMAIL || 'support@example.com'}`}>
            {process.env.NEXT_PUBLIC_SUPPORT_EMAIL || 'support@example.com'}
          </a>
        </p>
      </div>
    </section>
  );
};

export default TermsOfUsePage;
