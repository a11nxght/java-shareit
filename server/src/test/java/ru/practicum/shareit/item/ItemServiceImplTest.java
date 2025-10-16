package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemServiceImplTest {

    @Autowired
    ItemService itemService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    BookingRepository bookingRepository;

    @Test
    void testCreateUpdateGetItem() {
        User user = makeUser("qwe@ya.ru", "Иван Иванович");
        ItemDto itemDto = makeItemDto("УШМ", "как новая", true);
        user = userRepository.save(user);
        ItemDto createdItemDto = itemService.create(itemDto, user.getId());

        assertThat(createdItemDto.getId(), notNullValue());
        assertThat(createdItemDto.getName(), equalTo(itemDto.getName()));
        assertThat(createdItemDto.getDescription(), equalTo(itemDto.getDescription()));

        itemDto.setName("болгарка");
        ItemDto updatedItemDto = itemService.update(itemDto, createdItemDto.getId(), user.getId());
        assertThat(updatedItemDto.getName(), equalTo(itemDto.getName()));

        ItemWithBookingDto getItemDto = itemService.getItem(createdItemDto.getId(), user.getId());
        assertThat(getItemDto.getName(), equalTo(itemDto.getName()));
        assertThat(getItemDto.getId(), notNullValue());
    }

    @Test
    void testGetItems() {
        User user = makeUser("qwe@ya.ru", "Иван Иванович");
        user = userRepository.save(user);
        ItemDto itemDto1 = makeItemDto("УШМ", "как новая", true);
        ItemDto itemDto2 = makeItemDto("лобзик", "хорошо пилит фанеру", true);
        itemService.create(itemDto1, user.getId());
        itemService.create(itemDto2, user.getId());
        List<ItemWithBookingDto> items = itemService.getItems(user.getId());
        assertThat(items.getFirst().getName(), equalTo(itemDto1.getName()));
        assertThat(items.getLast().getName(), equalTo(itemDto2.getName()));
    }

    @Test
    void testSearchItems() {
        User user = makeUser("qwe@ya.ru", "Иван Иванович");
        user = userRepository.save(user);
        ItemDto itemDto1 = makeItemDto("УШМ", "как новая", true);
        ItemDto itemDto2 = makeItemDto("лобзик", "хорошо пилит фанеру", true);
        itemService.create(itemDto1, user.getId());
        itemService.create(itemDto2, user.getId());
        List<ItemDto> items = itemService.searchItems("лобз", user.getId());
        assertThat(items.getFirst().getName(), equalTo(itemDto2.getName()));
    }

    @Test
    void testCreateComment() {
        User user = makeUser("qwe@ya.ru", "Иван Иванович");
        user = userRepository.save(user);
        Item item = new Item();
        item.setName("УШМ");
        item.setDescription("как новая");
        item.setOwner(user);
        item.setAvailable(true);
        item = itemRepository.save(item);
        User user2 = makeUser("asd@ya.ru", "Петр Петрович");
        user2 = userRepository.save(user2);
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setStatus(BookingStatus.APPROVED);
        booking.setItem(item);
        booking.setBooker(user2);
        bookingRepository.save(booking);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("super");
        CommentDto createdCommentDto = itemService.createComment(commentDto, user2.getId(), item.getId());
        assertThat(createdCommentDto.getId(), notNullValue());
        assertThat(createdCommentDto.getText(), equalTo(commentDto.getText()));
        assertThat(createdCommentDto.getAuthorName(), equalTo(user2.getName()));
    }

    private ItemDto makeItemDto(String name, String description, boolean available) {
        ItemDto itemDto = new ItemDto();
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(available);
        return itemDto;
    }

    private User makeUser(String email, String name) {
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        return user;
    }
}