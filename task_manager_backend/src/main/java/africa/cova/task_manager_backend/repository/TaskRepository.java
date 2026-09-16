package africa.cova.task_manager_backend.repository;

import africa.cova.task_manager_backend.model.Task;
import africa.cova.task_manager_backend.model.TaskStatus;
import africa.cova.task_manager_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // Filtres optionnels combinés : status et search sont ignorés quand ils valent null
    @Query("""
            SELECT t FROM Task t
            WHERE t.owner = :owner
            AND (:status IS NULL OR t.status = :status)
            AND (:search IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    List<Task> findByOwnerWithFilters(@Param("owner") User owner,
                                       @Param("status") TaskStatus status,
                                       @Param("search") String search);
}
