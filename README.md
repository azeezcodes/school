# Attendance System API

A Spring Boot REST API for managing school classes, students, teachers, users, and attendance records.

## Features

- Manage classes, students, and teacher profiles
- Move students between classes and search students by name or admission number
- Record attendance for one student or a whole class
- Filter attendance, view student history, generate class reports, and export reports as CSV
- JWT login with role-based access control (`ADMIN`, `TEACHER`, `STUDENT`)
- Password change and password reset endpoints
- Consistent API response format, request validation, and Swagger UI documentation

## Tech stack

- Java 21
- Spring Boot 4
- Spring Web MVC, Spring Data JPA, Spring Security, Bean Validation
- PostgreSQL
- JWT (JJWT)
- SpringDoc OpenAPI / Swagger UI

## Requirements

- JDK 21 or later
- PostgreSQL

## Run locally

1. Create a PostgreSQL database named `attendance_system`.
2. In PowerShell, set your database password and a fresh random JWT signing key. This example creates a 64-byte key and keeps it only in the current terminal session:

   ```powershell
   $env:DB_PASSWORD = "your-local-postgres-password"
   $bytes = New-Object byte[] 64
   $rng = [Security.Cryptography.RandomNumberGenerator]::Create()
   $rng.GetBytes($bytes)
   $env:JWT_SECRET = [Convert]::ToBase64String($bytes)
   $rng.Dispose()
   ```

   The database URL defaults to `jdbc:postgresql://localhost:5432/attendance_system` and the username defaults to `postgres`. To override them, set `DB_URL` and `DB_USERNAME` in the same terminal.
3. Start the application from that same PowerShell terminal:

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

The API runs at `http://localhost:8080` by default. Swagger UI is available at `http://localhost:8080/swagger-ui/index.html`.

Database credentials and the JWT signing key are read from environment variables. Do not commit real credentials or signing keys. If the old values from `application.properties` were already pushed to a public or shared repository, change the database password and generate a new JWT key. The current reset-password flow returns its reset token directly and is intended for development; configure email delivery before using that flow in a real deployment.

## Main endpoints

All successful JSON endpoints use an `ApiResponse` wrapper. Most endpoints require a JWT access token in the `Authorization: Bearer <token>` header. Login, registration, and password-reset endpoints are public.

### Authentication

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/auth/register` | Register a user |
| POST | `/api/auth/login` | Log in and receive a JWT |
| POST | `/api/auth/forgot-password` | Generate a development password-reset token |
| POST | `/api/auth/reset-password` | Set a new password using a valid reset token |

### Classes

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/classes` | Create a class |
| GET | `/api/classes` | List classes |
| GET | `/api/classes/{id}` | Get a class |
| PUT | `/api/classes/{id}` | Update a class |
| DELETE | `/api/classes/{id}` | Delete a class |
| GET | `/api/classes/{id}/students` | List students in a class |
| GET | `/api/classes/{id}/teachers` | List teachers assigned to a class |
| PUT | `/api/classes/{id}/students` | Move existing students into a class |

### Students and teachers

| Method | Endpoint | Purpose |
|---|---|---|
| POST, GET | `/api/students` | Create or list students |
| GET, PUT, DELETE | `/api/students/{id}` | Retrieve, update, or delete a student |
| GET | `/api/students/search?query={text}` | Search by name or admission number |
| POST, GET | `/api/teachers` | Create or list teachers |
| GET, PUT, DELETE | `/api/teachers/{id}` | Retrieve, update, or delete a teacher |
| PUT | `/api/teachers/{id}/activate` | Activate a teacher |
| PUT | `/api/teachers/{id}/deactivate` | Deactivate a teacher |
| PUT | `/api/teachers/{id}/assign-class/{classId}` | Assign a teacher to a class |

### User accounts

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/users` | List user accounts (admin only) |
| GET | `/api/users/{id}` | Get a user account (admin only) |
| PUT | `/api/users/{id}/activate` | Activate a user account (admin only) |
| PUT | `/api/users/{id}/deactivate` | Deactivate a user account (admin only) |
| DELETE | `/api/users/{id}` | Delete a user account (admin only) |
| PUT | `/api/users/{id}/link-teacher/{teacherId}` | Link a user account to a teacher profile (admin only) |
| PUT | `/api/users/me/change-password` | Change the signed-in user's password |

### Attendance

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/attendance` | Mark attendance for one student |
| POST | `/api/attendance/bulk` | Mark attendance for multiple students |
| GET | `/api/attendance` | List records; optional `studentId`, `classId`, `date`, and `status` filters |
| GET, PUT, DELETE | `/api/attendance/{id}` | Retrieve, correct, or delete an attendance record |
| GET | `/api/attendance/student/{studentId}` | View a student's attendance history |
| GET | `/api/attendance/class/{classId}?date=YYYY-MM-DD` | View class attendance for a date |
| GET | `/api/attendance/report?classId={id}&startDate=YYYY-MM-DD&endDate=YYYY-MM-DD` | Generate a class summary |
| GET | `/api/attendance/export?classId={id}&startDate=YYYY-MM-DD&endDate=YYYY-MM-DD&format=csv` | Download the summary as CSV |

## Authentication example

Log in at `POST /api/auth/login` with JSON credentials, then use the returned JWT for protected requests:

```http
Authorization: Bearer your-jwt-token
```

Attendance statuses are `PRESENT`, `ABSENT`, and `LATE`.
