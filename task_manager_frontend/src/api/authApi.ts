import { axiosClient } from "./axiosClient";
import type { AuthResponse } from "../types";

export async function register(email: string, password: string): Promise<void> {
  await axiosClient.post("/api/auth/register", { email, password });
}

export async function login(email: string, password: string): Promise<AuthResponse> {
  const response = await axiosClient.post<AuthResponse>("/api/auth/login", { email, password });
  return response.data;
}
