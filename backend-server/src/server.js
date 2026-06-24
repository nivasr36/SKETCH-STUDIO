require('dotenv').config();
const path = require('path');
const express = require('express');
const cors = require('cors');
const helmet = require('helmet');
const morgan = require('morgan');
const bcrypt = require('bcryptjs');
const { createDb } = require('./db');
const { signToken, authRequired, adminRequired } = require('./auth');

const PORT = Number(process.env.PORT || 8080);
const JWT_SECRET = process.env.JWT_SECRET || 'dev_secret_change_me';
const ADMIN_EMAIL = process.env.ADMIN_EMAIL || 'admin@example.com';
const ADMIN_PASSWORD = process.env.ADMIN_PASSWORD || 'admin12345';
const DATABASE_PATH = process.env.DATABASE_PATH || './sketchcolor.sqlite';

const db = createDb(DATABASE_PATH, ADMIN_EMAIL, ADMIN_PASSWORD);
const app = express();

app.use(helmet({ contentSecurityPolicy: false }));
app.use(cors());
app.use(express.json({ limit: '10mb' }));
app.use(morgan('dev'));

app.get('/health', (req, res) => res.json({ ok: true, app: 'SketchColor Studio API' }));

app.post('/auth/register', (req, res) => {
  const { email, password } = req.body;
  if (!email || !password || password.length < 6) return res.status(400).json({ error: 'Email and 6+ character password required' });
  try {
    const result = db.prepare('INSERT INTO users(email, password_hash, role, created_at) VALUES(?,?,?,?)').run(
      String(email).toLowerCase(),
      bcrypt.hashSync(password, 10),
      'user',
      Date.now()
    );
    const user = db.prepare('SELECT id, email, role FROM users WHERE id=?').get(result.lastInsertRowid);
    return res.json({ message: 'Registered successfully', token: signToken(user, JWT_SECRET), user });
  } catch (err) {
    return res.status(409).json({ error: 'Email already registered' });
  }
});

app.post('/auth/login', (req, res) => {
  const { email, password } = req.body;
  const user = db.prepare('SELECT * FROM users WHERE email=?').get(String(email || '').toLowerCase());
  if (!user || !bcrypt.compareSync(password || '', user.password_hash)) return res.status(401).json({ error: 'Invalid email or password' });
  return res.json({ message: 'Logged in successfully', token: signToken(user, JWT_SECRET), user: { id: user.id, email: user.email, role: user.role } });
});

app.get('/me', authRequired(JWT_SECRET), (req, res) => {
  const user = db.prepare('SELECT id, email, role, premium_until, gems, created_at FROM users WHERE id=?').get(req.user.id);
  res.json({ user });
});

app.post('/artworks', authRequired(JWT_SECRET), (req, res) => {
  const { title, strokesJson } = req.body;
  if (!strokesJson) return res.status(400).json({ error: 'strokesJson required' });
  const result = db.prepare('INSERT INTO artworks(user_id, title, strokes_json, updated_at) VALUES(?,?,?,?)').run(
    req.user.id,
    title || 'Untitled artwork',
    strokesJson,
    Date.now()
  );
  res.json({ message: 'Artwork synced', artworkId: result.lastInsertRowid });
});

app.get('/artworks', authRequired(JWT_SECRET), (req, res) => {
  const rows = db.prepare('SELECT id, title, strokes_json AS strokesJson, updated_at AS updatedAt FROM artworks WHERE user_id=? ORDER BY updated_at DESC').all(req.user.id);
  res.json({ artworks: rows });
});

app.post('/analytics', authRequired(JWT_SECRET), (req, res) => {
  const { eventName, payload } = req.body;
  if (!eventName) return res.status(400).json({ error: 'eventName required' });
  db.prepare('INSERT INTO analytics_events(user_id, event_name, payload, created_at) VALUES(?,?,?,?)').run(
    req.user.id,
    eventName,
    JSON.stringify(payload || {}),
    Date.now()
  );
  res.json({ ok: true });
});

app.post('/billing/google-play-webhook', (req, res) => {
  // Production TODO:
  // 1. Verify the Google Play purchase token using Google Play Developer API.
  // 2. Check package name, product ID, order status, expiry time, and acknowledgement.
  // 3. Update users.premium_until or users.gems only after verification.
  db.prepare('INSERT INTO payments(platform, product_id, purchase_token, status, created_at) VALUES(?,?,?,?,?)').run(
    'google_play',
    req.body.productId || 'unknown',
    req.body.purchaseToken || null,
    'received_unverified',
    Date.now()
  );
  res.json({ received: true, note: 'Webhook received. Add Google Play Developer API verification before production.' });
});

app.post('/notifications/send', authRequired(JWT_SECRET), adminRequired, (req, res) => {
  // Production TODO: connect Firebase Cloud Messaging and send to saved user device tokens.
  res.json({ queued: true, note: 'Notification endpoint placeholder. Connect FCM for production push.' });
});

app.get('/admin/stats', authRequired(JWT_SECRET), adminRequired, (req, res) => {
  const users = db.prepare('SELECT COUNT(*) AS count FROM users').get().count;
  const artworks = db.prepare('SELECT COUNT(*) AS count FROM artworks').get().count;
  const events = db.prepare('SELECT COUNT(*) AS count FROM analytics_events').get().count;
  const payments = db.prepare('SELECT COUNT(*) AS count FROM payments').get().count;
  const latestUsers = db.prepare('SELECT id, email, role, created_at AS createdAt FROM users ORDER BY created_at DESC LIMIT 10').all();
  res.json({ users, artworks, events, payments, latestUsers });
});

app.use('/admin', express.static(path.join(__dirname, '../../admin-web')));

app.listen(PORT, () => {
  console.log(`SketchColor API running on http://localhost:${PORT}`);
  console.log(`Admin panel: http://localhost:${PORT}/admin`);
});
