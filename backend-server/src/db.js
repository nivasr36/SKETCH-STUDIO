const Database = require('better-sqlite3');
const bcrypt = require('bcryptjs');

function createDb(path, adminEmail, adminPassword) {
  const db = new Database(path);
  db.pragma('journal_mode = WAL');

  db.exec(`
    CREATE TABLE IF NOT EXISTS users (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      email TEXT UNIQUE NOT NULL,
      password_hash TEXT NOT NULL,
      role TEXT NOT NULL DEFAULT 'user',
      premium_until INTEGER,
      gems INTEGER NOT NULL DEFAULT 0,
      created_at INTEGER NOT NULL
    );

    CREATE TABLE IF NOT EXISTS artworks (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      user_id INTEGER NOT NULL,
      title TEXT NOT NULL,
      strokes_json TEXT NOT NULL,
      updated_at INTEGER NOT NULL,
      FOREIGN KEY(user_id) REFERENCES users(id)
    );

    CREATE TABLE IF NOT EXISTS payments (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      user_id INTEGER,
      platform TEXT NOT NULL,
      product_id TEXT NOT NULL,
      purchase_token TEXT,
      status TEXT NOT NULL,
      created_at INTEGER NOT NULL,
      FOREIGN KEY(user_id) REFERENCES users(id)
    );

    CREATE TABLE IF NOT EXISTS analytics_events (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      user_id INTEGER,
      event_name TEXT NOT NULL,
      payload TEXT NOT NULL,
      created_at INTEGER NOT NULL,
      FOREIGN KEY(user_id) REFERENCES users(id)
    );
  `);

  const admin = db.prepare('SELECT id FROM users WHERE email=?').get(adminEmail);
  if (!admin) {
    db.prepare('INSERT INTO users(email, password_hash, role, created_at) VALUES(?,?,?,?)').run(
      adminEmail,
      bcrypt.hashSync(adminPassword, 10),
      'admin',
      Date.now()
    );
    console.log(`Admin user created: ${adminEmail}`);
  }

  return db;
}

module.exports = { createDb };
