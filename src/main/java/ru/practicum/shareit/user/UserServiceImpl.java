package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Collection<UserDto> getAllUser() {
        log.info("Получение всех пользователей");

        return userRepository.getAllUser()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long id) {
        log.info("Поиск пользователя по ID: {}", id);

        User user = userRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с ID: " + id));

        log.info("Пользователь найден: {}", user.getName());
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        log.info("Создание нового пользователя с email: {}", userDto.getEmail());

        validateName(userDto.getName());
        validateEmail(userDto.getEmail());

        User user = UserMapper.toUser(userDto);
        User savedUser = userRepository.createUser(user);

        log.info("Пользователь успешно создан с ID: {}", savedUser.getId());
        return UserMapper.toUserDto(savedUser);
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        log.info("Обновление пользователя с ID: {}", id);

        User existingUser = userRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден c ID: " + id));

        if (userDto.getName() != null) {
            validateName(userDto.getName());
            existingUser.setName(userDto.getName());
        }

        if (userDto.getEmail() != null) {
            validateEmail(userDto.getEmail());
            existingUser.setEmail(userDto.getEmail());
        }

        userRepository.updateUser(existingUser);
        log.info("Пользователь с ID {} успешно обновлён", id);

        return UserMapper.toUserDto(existingUser);
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Удаление пользователя с ID: {}", id);

        userRepository.deleteUser(id);

        log.info("Пользователь с ID {} удалён", id);
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            log.warn("Попытка создать/обновить пользователя с пустым именем");
            throw new ValidationException("Имя не может быть пустым");
        }
    }

    private void validateEmail(String email) {

        if (email == null || email.isBlank()) {
            log.warn("Попытка создать/обновить пользователя с пустым email");
            throw new ValidationException("Email не должен быть пустым");
        }
        if (!email.contains("@")) {
            log.warn("Попытка создать/обновить пользователя с некорректным email: {}", email);
            throw new ValidationException("Email должен содержать @");
        }

        if (userRepository.existsByEmail(email)) {
            log.warn("Попытка создать/обновить пользователя с уже существующим email: {}", email);
            throw new ConflictException("Email уже существует: " + email);
        }
    }
}