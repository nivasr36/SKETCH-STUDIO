# QA Test Plan

## Drawing

- [ ] Draw fast strokes continuously for 2 minutes.
- [ ] Change brush size and draw.
- [ ] Change colors and draw.
- [ ] Use eraser.
- [ ] Undo and redo multiple strokes.
- [ ] Clear canvas and verify autosave updates.

## Autosave

- [ ] Draw something.
- [ ] Close app completely.
- [ ] Reopen app.
- [ ] Verify latest progress loads automatically.

## Gallery export

- [ ] Save PNG.
- [ ] Save JPG.
- [ ] Open Gallery and confirm image is visible.
- [ ] Zoom in to confirm high-resolution quality.

## Login/backend

- [ ] Start backend server.
- [ ] Register a new account from app.
- [ ] Login with same account.
- [ ] Confirm backend `/me` works with JWT token.

## Ads

- [ ] Confirm test banner appears.
- [ ] Confirm no production ad IDs are used during development.

## Billing

- [ ] Create Play Console test products.
- [ ] Add license testers.
- [ ] Test purchase with internal testing build.
- [ ] Verify backend purchase validation before granting premium.

## Notifications

- [ ] Android 13+: permission dialog appears.
- [ ] Save artwork and confirm notification appears if permission granted.

## Device coverage

- [ ] Small phone
- [ ] Large phone
- [ ] Tablet
- [ ] Android 8/9
- [ ] Android 13+
- [ ] Latest Android version
