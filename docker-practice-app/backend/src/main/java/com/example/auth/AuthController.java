package com.example.auth;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AppUserRepository repository;
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthController(AppUserRepository repository) {
        this.repository = repository;
    }

    public record LoginRequest(String username, String password) {
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        if (request.username() == null || request.password() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Username e password sono obbligatori"));
        }

        return repository.findByUsername(request.username())
                .filter(user -> encoder.matches(request.password(), user.getPasswordHash()))
                .<ResponseEntity<Map<String, String>>>map(user -> ResponseEntity.ok(
                        Map.of("message", "Login effettuato", "username", user.getUsername())))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Credenziali non valide")));
    }
}
