package org.example.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend.dto.UnuversalOkResponce;
import org.example.backend.dto.UserRequestDto;
import org.example.backend.dto.UserResponseDto;
import org.example.backend.exception.ConflictException;
import org.example.backend.mapper.UserMapper;
import org.example.backend.model.User;
import org.example.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Контроллер для управления пользователями.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository repo;

    /**
     * Создать нового пользователя.
     *
     * @param dto данные пользователя
     * @return созданный пользователь
     */
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody UserRequestDto dto) {
        if (repo.existsByEmail(dto.getEmail()))
            throw new ConflictException("Этот email уже зарегистрирован");
        if (repo.existsByUsername(dto.getUsername()))
            throw new ConflictException("Этот username уже занят");

        User saved = repo.save(UserMapper.toEntity(dto));
        var ok = new UnuversalOkResponce(
                UserMapper.toDto(saved),
                "Пользователь создан",
                HttpStatus.CREATED.value() + " " + HttpStatus.CREATED.getReasonPhrase()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ok.getResponse());
    }

    /**
     * Получить список всех пользователей.
     *
     * @return список пользователей
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll() {
        List<UserResponseDto> list = repo.findAll()
                .stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());

        var ok = new UnuversalOkResponce(list, "Список пользователей получен", "200 OK");
        return ResponseEntity.ok(ok.getResponse());
    }

    /**
     * Получить пользователя по id.
     *
     * @param id идентификатор пользователя
     * @return пользователь
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable UUID id) {
        var user = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("User with id %s not found", id)));

        var ok = new UnuversalOkResponce(UserMapper.toDto(user), "Пользователь получен", "200 OK");
        return ResponseEntity.ok(ok.getResponse());
    }

    /**
     * Обновить пользователя по id.
     *
     * @param id идентификатор пользователя
     * @param userRequestDto новые данные пользователя
     * @return обновлённый пользователь
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable UUID id,
                                                      @Valid @RequestBody UserRequestDto userRequestDto) {
        return repo.findById(id)
                .map(user -> {
                    UserMapper.update(user, userRequestDto);
                    var saved = repo.save(user);
                    var ok = new UnuversalOkResponce(UserMapper.toDto(saved), "Пользователь обновлён", "200 OK");
                    return ResponseEntity.ok(ok.getResponse());
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("User with id %s not found", id)));
    }

    /**
     * Частичное обновление текущего пользователя.
     *
     * @param userDetails текущий пользователь
     * @param userRequestDto новые данные
     * @return обновлённый пользователь
     */
    @PatchMapping("/me")
    public ResponseEntity<Map<String, Object>> patchMe(@AuthenticationPrincipal UserDetails userDetails,
                                                       @RequestBody UserRequestDto userRequestDto) {
        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        User user = repo.findByUsername(userDetails.getUsername()).orElseThrow();
        UserMapper.patch(user, userRequestDto);
        var saved = repo.save(user);

        var ok = new UnuversalOkResponce(UserMapper.toDto(saved), "Профиль обновлён", "200 OK");
        return ResponseEntity.ok(ok.getResponse());
    }

    /**
     * Удалить пользователя по id.
     *
     * @param id идентификатор пользователя
     * @return сообщение об удалении
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable UUID id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("User with id %s not found", id));
        }
        repo.deleteById(id);
        var ok = new UnuversalOkResponce(null, "Пользователь удалён", "200 OK");
        return ResponseEntity.ok(ok.getResponse());
    }
}
