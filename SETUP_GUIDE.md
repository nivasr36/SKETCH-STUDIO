# Step-by-Step Setup Guide

## A. Android app

1. Install Android Studio.
2. Open `android-app`.
3. Sync Gradle.
4. Run on emulator/device.
5. Draw and export PNG/JPG.

## B. Backend server

```bash
cd backend-server
cp .env.example .env
npm install
npm start
```

For emulator login, keep Android backend URL as:

```text
http://10.0.2.2:8080
```

For real phone testing, replace it with your computer LAN IP or deployed HTTPS URL.

## C. AdMob

1. Create an AdMob account.
2. Add your app.
3. Create a Banner ad unit.
4. Replace `admob_app_id` and `admob_banner_unit_id` in `strings.xml`.
5. Keep test IDs until you are ready for production review.

## D. Payments / Premium / Gems

1. Create Google Play Console developer account.
2. Upload the first internal testing build.
3. Create products:
   - Subscription: `premium_monthly`
   - One-time product: `gems_100`
4. Add license testers.
5. Complete ProductDetails query and launchBillingFlow code in `BillingManager.kt`.
6. Verify purchases on the backend before unlocking premium permanently.

## E. Admin panel

1. Start backend.
2. Open `/admin`.
3. Login with `.env` admin credentials.
4. View users, artworks, events, and payments.

## F. Legal pages

1. Edit templates in `legal/`.
2. Host them on your website.
3. Add URLs in Play Console.

## G. Play Store

Follow `play-store/PLAY_STORE_CHECKLIST.md`.
