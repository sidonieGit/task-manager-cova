import type { Task, TaskStatus } from "../types";

interface TaskItemProps {
  task: Task;
  onEdit: (task: Task) => void;
  onDelete: (task: Task) => void;
}

const STATUS_LABELS: Record<TaskStatus, string> = {
  TODO: "À faire",
  IN_PROGRESS: "En cours",
  DONE: "Terminé",
};

const STATUS_BADGE_CLASSES: Record<TaskStatus, string> = {
  TODO: "bg-orange-100 text-orange-700 dark:bg-orange-900/60 dark:text-orange-200",
  IN_PROGRESS: "bg-teal-100 text-teal-700 dark:bg-teal-900/60 dark:text-teal-200",
  DONE: "bg-green-100 text-green-700 dark:bg-green-900/60 dark:text-green-200",
};

export function TaskItem({ task, onEdit, onDelete }: TaskItemProps) {
  return (
    <div className="rounded-xl border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800 p-5 flex flex-col gap-3 font-body">
      <div className="flex items-start justify-between gap-2">
        <h3 className="font-heading text-base font-semibold text-gray-900 dark:text-gray-100 break-words">
          {task.title}
        </h3>
        <div className="flex items-center gap-2 shrink-0">
          <button
            onClick={() => onEdit(task)}
            aria-label="Modifier la tâche"
            className="text-gray-500 hover:text-cova-teal dark:text-gray-400 dark:hover:text-cova-teal"
          >
            ✎
          </button>
          <button
            onClick={() => onDelete(task)}
            aria-label="Supprimer la tâche"
            className="text-gray-500 hover:text-red-600 dark:text-gray-400 dark:hover:text-red-400"
          >
            🗑
          </button>
        </div>
      </div>

      {task.description && (
        <p className="text-sm text-gray-600 dark:text-gray-300 break-words">{task.description}</p>
      )}

      <span
        className={`self-start rounded-lg px-2.5 py-1 text-xs font-medium ${STATUS_BADGE_CLASSES[task.status]}`}
      >
        {STATUS_LABELS[task.status]}
      </span>
    </div>
  );
}
