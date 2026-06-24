const jwt = require('jsonwebtoken');

function signToken(user, secret) {
  return jwt.sign({ id: user.id, email: user.email, role: user.role }, secret, { expiresIn: '30d' });
}

function authRequired(secret) {
  return (req, res, next) => {
    const header = req.headers.authorization || '';
    const token = header.startsWith('Bearer ') ? header.slice(7) : null;
    if (!token) return res.status(401).json({ error: 'Missing token' });
    try {
      req.user = jwt.verify(token, secret);
      next();
    } catch (err) {
      return res.status(401).json({ error: 'Invalid token' });
    }
  };
}

function adminRequired(req, res, next) {
  if (!req.user || req.user.role !== 'admin') return res.status(403).json({ error: 'Admin only' });
  next();
}

module.exports = { signToken, authRequired, adminRequired };
