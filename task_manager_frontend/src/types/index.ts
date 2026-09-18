export type TaskStatus = "TODO" | "IN_PROGRESS" | "DONE";

export interface Task {
  id: number;
  title: string;
  description: string | null;
  status: TaskStatus;
  createdAt: string;
  updatedAt: string;
}

export interface AuthResponse {
  token: string;
  email: string;
}

export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
}
