package africa.cova.task_manager_backend.service.impl;

import africa.cova.task_manager_backend.dto.auth.request.LoginRequest;
import africa.cova.task_manager_backend.dto.auth.request.RegisterRequest;
import africa.cova.task_manager_backend.dto.auth.response.AuthResponse;
import africa.cova.task_manager_backend.exception.EmailAlreadyUsedException;
import africa.cova.task_manager_backend.model.User;
import africa.cova.task_manager_backend.repository.UserRepository;
import africa.cova.task_manager_backend.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    @DisplayName("register() sauvegarde un utilisateur avec un mot de passe encodé quand l'email est disponible")
    void register_shouldEncodePasswordAndReturnToken_whenEmailIsAvailable() {
        // arrange
        String plainInput = "plainPassword123";
        String hashedOutput= "encodedPassword123";
        RegisterRequest request = new RegisterRequest("new.user@test.com", plainInput);

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(plainInput)).thenReturn(hashedOutput);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("generated-token");

        // act
        AuthResponse response = authService.register(request);

        // assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals(request.email(), savedUser.getEmail());
        assertEquals(hashedOutput, savedUser.getPassword());
        assertNotEquals(plainInput, savedUser.getPassword());
        assertEquals("generated-token", response.token());
        assertEquals(request.email(), response.email());
        verify(passwordEncoder).encode(plainInput);
    }

    @Test
    @DisplayName("register() lève EmailAlreadyUsedException quand l'email existe déjà")
    void register_shouldThrowEmailAlreadyUsedException_whenEmailAlreadyExists() {
        // arrange
        RegisterRequest request = new RegisterRequest("existing.user@test.com", "plainPassword123");
        User existingUser = User.builder().email(request.email()).password("someEncodedPassword").build();

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(existingUser));

        // act & assert
        assertThrows(EmailAlreadyUsedException.class, () -> authService.register(request));

        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("login() retourne un AuthResponse avec le token généré quand les identifiants sont valides")
    void login_shouldReturnAuthResponseWithGeneratedToken_whenCredentialsAreValid() {
        // arrange
        LoginRequest request = new LoginRequest("valid.user@test.com", "plainPassword123");
        User user = User.builder().email(request.email()).password("encodedPassword123").build();

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(request.email())
                .password(user.getPassword())
                .authorities(Collections.emptyList())
                .build();

        Authentication authentication = org.mockito.Mockito.mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(userDetails)).thenReturn("valid-token");

        // act
        AuthResponse response = authService.login(request);

        // assert
        assertEquals("valid-token", response.token());
        assertEquals(request.email(), response.email());
    }

    @Test
    @DisplayName("login() lève une exception d'authentification quand les identifiants sont invalides")
    void login_shouldThrowAuthenticationException_whenCredentialsAreInvalid() {
        // arrange
        LoginRequest request = new LoginRequest("unknown.user@test.com", "wrongPassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Identifiants invalides"));

        // act & assert
        assertThrows(BadCredentialsException.class, () -> authService.login(request));

        verify(jwtService, never()).generateToken(any());
        verify(userRepository, never()).findByEmail(anyString());
    }
}
