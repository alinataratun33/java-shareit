package ru.practicum.shareit.integration;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceIntegrationTest {

    private final UserService userService;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setName("Тестовый Пользователь");
        userDto.setEmail("test@example.com");
    }

    @Test
    void testSaveUserToDatabase() {
        UserDto savedUser = userService.createUser(userDto);

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getName()).isEqualTo(userDto.getName());
        assertThat(savedUser.getEmail()).isEqualTo(userDto.getEmail());
    }

    @Test
    void testGetUserById() {
        UserDto savedUser = userService.createUser(userDto);
        UserDto foundUser = userService.getUserById(savedUser.getId());

        assertThat(foundUser.getId()).isEqualTo(savedUser.getId());
        assertThat(foundUser.getEmail()).isEqualTo(savedUser.getEmail());
    }

    @Test
    void testGetAllUser() {
        userService.createUser(userDto);

        UserDto secondUser = new UserDto();
        secondUser.setName("Второй Пользователь");
        secondUser.setEmail("second@example.com");
        userService.createUser(secondUser);

        var users = userService.getAllUser();

        assertThat(users.size()).isEqualTo(2);
    }

    @Test
    void testUpdateUser() {
        UserDto savedUser = userService.createUser(userDto);

        savedUser.setName("Обновлённое Имя");
        UserDto updatedUser = userService.updateUser(savedUser.getId(), savedUser);

        assertThat(updatedUser.getName()).isEqualTo("Обновлённое Имя");
    }


    @Test
    void testDeleteUser() {
        UserDto savedUser = userService.createUser(userDto);
        userService.deleteUser(savedUser.getId());

        assertThrows(NotFoundException.class, () -> userService.getUserById(savedUser.getId()));
    }
}
