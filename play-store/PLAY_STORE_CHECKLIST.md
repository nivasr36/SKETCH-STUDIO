# Google Play Store Launch Checklist

## Before internal testing

- [ ] Change package name only if needed before first upload.
- [ ] Replace app icon and branding.
- [ ] Replace AdMob test IDs with production IDs only after testing.
- [ ] Create Play Billing products: `premium_monthly`, `gems_100`.
- [ ] Add server-side Google Play purchase verification.
- [ ] Add privacy policy URL.
- [ ] Add account deletion URL if accounts are used.
- [ ] Prepare screenshots for phone and tablet.
- [ ] Prepare 1024x500 feature graphic.
- [ ] Fill Data Safety form accurately.
- [ ] Fill Ads declaration accurately.
- [ ] Fill content rating questionnaire.
- [ ] Test APK/AAB on multiple Android versions.

## Store listing draft

App name: SketchColor Studio

Short description:

```text
Draw, sketch, color, autosave your progress, and export high-resolution art.
```

Full description:

```text
SketchColor Studio is a simple and powerful drawing, sketching, and coloring app. Create artwork with brushes, colors, eraser, undo and redo tools. Your progress is saved automatically, and you can export completed work to your Gallery in high-resolution PNG or JPEG format.

Features:
- Smooth drawing canvas
- Brush and eraser tools
- Color palette
- Undo and redo
- Automatic progress saving
- High-resolution PNG/JPEG export
- Optional premium membership
```

## Release flow

1. Build debug APK and test locally.
2. Generate signed release AAB in Android Studio.
3. Upload to Internal Testing.
4. Fix crashes and policy warnings.
5. Promote to Closed Testing.
6. Promote to Production after enough successful testing.
