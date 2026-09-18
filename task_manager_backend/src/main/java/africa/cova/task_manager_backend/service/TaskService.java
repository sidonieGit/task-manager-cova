package africa.cova.task_manager_backend.service;

import africa.cova.task_manager_backend.dto.task.request.TaskRequest;
import africa.cova.task_manager_backend.dto.task.response.TaskResponse;

import java.util.List;

public interface TaskService {

    List<TaskResponse> getTasks(String userEmail, String status, String search);

    TaskResponse createTask(TaskRequest request, String userEmail);

    TaskResponse updateTask(Long taskId, TaskRequest request, String userEmail);

    void deleteTask(Long taskId, String userEmail);
}
