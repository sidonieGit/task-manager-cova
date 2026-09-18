import type { TaskStatus } from "../types";

interface FilterBarProps {
  search: string;
  onSearchChange: (value: string) => void;
  status: TaskStatus | "";
  onStatusChange: (value: TaskStatus | "") => void;
}

export function FilterBar({ search, onSearchChange, status, onStatusChange }: FilterBarProps) {
  return (
    <div className="flex flex-wrap items-center gap-3 px-6 py-4 font-body">
      <input
        type="text"
        value={search}
        onChange={(e) => onSearchChange(e.target.value)}
        placeholder="Rechercher une tâche..."
        className="w-64 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 px-3 py-2 text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-cova-teal"
      />
      <select
        value={status}
        onChange={(e) => onStatusChange(e.target.value as TaskStatus | "")}
        className="rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 px-3 py-2 text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-cova-teal"
      >
        <option value="">Tous</option>
        <option value="TODO">À faire</option>
        <option value="IN_PROGRESS">En cours</option>
        <option value="DONE">Terminé</option>
      </select>
    </div>
  );
}
