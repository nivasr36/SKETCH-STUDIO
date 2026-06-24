# Complete App Components

## 1. Frontend app
The Android app is inside `android-app`. It provides the drawing canvas, tools, autosave, exports, ads, billing hooks, notifications, analytics hooks, and login dialog.

## 2. Backend/server
The Node.js server is inside `backend-server`. It handles user accounts, login, JWT sessions, artwork sync, admin statistics, notification endpoint placeholders, and payment webhook placeholders.

## 3. Database
- Android local database: SQLite through `SketchDatabase.kt`
- Backend database: SQLite through `backend-server/src/db.js`

## 4. Login system
Backend JWT email/password login is implemented. Android has a login dialog connected to `/auth/register` and `/auth/login`.

## 5. Payment system
Google Play Billing scaffold is implemented in `BillingManager.kt`. You must create Play Console products and complete purchase verification before production.

## 6. Ads system
AdMob banner test integration is implemented. Replace Google test IDs with your production IDs after AdMob approval.

## 7. Admin panel
Admin panel is inside `admin-web` and is served at `/admin` by the backend.

## 8. Analytics
Android logs local analytics events and can post events to the backend. Backend stores analytics events.

## 9. Notifications
Android notification permission/channel/helper is implemented. Backend has a placeholder endpoint for push notifications. For production push notifications, add Firebase Cloud Messaging.

## 10. Privacy policy and legal pages
Templates are inside `legal/`. Replace placeholders with your company/app details.

## 11. App Store setup
Play Store checklist is inside `play-store/`.

## 12. Testing and maintenance
QA and maintenance guides are inside `testing/`.
