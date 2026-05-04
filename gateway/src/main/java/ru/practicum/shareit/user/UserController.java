package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserDto;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Запрос на получение всех пользователей");
        return userClient.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable Long id) {
        log.info("Запрос на получение пользователя c ID {}", id);
        return userClient.getUserById(id);
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto user) {
        log.info("Запрос на создание пользователя");
        return userClient.createUser(user);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable Long id, @RequestBody UserDto user) {
        log.info("Запрос на обновление пользователя с ID {}", id);
        return userClient.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
        log.info("Запрос на удаление пользователя с ID {}", id);
        return userClient.deleteUser(id);
    }
}
