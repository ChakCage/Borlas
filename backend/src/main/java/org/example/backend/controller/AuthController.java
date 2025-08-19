package org.example.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend.dto.AuthRequest;
import org.example.backend.dto.AuthResponse;
import org.example.backend.dto.UnuversalOkResponce;
import org.example.backend.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final UserDetailsService users;
    private final JwtService jwt;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest req) {
        // проверяем логин/пароль
        authManager.authenticate(new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));

        UserDetails user = users.loadUserByUsername(req.getUsername());
        String access  = jwt.generateAccessToken(user);
        String refresh = jwt.generateRefreshToken(user);

        var dto = new AuthResponse(access, refresh, "Bearer");
        var body = new UnuversalOkResponce(
                Map.of("token", dto), "Успешный вход", "200 OK", null
        ).getResponse();
        return ResponseEntity.ok(body);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> payload) {
        String refresh = payload.get("refreshToken");
        String username = jwt.extractUsername(refresh);
        UserDetails user = users.loadUserByUsername(username);
        // простая проверка: валиден ли refresh
        if (!jwt.isValid(refresh, user)) {
            var bad = new UnuversalOkResponce(null, "Неверный или истекший refresh токен",
                    "401 Unauthorized", "Unauthorized").getResponse();
            return ResponseEntity.status(401).body(bad);
        }
        String access = jwt.generateAccessToken(user);
        var body = new UnuversalOkResponce(Map.of("token",
                new AuthResponse(access, refresh, "Bearer")), "Токен обновлён", "200 OK", null).getResponse();
        return ResponseEntity.ok(body);
    }
}
