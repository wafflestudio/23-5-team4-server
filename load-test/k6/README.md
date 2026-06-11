# k6 Load Test (Pure Backend Concurrency)

## 1) Server start option

To exclude SMTP/SES latency from registration concurrency tests, start the server with:

```bash
REGISTRATION_EMAIL_NOTIFICATIONS_ENABLED=false ./gradlew bootRun
```

This keeps registration transaction logic active while disabling post-commit email sends.

## 2) Seed test users

```bash
mysql -u <user> -p <db> < load-test/k6/seed/seed_users.sql
```

## 3) Run scenario

```bash
k6 run -e BASE_URL=http://localhost:8080 load-test/k6/scenarios/03_registration_concurrent.js
```

## 4) Cleanup

```bash
mysql -u <user> -p <db> < load-test/k6/seed/cleanup_users.sql
```
