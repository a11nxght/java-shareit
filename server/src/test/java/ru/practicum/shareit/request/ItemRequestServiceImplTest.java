package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemRequestServiceImplTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ItemRequestService itemRequestService;

    @Test
    void testCreate() {
        User user = saveUser("Ivan", "dfgd@ya.ru");
        ItemRequestDto itemRequestDto = createItemRequestDto("нужна УШМ");
        ItemRequestDto created = itemRequestService.create(user.getId(), itemRequestDto);

        assertThat(created.getId(), notNullValue());
        assertThat(created.getDescription(), equalTo(itemRequestDto.getDescription()));
    }

    @Test
    void testNotCreate() {
        ItemRequestDto itemRequestDto = createItemRequestDto("нужна УШМ");
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.create(52L, itemRequestDto));
        assertEquals("User not found.", exception.getMessage());
    }

    @Test
    void testGetOwn() {
        User user = saveUser("Ivan", "dfgd@ya.ru");
        ItemRequestDto itemRequestDto1 = itemRequestService.create(user.getId(),
                createItemRequestDto("нужна УШМ"));
        ItemRequestDto itemRequestDto2 = itemRequestService.create(user.getId(),
                createItemRequestDto("нужна дрель"));
        List<ItemRequestDto> requestDtos = itemRequestService.getOwn(user.getId());
        assertThat(requestDtos.getFirst().getId(), equalTo(itemRequestDto2.getId()));
        assertThat(requestDtos.get(1).getDescription(), equalTo(itemRequestDto1.getDescription()));
    }

    @Test
    void testGetAll() {
        User user = saveUser("Ivan", "dfgd@ya.ru");
        User user2 = saveUser("Petr", "petrsss@ya.ru");
        ItemRequestDto itemRequestDto1 = itemRequestService.create(user.getId(),
                createItemRequestDto("нужна УШМ"));
        ItemRequestDto itemRequestDto2 = itemRequestService.create(user2.getId(),
                createItemRequestDto("нужна дрель"));
        ItemRequestDto itemRequestDto3 = itemRequestService.create(user.getId(),
                createItemRequestDto("нужна пила"));
        List<ItemRequestDto> requestDtos = itemRequestService.getAll(user2.getId());
        assertThat(requestDtos.getFirst().getDescription(), equalTo(itemRequestDto3.getDescription()));
        assertThat(requestDtos.get(1).getDescription(), equalTo(itemRequestDto1.getDescription()));
    }

    @Test
    void testGetById() {
        User user = saveUser("Ivan", "dfgd@ya.ru");
        User user2 = saveUser("Petr", "petrsss@ya.ru");
        ItemRequestDto itemRequestDto1 = itemRequestService.create(user.getId(),
                createItemRequestDto("нужна УШМ"));
        ItemRequestDto itemRequestDto2 = itemRequestService.create(user2.getId(),
                createItemRequestDto("нужна дрель"));
        ItemRequestDto itemRequestDto3 = itemRequestService.create(user.getId(),
                createItemRequestDto("нужна пила"));
        ItemRequestDto getsItem = itemRequestService.getById(user.getId(), itemRequestDto3.getId());
        assertThat(getsItem.getId(), equalTo(itemRequestDto3.getId()));
    }

    @Test
    void create_userNotFound_shouldThrow() {
        ItemRequestDto dto = createItemRequestDto("нужна шлифмашина");
        assertThrows(NotFoundException.class,
                () -> itemRequestService.create(9999L, dto));
    }

    @Test
    void getOwn_userNotFound_shouldThrow() {
        assertThrows(NotFoundException.class,
                () -> itemRequestService.getOwn(9999L));
    }

    @Test
    void getAll_userNotFound_shouldThrow() {
        assertThrows(NotFoundException.class,
                () -> itemRequestService.getAll(9999L));
    }

    @Test
    void getById_userNotFound_shouldThrow() {
        // сначала создадим реальный запрос другим пользователем
        User user = saveUser("Ivan", "ivan@n.n");
        ItemRequestDto created = itemRequestService.create(user.getId(), createItemRequestDto("нужна пила"));

        // теперь запросим по несуществующему пользователю
        assertThrows(NotFoundException.class,
                () -> itemRequestService.getById(9999L, created.getId()));
    }

    @Test
    void getById_requestNotFound_shouldThrow() {
        User user = saveUser("Petr", "petr@n.n");
        assertThrows(NotFoundException.class,
                () -> itemRequestService.getById(user.getId(), 987654321L));
    }

    @Test
    void getOwn_whenNoRequests_returnsEmpty() {
        User user = saveUser("NoReq", "no@req.ru");
        List<ItemRequestDto> own = itemRequestService.getOwn(user.getId());
        assertTrue(own.isEmpty());
    }

    @Test
    void getAll_excludesOwnRequests_andReturnsEmptyIfNoOthers() {
        User me = saveUser("Me", "me@me.ru");
        User other = saveUser("Other", "other@ex.ru");

        ItemRequestDto myReq1 = itemRequestService.create(me.getId(), createItemRequestDto("мой запрос 1"));
        ItemRequestDto myReq2 = itemRequestService.create(me.getId(), createItemRequestDto("мой запрос 2"));

        List<ItemRequestDto> forOther = itemRequestService.getAll(other.getId());
        assertFalse(forOther.isEmpty(), "У Other должны быть видны чужие (мои) запросы");

        assertEquals(2, forOther.size());
        var forOtherIds = forOther.stream().map(ItemRequestDto::getId).toList();
        assertTrue(forOtherIds.containsAll(List.of(myReq1.getId(), myReq2.getId())));

        ItemRequestDto foreign = itemRequestService.create(other.getId(), createItemRequestDto("ищу перфоратор"));

        List<ItemRequestDto> forMe = itemRequestService.getAll(me.getId());
        assertFalse(forMe.isEmpty());
        assertEquals(foreign.getId(), forMe.getFirst().getId());
        var forMeIds = forMe.stream().map(ItemRequestDto::getId).toList();
        assertFalse(forMeIds.contains(myReq1.getId()));
        assertFalse(forMeIds.contains(myReq2.getId()));
    }


    private User createUser(long id, String name, String email) {
        User u = new User();
        u.setName(name);
        u.setEmail(email);
        return u;
    }

    private User saveUser(String name, String email) {
        User u = new User();
        u.setName(name);
        u.setEmail(email);
        return userRepository.save(u);
    }

    private ItemRequestDto createItemRequestDto(String description) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription(description);
        return itemRequestDto;
    }
}