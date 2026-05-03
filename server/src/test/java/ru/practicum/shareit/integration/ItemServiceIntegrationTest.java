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
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceIntegrationTest {

    private final ItemService itemService;
    private final UserService userService;

    private Long ownerId;
    private Long bookerId;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {

        UserDto owner = new UserDto();
        owner.setName("Владелец");
        owner.setEmail("owner@example.com");
        ownerId = userService.createUser(owner).getId();


        UserDto booker = new UserDto();
        booker.setName("Арендатор");
        booker.setEmail("booker@example.com");
        bookerId = userService.createUser(booker).getId();


        itemDto = new ItemDto();
        itemDto.setName("Фотоаппарт");
        itemDto.setDescription("Зеркальный фотоаппарт");
        itemDto.setAvailable(true);
    }

    @Test
    void testSaveItemToDatabase() {
        ItemDto savedItem = itemService.createItem(ownerId, itemDto);

        assertThat(savedItem.getId()).isNotNull();
        assertThat(savedItem.getName()).isEqualTo("Фотоаппарт");
        assertThat(savedItem.getDescription()).isEqualTo("Зеркальный фотоаппарт");
        assertThat(savedItem.getAvailable()).isTrue();
    }

    @Test
    void testGetItemById() {
        ItemDto savedItem = itemService.createItem(ownerId, itemDto);
        ItemDto foundItem = itemService.getItemById(savedItem.getId(), ownerId);

        assertThat(foundItem.getId()).isEqualTo(savedItem.getId());
        assertThat(foundItem.getName()).isEqualTo(savedItem.getName());
    }

    @Test
    void testGetAllItemsByOwner() {
        itemService.createItem(ownerId, itemDto);

        ItemDto secondItem = new ItemDto();
        secondItem.setName("Шуруповерт");
        secondItem.setDescription("Компактный шуруповерт");
        secondItem.setAvailable(true);
        itemService.createItem(ownerId, secondItem);

        var items = itemService.getAllItemsByOwner(ownerId);

        assertThat(items.size()).isEqualTo(2);
    }

    @Test
    void testUpdateItem() {
        ItemDto savedItem = itemService.createItem(ownerId, itemDto);

        savedItem.setName("Профессиональный фотоаппарт");
        ItemDto updatedItem = itemService.updateItem(savedItem.getId(), ownerId, savedItem);

        assertThat(updatedItem.getName()).isEqualTo("Профессиональный фотоаппарт");
    }

    @Test
    void testSearchItemByText() {
        itemService.createItem(ownerId, itemDto);

        var foundItems = itemService.searchItem("фотоаппарт");
        var foundItemsSecond = itemService.searchItem("шуруповерт");

        assertThat(foundItems.size()).isEqualTo(1);
        assertThat(foundItemsSecond.size()).isEqualTo(0);
    }
}
