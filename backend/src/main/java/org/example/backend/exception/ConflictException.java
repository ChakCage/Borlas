package org.example.backend.exception;

import org.springframework.http.HttpStatus;

/**
 * Бизнес-исключение. По умолчанию 409 CONFLICT,
 * но можно указать любой HttpStatus.
 */
public class ConflictException extends RuntimeException {

    private final HttpStatus status;

    public ConflictException(String message) {
        super(message);
        this.status = HttpStatus.CONFLICT;
    }

    public ConflictException(HttpStatus status, String message) {
        super(message);
        this.status = status == null ? HttpStatus.CONFLICT : status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
