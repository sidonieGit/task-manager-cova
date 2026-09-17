package africa.cova.task_manager_backend.dto.task.request;

import africa.cova.task_manager_backend.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;

public record TaskRequest(
        @NotBlank(message = "Le titre est obligatoire")
        String title,

        String description,

        TaskStatus status
) {
}
