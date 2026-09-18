import { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import { useTheme } from "../context/ThemeContext";
import { getTasks, createTask, updateTask, deleteTask, type TaskInput } from "../api/tasksApi";
import type { Task, TaskStatus } from "../types";
import { extractErrorMessage } from "../utils/errorHandler";
import { FilterBar } from "../components/FilterBar";
import { TaskList } from "../components/TaskList";
import { TaskForm } from "../components/TaskForm";
import { Toast } from "../components/Toast";
import { Footer } from "../components/Footer";

export function DashboardPage() {
  const { email, logout } = useAuth();
  const { theme, toggleTheme } = useTheme();

  const [tasks, setTasks] = useState<Task[]>([]);
  const [search, setSearch] = useState("");
  const [status, setStatus] = useState<TaskStatus | "">("");
  const [toast, setToast] = useState<{ message: string; type: "error" | "success" } | null>(null);
  const [formOpen, setFormOpen] = useState(false);
  const [editingTask, setEditingTask] = useState<Task | undefined>(undefined);

  // Recharge la liste des tâches à chaque changement de filtre
  useEffect(() => {
    let cancelled = false;

    async function loadTasks() {
      try {
        const data = await getTasks(status || undefined, search || undefined);
        if (!cancelled) {
          setTasks(data);
        }
      } catch (err) {
        if (!cancelled) {
          setToast({ message: extractErrorMessage(err), type: "error" });
        }
      }
    }

    loadTasks();
    return () => {
      cancelled = true;
    };
  }, [status, search]);

  const refreshTasks = async () => {
    try {
      const data = await getTasks(status || undefined, search || undefined);
      setTasks(data);
    } catch (err) {
      setToast({ message: extractErrorMessage(err), type: "error" });
    }
  };

  const handleCreate = () => {
    setEditingTask(undefined);
    setFormOpen(true);
  };

  const handleEdit = (task: Task) => {
    setEditingTask(task);
    setFormOpen(true);
  };

  const handleDelete = async (task: Task) => {
    try {
      await deleteTask(task.id);
      setToast({ message: "Tâche supprimée", type: "success" });
      await refreshTasks();
    } catch (err) {
      setToast({ message: extractErrorMessage(err), type: "error" });
    }
  };

  const handleFormSubmit = async (data: TaskInput) => {
    try {
      if (editingTask) {
        await updateTask(editingTask.id, data);
        setToast({ message: "Tâche modifiée", type: "success" });
      } else {
        await createTask(data);
        setToast({ message: "Tâche créée", type: "success" });
      }
      setFormOpen(false);
      setEditingTask(undefined);
      await refreshTasks();
    } catch (err) {
      setToast({ message: extractErrorMessage(err), type: "error" });
    }
  };

  return (
    <div className="min-h-screen flex flex-col bg-gray-50 dark:bg-gray-900">
      <header className="bg-cova-teal text-white px-6 py-4 flex items-center justify-between">
        <h1 className="font-heading text-lg font-semibold">Task Manager</h1>
        <div className="flex items-center gap-4 font-body text-sm">
          <span className="hidden sm:inline">{email}</span>
          <button
            onClick={toggleTheme}
            aria-label="Changer de thème"
            className="rounded-lg px-2 py-1 hover:bg-white/10"
          >
            {theme === "dark" ? "☀" : "🌙"}
          </button>
          <button onClick={logout} className="rounded-lg px-3 py-1.5 hover:bg-white/10">
            Déconnexion
          </button>
        </div>
      </header>

      <div className="flex items-center justify-between px-6 pt-4">
        <FilterBar
          search={search}
          onSearchChange={setSearch}
          status={status}
          onStatusChange={setStatus}
        />
        <button
          onClick={handleCreate}
          className="rounded-lg bg-cova-orange px-4 py-2 text-sm font-medium text-white hover:bg-cova-orange-dark font-body"
        >
          Nouvelle tâche
        </button>
      </div>

      <main className="flex-1">
        <TaskList tasks={tasks} onEdit={handleEdit} onDelete={handleDelete} />
      </main>

      <Footer />

      {formOpen && (
        <TaskForm
          initialData={editingTask}
          onSubmit={handleFormSubmit}
          onCancel={() => {
            setFormOpen(false);
            setEditingTask(undefined);
          }}
        />
      )}

      {toast && <Toast message={toast.message} type={toast.type} onClose={() => setToast(null)} />}
    </div>
  );
}
