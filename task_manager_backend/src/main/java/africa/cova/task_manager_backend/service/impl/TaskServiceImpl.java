package africa.cova.task_manager_backend.service.impl;

import africa.cova.task_manager_backend.dto.task.request.TaskRequest;
import africa.cova.task_manager_backend.dto.task.response.TaskResponse;
import africa.cova.task_manager_backend.exception.ResourceNotFoundException;
import africa.cova.task_manager_backend.exception.UnauthorizedTaskAccessException;
import africa.cova.task_manager_backend.mapper.TaskMapper;
import africa.cova.task_manager_backend.model.Task;
import africa.cova.task_manager_backend.model.TaskStatus;
import africa.cova.task_manager_backend.model.User;
import africa.cova.task_manager_backend.repository.TaskRepository;
import africa.cova.task_manager_backend.repository.UserRepository;
import africa.cova.task_manager_backend.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasks(String userEmail, String status, String search) {
        User owner = getOwnerByEmail(userEmail);
        TaskStatus statusFilter = (status != null && !status.isBlank()) ? TaskStatus.valueOf(status.toUpperCase()) : null;
        String searchFilter = (search != null && !search.isBlank()) ? search : null;

        return taskRepository.findByOwnerWithFilters(owner, statusFilter, searchFilter).stream()
                .map(TaskMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public TaskResponse createTask(TaskRequest request, String userEmail) {
        User owner = getOwnerByEmail(userEmail);
        Task task = TaskMapper.toEntity(request, owner);
        taskRepository.save(task);
        return TaskMapper.toResponse(task);
    }

    @Override
    @Transactional
    public TaskResponse updateTask(Long taskId, TaskRequest request, String userEmail) {
        Task task = getOwnedTask(taskId, userEmail);

        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStatus(request.status() != null ? request.status() : task.getStatus());
        taskRepository.save(task);

        return TaskMapper.toResponse(task);
    }

    @Override
    @Transactional
    public void deleteTask(Long taskId, String userEmail) {
        Task task = getOwnedTask(taskId, userEmail);
        taskRepository.delete(task);
    }

    private Task getOwnedTask(Long taskId, String userEmail) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Tâche introuvable avec l'id : " + taskId));

        if (!task.getOwner().getEmail().equals(userEmail)) {
            throw new UnauthorizedTaskAccessException();
        }

        return task;
    }

    private User getOwnerByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable avec l'email : " + email));
    }
}
