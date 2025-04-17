🧠 Fullstack Course Platform

A full-featured course platform inspired by Udemy, built with Java (Spring Boot) for the backend and Next.js (React) for the frontend. Users can browse, purchase, and follow video-based courses, with support for teachers and admins to manage content and users.

🔧 Tech Stack

Frontend
Next.js, React, Tailwind CSS
Redux Toolkit + RTK Query
shadcn/ui for modern UI components
Zod for validation
Stripe for payments
FilePond for media uploads
Backend
Java, Spring Boot, Spring Security, Spring Mail
PostgreSQL
JWT (Access + Refresh tokens) with secure cookie-based auth
MapStruct for mapping
Lombok for boilerplate reduction
SendGrid for email verification and password reset
🔐 Key Features

👤 User, Teacher, and Admin roles with separate dashboards
📚 Course creation, purchase, and progress tracking
🧩 Courses structured as Sections & Chapters (video or text)
🧾 Secure payments via Stripe
🖼 Image upload (avatars, thumbnails)
🔍 Filtering, search, and pagination
✅ Email verification and forgot password flow
🛒 Cart and checkout
🔒 Secure authentication (http-only cookies, SameSite=Strict)
🚀 Getting Started

Clone the project
Set up environment variables (see .env.example for frontend and application.properties with .env.example for backend):
Stripe API keys
SendGrid API key
PostgreSQL DB connection details
JWT secrets
Run backend and frontend (npm run dev)
Access the app at http://localhost:3000
