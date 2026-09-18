package africa.cova.task_manager_backend.service;

import africa.cova.task_manager_backend.dto.auth.request.LoginRequest;
import africa.cova.task_manager_backend.dto.auth.request.RegisterRequest;
import africa.cova.task_manager_backend.dto.auth.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
