package ru.practicum.shareit.user;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {
    Collection<User> getAllUser();

    Optional<User> getById(Long id);

    User createUser(User user);

    User updateUser(User user);

    void deleteUser(Long id);

    boolean existsByEmail(String email);
}
