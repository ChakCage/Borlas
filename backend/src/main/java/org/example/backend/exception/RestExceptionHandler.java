package org.example.backend.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.dto.UnuversalOkResponce;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class RestExceptionHandler {

    private static String s(HttpStatus st) {
        return st.value() + " " + st.getReasonPhrase();
    }

    // 400: ошибки валидации (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + ": " + (fe.getDefaultMessage() == null ? "invalid" : fe.getDefaultMessage()))
                .collect(Collectors.toList());

        String message = "Ошибка валидации: " + String.join("; ", errors);
        var body = new UnuversalOkResponce(null, message, s(HttpStatus.BAD_REQUEST), "Validation failed")
                .getResponse();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // Наше бизнес-исключение
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<?> handleConflict(ConflictException ex) {
        HttpStatus st = ex.getStatus();
        var body = new UnuversalOkResponce(null, ex.getMessage(), s(st), st.getReasonPhrase()).getResponse();
        return ResponseEntity.status(st).body(body);
    }

    // Прямой ResponseStatusException -> в наш формат
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleResponseStatus(ResponseStatusException ex, HttpServletRequest req) {
        HttpStatus st = HttpStatus.resolve(ex.getStatusCode().value());
        if (st == null) st = HttpStatus.INTERNAL_SERVER_ERROR;
        String msg = ex.getReason() == null ? st.getReasonPhrase() : ex.getReason();
        var body = new UnuversalOkResponce(null, msg, s(st), st.getReasonPhrase()).getResponse();
        return ResponseEntity.status(st).body(body);
    }

    // Частые кейсы (404, 400, 401, 403, 409)
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleNoSuch(NoSuchElementException ex) {
        var st = HttpStatus.NOT_FOUND;
        var body = new UnuversalOkResponce(null, "Ресурс не найден", s(st), st.getReasonPhrase()).getResponse();
        return ResponseEntity.status(st).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArg(IllegalArgumentException ex) {
        var st = HttpStatus.BAD_REQUEST;
        var body = new UnuversalOkResponce(null, ex.getMessage(), s(st), st.getReasonPhrase()).getResponse();
        return ResponseEntity.status(st).body(body);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuth(AuthenticationException ex) {
        var st = HttpStatus.UNAUTHORIZED;
        var body = new UnuversalOkResponce(null, "Пользователь не авторизован", s(st), st.getReasonPhrase()).getResponse();
        return ResponseEntity.status(st).body(body);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleDenied(AccessDeniedException ex) {
        var st = HttpStatus.FORBIDDEN;
        var body = new UnuversalOkResponce(null, "Доступ запрещён", s(st), st.getReasonPhrase()).getResponse();
        return ResponseEntity.status(st).body(body);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleIntegrity(DataIntegrityViolationException ex) {
        var st = HttpStatus.CONFLICT;
        var body = new UnuversalOkResponce(null, "Нарушение целостности данных", s(st), st.getReasonPhrase()).getResponse();
        return ResponseEntity.status(st).body(body);
    }

    // 500 — последнее правило
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAny(Exception ex) {
        var st = HttpStatus.INTERNAL_SERVER_ERROR;
        var body = new UnuversalOkResponce(null, "Внутренняя ошибка сервера", s(st), st.getReasonPhrase()).getResponse();
        return ResponseEntity.status(st).body(body);
    }
}
