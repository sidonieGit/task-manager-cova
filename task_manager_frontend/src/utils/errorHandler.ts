import { AxiosError } from "axios";
import type { ApiError } from "../types";

// Extrait un message utilisateur lisible depuis une erreur d'appel API
export function extractErrorMessage(error: unknown): string {
  if (error instanceof AxiosError) {
    const data = error.response?.data as ApiError | undefined;
    if (data?.message) {
      return data.message;
    }
  }
  if (error instanceof Error) {
    return error.message;
  }
  return "Une erreur inattendue s'est produite";
}
