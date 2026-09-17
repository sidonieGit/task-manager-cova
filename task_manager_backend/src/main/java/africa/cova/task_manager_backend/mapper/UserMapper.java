package africa.cova.task_manager_backend.mapper;

import africa.cova.task_manager_backend.dto.auth.response.AuthResponse;
import africa.cova.task_manager_backend.model.User;

public final class UserMapper {

    private UserMapper() {
    }

    public static AuthResponse toAuthResponse(User user, String token) {
        return new AuthResponse(token, user.getEmail());
    }
}
