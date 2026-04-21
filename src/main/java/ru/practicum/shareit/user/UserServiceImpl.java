package ru.practicum.shareit.user;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с ID: " + id));
    }

    @Override
    public Collection<UserDto> getAllUser() {
        log.info("Получение всех пользователей");

        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long id) {
        log.info("Поиск пользователя по ID: {}", id);

        User user = getUserOrThrow(id);
        log.info("Пользователь найден: {}", user.getName());
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        log.info("Создание нового пользователя с email: {}", userDto.getEmail());

        checkEmailUniqueness(userDto.getEmail());

        User user = UserMapper.toUser(userDto);
        User savedUser = userRepository.save(user);

        log.info("Пользователь успешно создан с ID: {}", savedUser.getId());
        return UserMapper.toUserDto(savedUser);
    }

    @Override
    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {
        log.info("Обновление пользователя с ID: {}", id);

        User existingUser = getUserOrThrow(id);

        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            existingUser.setName(userDto.getName());
        }

        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            if (!userDto.getEmail().equals(existingUser.getEmail())) {
                checkEmailUniqueness(userDto.getEmail());
            }
            existingUser.setEmail(userDto.getEmail());
        }

        userRepository.save(existingUser);
        log.info("Пользователь с ID {} успешно обновлён", id);

        return UserMapper.toUserDto(existingUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.info("Удаление пользователя с ID: {}", id);

        userRepository.deleteById(id);

        log.info("Пользователь с ID {} удалён", id);
    }

    private void checkEmailUniqueness(String email) {
        if (userRepository.existsByEmail(email)) {
            log.warn("Попытка создать/обновить пользователя с уже существующим email: {}", email);
            throw new ConflictException("Email уже существует: " + email);
        }
    }
}