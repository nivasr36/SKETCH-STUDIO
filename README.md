# SketchColor Studio - Complete Android Drawing, Sketching & Coloring App

SketchColor Studio is a production-ready starter project for an Android drawing, sketching, and coloring app. It includes:

- Native Android frontend app in Kotlin
- Drawing canvas with brush, eraser, colors, undo, redo, clear
- Automatic progress saving using local SQLite database
- Export completed artwork to Gallery as high-resolution PNG or JPEG/JPG
- Backend/server API with user registration, login, JWT sessions, artwork sync, admin stats, payment webhook placeholders, and notification endpoints
- Database schema for users, artwork, payments, analytics events
- AdMob banner ads with Google test ad IDs
- Google Play Billing scaffold for Premium Membership and Gems
- Admin panel web page
- Analytics event logging
- Android notifications helper
- Privacy policy, terms, Play Store checklist, testing and maintenance guide
- GitHub Actions workflow to generate APK

> Important: the app runs locally for drawing, autosave, and image export. Payments, live ads, backend cloud hosting, Firebase/Google sign-in, Play Store upload, and production analytics require your own Google Play Console, AdMob, Firebase, domain, hosting, and legal/business details.

## Project structure

```text
sketchcolor_studio/
├─ android-app/                 # Native Android Studio project
├─ backend-server/              # Node.js + SQLite backend API
├─ admin-web/                   # Admin panel served by backend
├─ legal/                       # Privacy Policy and Terms templates
├─ play-store/                  # Store listing and launch checklists
├─ testing/                     # QA and maintenance plans
└─ .github/workflows/           # GitHub APK build workflow
```

## Fastest way to test the Android app

1. Open `android-app` in Android Studio.
2. Let Gradle sync.
3. Run the app on an Android device or emulator.
4. Draw on the canvas.
5. Press **Save PNG** or **Save JPG**. The image is saved to Gallery under `Pictures/SketchColor Studio`.

## Build APK using GitHub Actions

1. Create a new GitHub repository.
2. Upload all files from this folder.
3. Go to **Actions**.
4. Open **Android APK Build**.
5. Click **Run workflow**.
6. Download the generated APK from workflow artifacts.

## Backend local setup

```bash
cd backend-server
cp .env.example .env
npm install
npm start
```

Open admin panel:

```text
http://localhost:8080/admin
```

Default admin user is created from `.env` values on first run.

## Production setup summary

- Replace Android package name before launch if needed: `com.kompsia.sketchcolor`
- Replace app name and logo in Android resources.
- Create Firebase project only if you want Firebase Auth/Analytics/FCM.
- Create AdMob app and replace test IDs in `android-app/app/src/main/res/values/strings.xml`.
- Create Play Console in-app products:
  - `premium_monthly`
  - `gems_100`
- Deploy backend to Render/Railway/Fly.io/AWS/VPS.
- Put backend URL in `ApiClient.kt`.
- Replace legal templates with your company details.
- Test on at least 5 Android devices/screen sizes.
- Publish internal testing build before production.

## What is already working

- Drawing canvas
- Brush/eraser/color/size tools
- Undo/redo
- Clear canvas
- Auto-save after strokes
- Load latest saved progress automatically
- Local SQLite artwork database
- Export high-resolution PNG/JPEG to Gallery
- AdMob test banner integration
- Billing client initialization scaffold
- Local analytics logging
- Notification helper
- Backend registration/login/artwork sync/admin APIs
- Admin dashboard UI

## What you must configure yourself

- Live AdMob app ID and ad unit IDs
- Google Play Billing product setup and server purchase verification
- Google Play Console account
- Backend public hosting URL
- Real privacy policy/terms/company details
- App icon, screenshots, feature graphic, store listing
- Firebase project if you use Firebase services

