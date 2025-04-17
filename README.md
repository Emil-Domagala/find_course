 <h1>🧠 Fullstack Course Platform</h1>

  <p>
    A full-featured course platform inspired by Udemy, built with <strong>Java (Spring Boot)</strong> for the backend and <strong>Next.js (React)</strong> for the frontend. Users can browse, purchase, and follow video-based courses, with support for teachers and admins to manage content and users.
  </p>

  <h2>🔧 Tech Stack</h2>

  <h3>Frontend</h3>
  <ul>
    <li>Next.js, React, Tailwind CSS</li>
    <li>Redux Toolkit + RTK Query</li>
    <li>shadcn/ui for modern UI components</li>
    <li>Zod for validation</li>
    <li>Stripe for payments</li>
    <li>FilePond for media uploads</li>
  </ul>

  <h3>Backend</h3>
  <ul>
    <li>Java, Spring Boot, Spring Security, Spring Mail</li>
    <li>PostgreSQL</li>
    <li>JWT (Access + Refresh tokens) with secure cookie-based auth</li>
    <li>MapStruct for mapping</li>
    <li>Lombok for boilerplate reduction</li>
    <li>SendGrid for email verification and password reset</li>
  </ul>

  <h2>🔐 Key Features</h2>
  <ul>
    <li>👤 User, Teacher, and Admin roles with separate dashboards</li>
    <li>📚 Course creation, purchase, and progress tracking</li>
    <li>🧩 Courses structured as Sections & Chapters (video or text)</li>
    <li>🧾 Secure payments via Stripe</li>
    <li>🖼 Image upload (avatars, thumbnails)</li>
    <li>🔍 Filtering, search, and pagination</li>
    <li>✅ Email verification and forgot password flow</li>
    <li>🛒 Cart and checkout</li>
    <li>🔒 Secure authentication (http-only cookies, SameSite=Strict)</li>
  </ul>

  <h2>🚀 Getting Started</h2>
  <ol>
    <li>Clone the project</li>
    <li>
      Set up environment variables (see <code>.env.example</code> for frontend and <code>application.properties</code> with <code>.env.example</code> for backend):
      <ul>
        <li>Stripe API keys</li>
        <li>SendGrid API key</li>
        <li>PostgreSQL DB connection details</li>
        <li>JWT secrets</li>
      </ul>
    </li>
    <li>Run backend and frontend: <code>npm run dev</code></li>
    <li>Access the app at <a href="http://localhost:3000">http://localhost:3000</a></li>
  </ol>
