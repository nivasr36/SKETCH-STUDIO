# Android App Setup

## Run locally

1. Open this `android-app` folder in Android Studio.
2. Sync Gradle.
3. Run on emulator/device.

## Core app features

- Drawing canvas
- Brush size slider
- Color palette
- Eraser
- Undo / redo
- Auto-save latest progress to SQLite
- Auto-load latest progress on app start
- High-resolution PNG export
- High-resolution JPG/JPEG export
- AdMob test banner
- Billing scaffold for Premium and Gems
- Backend login dialog
- Analytics logging
- Notifications

## Production replacements

Open:

```text
app/src/main/res/values/strings.xml
```

Replace:

```text
admob_app_id
admob_banner_unit_id
```

Open:

```text
app/src/main/java/com/kompsia/sketchcolor/network/ApiClient.kt
```

Replace:

```text
http://10.0.2.2:8080
```

with your deployed backend URL.

## Payment product IDs

Create these in Google Play Console:

```text
premium_monthly
#gives ad-free premium membership

gems_100
#consumable gems pack
```

Then complete the product details query and purchase launch in `BillingManager.kt`.

## High-resolution save

The app renders the normalized vector stroke data to a 3x bitmap and saves to Android Gallery using MediaStore.
