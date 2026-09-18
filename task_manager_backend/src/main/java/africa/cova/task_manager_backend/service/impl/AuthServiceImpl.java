package africa.cova.task_manager_backend.service.impl;

import africa.cova.task_manager_backend.dto.auth.request.LoginRequest;
import africa.cova.task_manager_backend.dto.auth.request.RegisterRequest;
import africa.cova.task_manager_backend.dto.auth.response.AuthResponse;
import africa.cova.task_manager_backend.exception.EmailAlreadyUsedException;
import africa.cova.task_manager_backend.mapper.UserMapper;
import africa.cova.task_manager_backend.model.User;
import africa.cova.task_manager_backend.repository.UserRepository;
import africa.cova.task_manager_backend.security.JwtService;
import africa.cova.task_manager_backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        userRepository.findByEmail(request.email()).ifPresent(existing -> {
            throw new EmailAlreadyUsedException(request.email());
        });

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();
        userRepository.save(user);

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(java.util.Collections.emptyList())
                .build();
        String token = jwtService.generateToken(userDetails);

        return UserMapper.toAuthResponse(user, token);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalStateException("Utilisateur authentifié introuvable en base"));

        String token = jwtService.generateToken(userDetails);
        return UserMapper.toAuthResponse(user, token);
    }
}
