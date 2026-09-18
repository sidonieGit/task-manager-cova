import axios, { AxiosError } from "axios";

const baseURL = import.meta.env.VITE_API_URL ?? "http://localhost:8080";

export const axiosClient = axios.create({ baseURL });

// Ajoute le token d'authentification à chaque requête si présent
axiosClient.interceptors.request.use((config) => {
  const token = localStorage.getItem("authToken");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// En cas de 401, la session n'est plus valide : on nettoie et on renvoie vers le login
axiosClient.interceptors.response.use(
  (response) => response,
  (error: AxiosError) => {
    const isAuthRequest =
      error.config?.url?.includes("/api/auth/login") ||
      error.config?.url?.includes("/api/auth/register");

    if (error.response?.status === 401 && !isAuthRequest) {
      localStorage.removeItem("authToken");
      window.location.href = "/login";
    }
    return Promise.reject(error);
  },
);
