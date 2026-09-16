package africa.cova.task_manager_backend.mapper;

import africa.cova.task_manager_backend.dto.task.request.TaskRequest;
import africa.cova.task_manager_backend.dto.task.response.TaskResponse;
import africa.cova.task_manager_backend.model.Task;
import africa.cova.task_manager_backend.model.TaskStatus;
import africa.cova.task_manager_backend.model.User;

public final class TaskMapper {

    private TaskMapper() {
    }

    public static TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }

    public static Task toEntity(TaskRequest request, User owner) {
        return Task.builder()
                .title(request.title())
                .description(request.description())
                .status(request.status() != null ? request.status() : TaskStatus.TODO)
                .owner(owner)
                .build();
    }
}
