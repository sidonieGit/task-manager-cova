package africa.cova.task_manager_backend.service.impl;

import africa.cova.task_manager_backend.dto.task.request.TaskRequest;
import africa.cova.task_manager_backend.dto.task.response.TaskResponse;
import africa.cova.task_manager_backend.exception.ResourceNotFoundException;
import africa.cova.task_manager_backend.exception.UnauthorizedTaskAccessException;
import africa.cova.task_manager_backend.model.Task;
import africa.cova.task_manager_backend.model.TaskStatus;
import africa.cova.task_manager_backend.model.User;
import africa.cova.task_manager_backend.repository.TaskRepository;
import africa.cova.task_manager_backend.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    private static final String OWNER_EMAIL = "owner@test.com";
    private static final String OTHER_USER_EMAIL = "intruder@test.com";
    private static final Long TASK_ID = 42L;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    @Test
    @DisplayName("getTasks() retourne les tâches filtrées par statut et recherche pour l'utilisateur donné")
    void getTasks_shouldReturnTasksFilteredByStatusAndSearch_forGivenUser() {
        // arrange
        User owner = User.builder().id(1L).email(OWNER_EMAIL).build();
        Task task1 = Task.builder().id(1L).title("Faire les courses").status(TaskStatus.TODO).owner(owner).build();
        Task task2 = Task.builder().id(2L).title("Faire le ménage").status(TaskStatus.TODO).owner(owner).build();

        when(userRepository.findByEmail(OWNER_EMAIL)).thenReturn(Optional.of(owner));
        when(taskRepository.findByOwnerWithFilters(owner, TaskStatus.TODO, "faire"))
                .thenReturn(List.of(task1, task2));

        // act
        List<TaskResponse> result = taskService.getTasks(OWNER_EMAIL, "TODO", "faire");

        // assert
        assertEquals(2, result.size());
        assertEquals(task1.getTitle(), result.get(0).title());
        assertEquals(task2.getTitle(), result.get(1).title());
        verify(taskRepository).findByOwnerWithFilters(owner, TaskStatus.TODO, "faire");
    }

    @Test
    @DisplayName("createTask() sauvegarde la tâche avec le bon owner")
    void createTask_shouldSaveTaskWithCorrectOwner() {
        // arrange
        User owner = User.builder().id(1L).email(OWNER_EMAIL).build();
        TaskRequest request = new TaskRequest("Nouvelle tâche", "Description", TaskStatus.TODO);

        when(userRepository.findByEmail(OWNER_EMAIL)).thenReturn(Optional.of(owner));

        // act
        taskService.createTask(request, OWNER_EMAIL);

        // assert
        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(taskCaptor.capture());
        Task savedTask = taskCaptor.getValue();

        assertEquals(owner, savedTask.getOwner());
        assertEquals(request.title(), savedTask.getTitle());
        assertEquals(request.description(), savedTask.getDescription());
    }

    @Test
    @DisplayName("updateTask() met à jour la tâche quand l'utilisateur connecté est le propriétaire")
    void updateTask_shouldUpdateTask_whenUserIsOwner() {
        // arrange
        User owner = User.builder().id(1L).email(OWNER_EMAIL).build();
        Task existingTask = Task.builder()
                .id(TASK_ID)
                .title("Ancien titre")
                .description("Ancienne description")
                .status(TaskStatus.TODO)
                .owner(owner)
                .build();
        TaskRequest request = new TaskRequest("Nouveau titre", "Nouvelle description", TaskStatus.IN_PROGRESS);

        when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(existingTask));

        // act
        TaskResponse response = taskService.updateTask(TASK_ID, request, OWNER_EMAIL);

        // assert
        assertEquals("Nouveau titre", response.title());
        assertEquals("Nouvelle description", response.description());
        assertEquals(TaskStatus.IN_PROGRESS, response.status());
        verify(taskRepository).save(existingTask);
    }

    @Test
    @DisplayName("updateTask() lève UnauthorizedTaskAccessException quand l'utilisateur connecté n'est pas le propriétaire")
    void updateTask_shouldThrowUnauthorizedTaskAccessException_whenUserIsNotOwner() {
        // arrange
        User owner = User.builder().id(1L).email(OWNER_EMAIL).build();
        Task existingTask = Task.builder()
                .id(TASK_ID)
                .title("Titre original")
                .description("Description originale")
                .status(TaskStatus.TODO)
                .owner(owner)
                .build();
        TaskRequest request = new TaskRequest("Titre modifié par un intrus", "Description modifiée", TaskStatus.DONE);

        when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(existingTask));

        // act & assert
        assertThrows(UnauthorizedTaskAccessException.class,
                () -> taskService.updateTask(TASK_ID, request, OTHER_USER_EMAIL));

        // la tâche ne doit ni être modifiée ni être persistée
        assertEquals("Titre original", existingTask.getTitle());
        assertEquals("Description originale", existingTask.getDescription());
        assertEquals(TaskStatus.TODO, existingTask.getStatus());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("updateTask() lève ResourceNotFoundException si la tâche n'existe pas")
    void updateTask_shouldThrowResourceNotFoundException_whenTaskDoesNotExist() {
        // arrange
        TaskRequest request = new TaskRequest("Titre", "Description", TaskStatus.TODO);
        when(taskRepository.findById(TASK_ID)).thenReturn(Optional.empty());

        // act & assert
        assertThrows(ResourceNotFoundException.class,
                () -> taskService.updateTask(TASK_ID, request, OWNER_EMAIL));

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("deleteTask() supprime la tâche quand l'utilisateur connecté est le propriétaire")
    void deleteTask_shouldDeleteTask_whenUserIsOwner() {
        // arrange
        User owner = User.builder().id(1L).email(OWNER_EMAIL).build();
        Task existingTask = Task.builder().id(TASK_ID).title("Tâche à supprimer").owner(owner).build();

        when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(existingTask));

        // act
        taskService.deleteTask(TASK_ID, OWNER_EMAIL);

        // assert
        verify(taskRepository).delete(existingTask);
    }

    @Test
    @DisplayName("deleteTask() lève UnauthorizedTaskAccessException quand l'utilisateur connecté n'est pas le propriétaire")
    void deleteTask_shouldThrowUnauthorizedTaskAccessException_whenUserIsNotOwner() {
        // arrange
        User owner = User.builder().id(1L).email(OWNER_EMAIL).build();
        Task existingTask = Task.builder().id(TASK_ID).title("Tâche protégée").owner(owner).build();

        when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(existingTask));

        // act & assert
        assertThrows(UnauthorizedTaskAccessException.class,
                () -> taskService.deleteTask(TASK_ID, OTHER_USER_EMAIL));

        verify(taskRepository, never()).delete(any(Task.class));
    }

    @Test
    @DisplayName("deleteTask() lève ResourceNotFoundException si la tâche n'existe pas")
    void deleteTask_shouldThrowResourceNotFoundException_whenTaskDoesNotExist() {
        // arrange
        when(taskRepository.findById(TASK_ID)).thenReturn(Optional.empty());

        // act & assert
        assertThrows(ResourceNotFoundException.class,
                () -> taskService.deleteTask(TASK_ID, OWNER_EMAIL));

        verify(taskRepository, never()).delete(any(Task.class));
    }
}
