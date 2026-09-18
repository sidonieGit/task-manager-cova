import { useState, type FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";
import { login } from "../api/authApi";
import { useAuth } from "../context/AuthContext";
import { extractErrorMessage } from "../utils/errorHandler";
import { Toast } from "../components/Toast";
import { Footer } from "../components/Footer";

export function LoginPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const auth = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    try {
      const response = await login(email, password);
      auth.login(response);
      navigate("/");
    } catch (err) {
      setError(extractErrorMessage(err));
    }
  };

  return (
    <div className="min-h-screen flex flex-col bg-gray-50 dark:bg-gray-900">
      <div className="flex-1 flex items-center justify-center px-4">
        <form
          onSubmit={handleSubmit}
          className="w-full max-w-sm rounded-xl bg-white dark:bg-gray-800 p-8 flex flex-col gap-4 font-body"
        >
          <h1 className="font-heading text-xl font-semibold text-gray-900 dark:text-gray-100 text-center">
            Connexion
          </h1>

          <div className="flex flex-col gap-1">
            <label
              htmlFor="email"
              className="text-sm text-gray-700 dark:text-gray-300"
            >
              Email
            </label>
            <input
              id="email"
              type="email"
              required
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-900 px-3 py-2 text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-cova-teal"
            />
          </div>

          <div className="flex flex-col gap-1">
            <label
              htmlFor="password"
              className="text-sm text-gray-700 dark:text-gray-300"
            >
              Mot de passe
            </label>
            <input
              id="password"
              type="password"
              required
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-900 px-3 py-2 text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-cova-teal"
            />
          </div>

          <button
            type="submit"
            className="mt-2 rounded-lg bg-cova-orange px-4 py-2 text-sm font-medium text-white hover:bg-cova-orange-dark"
          >
            Se connecter
          </button>

          <p className="text-center text-sm text-gray-600 dark:text-gray-300">
            Pas encore de compte ?{" "}
            <Link to="/register" className="text-cova-orange hover:underline">
              S'inscrire
            </Link>
          </p>
        </form>
      </div>
      <Footer />
      {error && (
        <Toast message={error} type="error" onClose={() => setError(null)} />
      )}
    </div>
  );
}
