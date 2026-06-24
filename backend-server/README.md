# SketchColor Backend Server

## Setup

```bash
cp .env.example .env
npm install
npm start
```

## Important endpoints

```text
GET  /health
POST /auth/register
POST /auth/login
GET  /me
POST /artworks
GET  /artworks
POST /analytics
POST /billing/google-play-webhook
POST /notifications/send
GET  /admin/stats
```

## Admin panel

Open:

```text
http://localhost:8080/admin
```

Login using admin email/password from `.env`.

## Production notes

- Change `JWT_SECRET`.
- Deploy on HTTPS.
- Use a managed database for serious production use.
- Add Google Play Developer API purchase verification before granting premium/gems.
- Add Firebase Cloud Messaging if you want real push notifications.
- Add rate limiting and abuse protection before public launch.
