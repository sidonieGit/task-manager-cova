package africa.cova.task_manager_backend.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedTaskAccessException extends BusinessException {

    public UnauthorizedTaskAccessException() {
        super("Vous n'êtes pas autorisé à accéder à cette tâche", HttpStatus.FORBIDDEN);
    }
}
