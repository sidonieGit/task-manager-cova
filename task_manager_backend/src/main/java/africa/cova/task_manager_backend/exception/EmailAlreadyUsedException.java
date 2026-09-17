package africa.cova.task_manager_backend.exception;

import org.springframework.http.HttpStatus;

public class EmailAlreadyUsedException extends BusinessException {

    public EmailAlreadyUsedException(String email) {
        super("Un compte existe déjà avec l'email : " + email, HttpStatus.CONFLICT);
    }
}
