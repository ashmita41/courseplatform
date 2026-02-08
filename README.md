# Course Platform API

A Spring Boot backend service for a learning platform where users can browse courses, enroll, and track their learning progress.

## 🚀 Features Implemented

### ✅ Core Features

- **Course Browsing** (Public)
  - List all courses with topic and subtopic counts
  - View course details with full topic/subtopic hierarchy
  - Markdown content support for subtopics

- **Search Functionality** (Public)
  - Case-insensitive search across courses, topics, and subtopics
  - Partial matching support
  - Searches in titles, descriptions, and content

- **User Authentication**
  - User registration with email/password
  - JWT-based login
  - Secure password hashing with BCrypt

- **Course Enrollment** (Authenticated)
  - Enroll in courses
  - Prevent duplicate enrollments
  - Enrollment tracking with timestamps

- **Progress Tracking** (Authenticated)
  - Mark subtopics as completed
  - View enrollment progress with completion percentage
  - Idempotent progress updates
  - Enrollment verification before tracking

- **API Documentation**
  - Swagger UI for interactive API testing
  - OpenAPI 3.0 specification
  - Public access to Swagger UI

### ✅ Technical Stack

- **Java 17+** with Spring Boot 3.2.0
- **PostgreSQL** database (Supabase)
- **Spring Data JPA / Hibernate** for data persistence
- **Spring Security** with JWT authentication
- **Swagger/OpenAPI** for API documentation
- **Maven** for dependency management

## 📋 API Endpoints

### Public Endpoints (No Authentication)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/courses` | List all courses |
| GET | `/api/courses/{courseId}` | Get course details |
| GET | `/api/search?q={query}` | Search courses and content |
| GET | `/swagger-ui.html` | Swagger UI documentation |

### Authenticated Endpoints (JWT Required)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login and get JWT token |
| POST | `/api/courses/{courseId}/enroll` | Enroll in a course |
| POST | `/api/subtopics/{subtopicId}/complete` | Mark subtopic as completed |
| GET | `/api/enrollments/{enrollmentId}/progress` | View enrollment progress |

## 🛠️ Setup & Run

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL database (or Supabase)

### Configuration

1. Update database credentials in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://your-database-url
   spring.datasource.username=your-username
   spring.datasource.password=your-password
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

4. Access Swagger UI:
   ```
   http://localhost:8080/swagger-ui.html
   ```

## 📊 Seed Data

The application automatically loads seed data on first startup:
- 2 courses (Physics and Mathematics)
- Multiple topics and subtopics with markdown content
- Default test users (user@example.com / password, admin@example.com / adminpass)

## 🔒 Security

- JWT-based authentication
- Password encryption with BCrypt
- Role-based access control
- CORS configuration enabled
- Public endpoints for course browsing and search

## 🗄️ Database Schema

### Entity Relationships

```
users
  ├── enrollments (user_id) → courses
  ├── subtopic_progress (user_id) → subtopics
  └── user_roles (user_id)

courses
  ├── topics (course_id)
  └── enrollments (course_id) → users

topics
  ├── subtopics (topic_id)
  └── courses (course_id)

subtopics
  ├── topics (topic_id)
  └── subtopic_progress (subtopic_id) → users
```

### Schema Definition

```sql
-- WARNING: This schema is for context only and is not meant to be run.
-- Table order and constraints may not be valid for execution.

CREATE TABLE public.courses (
  id character varying NOT NULL,
  description text,
  title character varying NOT NULL,
  CONSTRAINT courses_pkey PRIMARY KEY (id)
);

CREATE TABLE public.topics (
  id bigint NOT NULL DEFAULT nextval('topics_id_seq'::regclass),
  title character varying NOT NULL,
  topic_id character varying,
  course_id character varying NOT NULL,
  CONSTRAINT topics_pkey PRIMARY KEY (id),
  CONSTRAINT fkhn8u5k2hlwgftn6xkk7i2vh1o FOREIGN KEY (course_id) REFERENCES public.courses(id)
);

CREATE TABLE public.subtopics (
  id bigint NOT NULL DEFAULT nextval('subtopics_id_seq'::regclass),
  content text,
  subtopic_id character varying,
  title character varying NOT NULL,
  topic_id bigint NOT NULL,
  CONSTRAINT subtopics_pkey PRIMARY KEY (id),
  CONSTRAINT fkighwopn7pd9ikyje7c0iq927a FOREIGN KEY (topic_id) REFERENCES public.topics(id)
);

CREATE TABLE public.users (
  id bigint NOT NULL DEFAULT nextval('users_id_seq'::regclass),
  created_at timestamp without time zone,
  email character varying NOT NULL UNIQUE,
  password character varying NOT NULL,
  CONSTRAINT users_pkey PRIMARY KEY (id)
);

CREATE TABLE public.user_roles (
  user_id bigint NOT NULL,
  role character varying,
  CONSTRAINT fkhfh9dx7w3ubf1co1vdev94g3f FOREIGN KEY (user_id) REFERENCES public.users(id)
);

CREATE TABLE public.enrollments (
  id bigint NOT NULL DEFAULT nextval('enrollments_id_seq'::regclass),
  enrolled_at timestamp without time zone NOT NULL,
  course_id character varying NOT NULL,
  user_id bigint NOT NULL,
  CONSTRAINT enrollments_pkey PRIMARY KEY (id),
  CONSTRAINT fkho8mcicp4196ebpltdn9wl6co FOREIGN KEY (course_id) REFERENCES public.courses(id),
  CONSTRAINT fk3hjx6rcnbmfw368sxigrpfpx0 FOREIGN KEY (user_id) REFERENCES public.users(id),
  CONSTRAINT unique_user_course UNIQUE (user_id, course_id)
);

CREATE TABLE public.subtopic_progress (
  id bigint NOT NULL DEFAULT nextval('subtopic_progress_id_seq'::regclass),
  completed boolean NOT NULL,
  completed_at timestamp without time zone NOT NULL,
  subtopic_id bigint NOT NULL,
  user_id bigint NOT NULL,
  CONSTRAINT subtopic_progress_pkey PRIMARY KEY (id),
  CONSTRAINT fkhf3js2ddgfbsjhvarhn6sh7qj FOREIGN KEY (subtopic_id) REFERENCES public.subtopics(id),
  CONSTRAINT fkmildy40pkrpp50f3xie04l4wi FOREIGN KEY (user_id) REFERENCES public.users(id),
  CONSTRAINT unique_user_subtopic UNIQUE (user_id, subtopic_id)
);
```

### Key Constraints

- **Unique Constraints:**
  - `users.email` - Unique email addresses
  - `enrollments(user_id, course_id)` - One enrollment per user per course
  - `subtopic_progress(user_id, subtopic_id)` - One progress record per user per subtopic

- **Foreign Key Relationships:**
  - Topics → Courses (many-to-one)
  - Subtopics → Topics (many-to-one)
  - Enrollments → Users & Courses (many-to-one each)
  - Subtopic Progress → Users & Subtopics (many-to-one each)
  - User Roles → Users (many-to-one)

## 📝 Project Structure

```
src/main/java/com/courseplatform/
├── config/          # Configuration classes (Security, CORS, OpenAPI)
├── controller/       # REST controllers
├── dto/             # Data Transfer Objects (Request/Response)
├── entity/          # JPA entities
├── exception/       # Custom exceptions and global handler
├── loader/          # Seed data loader
├── repository/      # JPA repositories
├── security/        # JWT and security components
└── service/         # Business logic services
```

## ✅ Requirements Compliance

All mandatory requirements from the assignment specification have been implemented:

- ✅ Complete domain model (Course, Topic, Subtopic, User, Enrollment, Progress)
- ✅ Seed data loading on startup
- ✅ Public course browsing and search
- ✅ JWT authentication
- ✅ Course enrollment with duplicate prevention
- ✅ Progress tracking with enrollment verification
- ✅ Swagger UI documentation
- ✅ Case-insensitive partial search
- ✅ Proper error handling and validation

## 🧪 Testing

Use Swagger UI at `http://localhost:8080/swagger-ui.html` to test all endpoints:

1. **Test Public Endpoints:**
   - Browse courses: `GET /api/courses`
   - Search: `GET /api/search?q=velocity`

2. **Test Authentication:**
   - Register: `POST /api/auth/register`
   - Login: `POST /api/auth/login` (copy the JWT token)

3. **Test Authenticated Endpoints:**
   - Click "Authorize" in Swagger UI
   - Paste JWT token
   - Enroll in course: `POST /api/courses/{courseId}/enroll`
   - Mark progress: `POST /api/subtopics/{subtopicId}/complete`
   - View progress: `GET /api/enrollments/{enrollmentId}/progress`

