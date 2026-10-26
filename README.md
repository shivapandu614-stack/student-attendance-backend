# Student Attendance Backend

Spring Boot REST API for the student attendance system. Requires JDK 17 or 21 and MySQL 8 or later.

## Run locally

Configure the database environment variables if needed, then run:

```powershell
.\mvnw.cmd spring-boot:run
```

The default database is `student_attendance_db` on `localhost:3306`, using MySQL username `root` and an empty password. Override these defaults with:

| Variable | Purpose |
| --- | --- |
| `DB_URL` | JDBC URL |
| `DB_USERNAME` | MySQL username |
| `DB_PASSWORD` | MySQL password |
| `PORT` | HTTP port (default `8080`) |
| `JWT_SECRET` | Private HMAC key of at least 32 bytes |
| `JWT_EXPIRATION_MINUTES` | Token lifetime (default `480`) |
| `SEED_DEMO_DATA` | Create local demo data (default `true`) |
| `CORS_ALLOWED_ORIGINS` | Comma-separated exact browser origins allowed to call the API |

For a separate frontend deployment, set `CORS_ALLOWED_ORIGINS` to the frontend's origin, such as `https://your-frontend.vercel.app` (no trailing slash). Do not use `*` for production. Set `DB_*`, `JWT_SECRET`, and CORS variables in the backend host's secret/environment settings.

The frontend is maintained separately in [student-attendance-frontend](https://github.com/shivapandu614-stack/student-attendance-frontend).
