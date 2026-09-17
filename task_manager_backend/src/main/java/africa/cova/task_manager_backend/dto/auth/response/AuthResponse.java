package africa.cova.task_manager_backend.dto.auth.response;

public record AuthResponse(
        String token,
        String email
) {
}
