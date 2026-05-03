package ru.practicum.shareit.integration;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestServiceIntegrationTest {

    private final ItemRequestService requestService;
    private final UserService userService;
    private final ItemService itemService;

    private Long userId;
    private Long secondUserId;
    private ItemRequestDto requestDto;

    @BeforeEach
    void setUp() {

        UserDto user = new UserDto();
        user.setName("Пользователь");
        user.setEmail("user@example.com");
        userId = userService.createUser(user).getId();

        UserDto secondUser = new UserDto();
        secondUser.setName("Второй Пользователь");
        secondUser.setEmail("second@example.com");
        secondUserId = userService.createUser(secondUser).getId();

        requestDto = new ItemRequestDto();
        requestDto.setDescription("Нужен зеркальный фотоаппарат");
    }

    @Test
    void testSaveRequestToDatabase() {
        ItemRequestDto savedRequest = requestService.createRequest(userId, requestDto);

        assertThat(savedRequest.getId()).isNotNull();
        assertThat(savedRequest.getDescription()).isEqualTo("Нужен зеркальный фотоаппарат");
        assertThat(savedRequest.getRequesterId()).isEqualTo(userId);
        assertThat(savedRequest.getCreated()).isNotNull();
    }

    @Test
    void testGetUserRequests() {
        requestService.createRequest(userId, requestDto);

        ItemRequestDto secondRequest = new ItemRequestDto();
        secondRequest.setDescription("Нужен шуруповерт");
        requestService.createRequest(userId, secondRequest);

        var requests = requestService.getUserRequests(userId);

        assertThat(requests.size()).isEqualTo(2);
    }

    @Test
    void testGetAllRequests() {

        requestService.createRequest(userId, requestDto);


        ItemRequestDto otherRequest = new ItemRequestDto();
        otherRequest.setDescription("Нужен телефон");
        requestService.createRequest(secondUserId, otherRequest);

        var requests = requestService.getAllRequests(userId);

        assertThat(requests.size()).isEqualTo(1);
    }

    @Test
    void testGetRequestById() {

        ItemRequestDto savedRequest = requestService.createRequest(userId, requestDto);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Фотоаппарат");
        itemDto.setDescription("Зеркальный фотоаппарат");
        itemDto.setAvailable(true);
        itemDto.setRequestId(savedRequest.getId());
        itemService.createItem(secondUserId, itemDto);

        ItemRequestDto foundRequest = requestService.getRequestById(secondUserId, savedRequest.getId());

        assertThat(foundRequest.getId()).isEqualTo(savedRequest.getId());
        assertThat(foundRequest.getDescription()).isEqualTo(savedRequest.getDescription());
        assertThat(foundRequest.getItems()).isNotNull();
        assertThat(foundRequest.getItems().get(0).getName()).isEqualTo("Фотоаппарат");
    }
}
