import { useEffect } from "react";

interface ToastProps {
  message: string;
  type: "error" | "success";
  onClose: () => void;
}

const DISPLAY_DURATION_MS = 4000;

export function Toast({ message, type, onClose }: ToastProps) {
  // Fermeture automatique après quelques secondes
  useEffect(() => {
    const timer = setTimeout(onClose, DISPLAY_DURATION_MS);
    return () => clearTimeout(timer);
  }, [onClose]);

  const colorClasses =
    type === "error"
      ? "bg-red-100 text-red-800 border-red-300 dark:bg-red-900/80 dark:text-red-100 dark:border-red-700"
      : "bg-green-100 text-green-800 border-green-300 dark:bg-green-900/80 dark:text-green-100 dark:border-green-700";

  return (
    <div
      className={`fixed top-4 right-4 z-50 max-w-sm rounded-lg border px-4 py-3 text-sm font-body shadow-sm ${colorClasses}`}
      role="alert"
    >
      <div className="flex items-start gap-3">
        <span className="flex-1">{message}</span>
        <button
          onClick={onClose}
          aria-label="Fermer"
          className="text-current opacity-70 hover:opacity-100"
        >
          ✕
        </button>
      </div>
    </div>
  );
}
