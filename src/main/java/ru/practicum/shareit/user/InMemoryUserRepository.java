package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private long currentId = 1;

    @Override
    public Collection<User> getAllUser() {
        log.debug("Запрос всех пользователей");
        return users.values();
    }

    @Override
    public Optional<User> getById(Long id) {
        log.debug("Поиск пользователя по ID: {}", id);
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User createUser(User user) {
        log.debug("Создание нового пользователя");
        user.setId(currentId++);
        users.put(user.getId(), user);
        log.debug("Пользователь успешно сохранён с ID: {}", user.getId());
        return user;
    }

    @Override
    public User updateUser(User user) {
        log.debug("Обновление пользователя");
        users.put(user.getId(), user);
        log.debug("Пользователь с ID {} успешно обновлён", user.getId());
        return user;
    }

    @Override
    public void deleteUser(Long id) {
        log.debug("Удаление пользователя по ID: {}", id);
        users.remove(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        log.debug("Проверка существования email: {}", email);
        return users.values().stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }
}
