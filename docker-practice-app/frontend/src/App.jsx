import { useState } from "react";

// Letta a BUILD TIME da Vite (variabile VITE_API_URL). Default: backend locale.
const API_URL = import.meta.env.VITE_API_URL || "http://localhost:8080";

export default function App() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [loggedUser, setLoggedUser] = useState(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      const res = await fetch(`${API_URL}/api/auth/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password }),
      });
      const data = await res.json();
      if (res.ok) {
        setLoggedUser(data.username);
      } else {
        setError(data.message || "Errore di login");
      }
    } catch {
      setError("Backend non raggiungibile");
    } finally {
      setLoading(false);
    }
  }

  if (loggedUser) {
    return (
      <main className="card">
        <h1>Benvenuto, {loggedUser}! 🎉</h1>
        <p>Frontend, backend e database comunicano correttamente.</p>
        <button onClick={() => setLoggedUser(null)}>Logout</button>
      </main>
    );
  }

  return (
    <main className="card">
      <h1>Login</h1>
      <form onSubmit={handleSubmit}>
        <label>
          Username
          <input
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            autoComplete="username"
            required
          />
        </label>
        <label>
          Password
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            autoComplete="current-password"
            required
          />
        </label>
        {error && <p className="error">{error}</p>}
        <button type="submit" disabled={loading}>
          {loading ? "Accesso..." : "Accedi"}
        </button>
      </form>
      <small>Utente di prova: admin / admin123</small>
    </main>
  );
}
