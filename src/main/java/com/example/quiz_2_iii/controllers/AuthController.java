package com.example.quiz_2_iii.controllers;

import com.example.quiz_2_iii.dtos.AuthResponse;
import com.example.quiz_2_iii.dtos.LoginRequest;
import com.example.quiz_2_iii.dtos.RegisterRequest;
import com.example.quiz_2_iii.models.User;
import com.example.quiz_2_iii.repositories.UserRepository;
import com.example.quiz_2_iii.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final ReactiveAuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/register")
    public Mono<ResponseEntity<Object>> register(@RequestBody RegisterRequest request) {
        if (request.getUsername() == null || request.getPassword() == null) {
            return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "El nombre de usuario y la contraseña son obligatorios")));
        }

        return userRepository.findByUsername(request.getUsername())
                .map(existingUser -> ResponseEntity.status(HttpStatus.CONFLICT)
                        .body((Object) Map.of("message", "El nombre de usuario ya está en uso")))
                .switchIfEmpty(Mono.defer(() -> {
                    String role = request.getRole() == null ? "USER" : request.getRole().toUpperCase();
                    if (!role.equals("ADMIN") && !role.equals("USER")) {
                        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body((Object) Map.of("message", "El rol especificado no es válido. Los roles válidos son 'ADMIN' y 'USER'.")));
                    }

                    User user = User.builder()
                            .username(request.getUsername())
                            .password(passwordEncoder.encode(request.getPassword()))
                            .role(role)
                            .build();

                    return userRepository.save(user)
                            .map(savedUser -> {
                                String token = jwtService.generateToken(savedUser);
                                return ResponseEntity.ok((Object) AuthResponse.builder().token(token).build());
                            });
                }));
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<Object>> login(@RequestBody LoginRequest request) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        ).flatMap(auth -> userRepository.findByUsername(request.getUsername())
                .map(user -> {
                    String token = jwtService.generateToken(user);
                    return ResponseEntity.ok((Object) AuthResponse.builder().token(token).build());
                }))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body((Object) Map.of("message", "Credenciales inválidas"))));
    }
}
