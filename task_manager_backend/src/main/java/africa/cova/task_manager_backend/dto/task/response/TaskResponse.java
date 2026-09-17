package africa.cova.task_manager_backend.dto.task.response;

import africa.cova.task_manager_backend.model.TaskStatus;

import java.time.LocalDateTime;

public record TaskResponse(
        Long id,
        String title,
        String description,
        TaskStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
