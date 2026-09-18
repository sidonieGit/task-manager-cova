import { axiosClient } from "./axiosClient";
import type { Task, TaskStatus } from "../types";

export interface TaskInput {
  title: string;
  description: string | null;
  status: TaskStatus;
}

export async function getTasks(status?: TaskStatus, search?: string): Promise<Task[]> {
  const params: Record<string, string> = {};
  if (status) params.status = status;
  if (search) params.search = search;
  const response = await axiosClient.get<Task[]>("/api/tasks", { params });
  return response.data;
}

export async function createTask(data: TaskInput): Promise<Task> {
  const response = await axiosClient.post<Task>("/api/tasks", data);
  return response.data;
}

export async function updateTask(id: number, data: TaskInput): Promise<Task> {
  const response = await axiosClient.put<Task>(`/api/tasks/${id}`, data);
  return response.data;
}

export async function deleteTask(id: number): Promise<void> {
  await axiosClient.delete(`/api/tasks/${id}`);
}
