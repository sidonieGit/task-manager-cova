import { createContext, useContext, useState, type ReactNode } from "react";
import type { AuthResponse } from "../types";

interface AuthContextValue {
  token: string | null;
  email: string | null;
  login: (auth: AuthResponse) => void;
  logout: () => void;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState<string | null>(() => localStorage.getItem("authToken"));
  const [email, setEmail] = useState<string | null>(() => localStorage.getItem("userEmail"));

  const login = (auth: AuthResponse) => {
    localStorage.setItem("authToken", auth.token);
    localStorage.setItem("userEmail", auth.email);
    setToken(auth.token);
    setEmail(auth.email);
  };

  const logout = () => {
    localStorage.removeItem("authToken");
    localStorage.removeItem("userEmail");
    setToken(null);
    setEmail(null);
  };

  return (
    <AuthContext.Provider value={{ token, email, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth(): AuthContextValue {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth doit être utilisé à l'intérieur d'un AuthProvider");
  }
  return context;
}
