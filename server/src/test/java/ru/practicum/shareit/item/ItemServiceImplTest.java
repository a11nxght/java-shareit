package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    void createComment_byNonBooker_shouldFail() {
        User owner = userRepository.save(makeUser("own@x", "Owner"));

        Item item = new Item();
        item.setName("дрель");
        item.setDescription("ok");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        User stranger = userRepository.save(makeUser("s@x", "Stranger"));

        CommentDto dto = new CommentDto();
        dto.setText("hi");

        Item finalItem = item;
        assertThrows(
                BadRequestException.class,
                () -> itemService.createComment(dto, stranger.getId(), finalItem.getId())
        );
    }

    @Test
    void createComment_beforeBookingFinished_shouldFail() {
        User owner = userRepository.save(makeUser("own2@x", "Owner2"));

        Item item = new Item();
        item.setName("пила");
        item.setDescription("ok");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        User booker = userRepository.save(makeUser("b@x", "Booker"));

        Booking b = new Booking();
        b.setItem(item);
        b.setBooker(booker);
        b.setStart(LocalDateTime.now().plusHours(1));
        b.setEnd(LocalDateTime.now().plusHours(3));
        b.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(b);

        CommentDto dto = new CommentDto();
        dto.setText("ещё рано");

        Item finalItem = item;
        assertThrows(
                BadRequestException.class,
                () -> itemService.createComment(dto, booker.getId(), finalItem.getId())
        );
    }

    @Test
    void update_partialOnlyName_shouldKeepOtherFields() {
        User owner = userRepository.save(makeUser("o@x", "Owner"));

        Item source = new Item();
        source.setName("старое имя");
        source.setDescription("desc");
        source.setAvailable(true);
        source.setOwner(owner);
        Item saved = itemRepository.save(source);

        ItemDto patch = new ItemDto();
        patch.setName("новое имя"); // только имя

        ItemDto updated = itemService.update(patch, saved.getId(), owner.getId());
        assertThat(updated.getName(), equalTo("новое имя"));
        assertThat(updated.getDescription(), equalTo("desc"));
        assertThat(updated.getAvailable(), equalTo(true));
    }

    @Test
    void update_byNonOwner_shouldFail() {
        User owner = userRepository.save(makeUser("o2@x", "Owner2"));

        Item item = new Item();
        item.setName("вещь");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        User stranger = userRepository.save(makeUser("s2@x", "Stranger"));

        ItemDto patch = new ItemDto();
        patch.setDescription("hack");

        Item finalItem = item;
        org.junit.jupiter.api.Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.update(patch, finalItem.getId(), stranger.getId())
        );
    }

    @Test
    void getItem_ownerSeesLastAndNext_nonOwnerDoesNot() {
        LocalDateTime now = LocalDateTime.now();

        User owner = userRepository.save(makeUser("own3@x", "Owner3"));

        Item item = new Item();
        item.setName("лампа");
        item.setDescription("desk");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        User booker = userRepository.save(makeUser("book@x", "Booker"));
        User other  = userRepository.save(makeUser("other@x", "Other"));

        Booking past = new Booking();
        past.setItem(item); past.setBooker(booker);
        past.setStart(now.minusDays(3)); past.setEnd(now.minusDays(1));
        past.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(past);

        Booking cur = new Booking();
        cur.setItem(item); cur.setBooker(booker);
        cur.setStart(now.minusHours(1)); cur.setEnd(now.plusHours(2));
        cur.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(cur);

        Booking fut = new Booking();
        fut.setItem(item); fut.setBooker(booker);
        fut.setStart(now.plusDays(2)); fut.setEnd(now.plusDays(3));
        fut.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(fut);

        ItemWithBookingDto dtoForOwner = itemService.getItem(item.getId(), owner.getId());
        assertThat(dtoForOwner.getLastBooking(), notNullValue());
        assertThat(dtoForOwner.getNextBooking(), notNullValue());
        assertThat(dtoForOwner.getLastBooking().getId(), equalTo(cur.getId()));
        assertThat(dtoForOwner.getNextBooking().getId(), equalTo(fut.getId()));

        ItemWithBookingDto dtoForOther = itemService.getItem(item.getId(), other.getId());
        assertThat(dtoForOther.getLastBooking(), nullValue());
        assertThat(dtoForOther.getNextBooking(), nullValue());
    }

    @Test
    void searchItems_returnsOnlyAvailable() {
        User owner = userRepository.save(makeUser("ava@x", "Owner"));

        Item i1 = new Item();
        i1.setName("дрель");
        i1.setDescription("мощная");
        i1.setAvailable(true);
        i1.setOwner(owner);
        itemRepository.save(i1);

        Item i2 = new Item();
        i2.setName("дрель");
        i2.setDescription("сломана");
        i2.setAvailable(false);
        i2.setOwner(owner);
        itemRepository.save(i2);

        List<ItemDto> res = itemService.searchItems("дрель", owner.getId());
        assertThat(res, not(empty()));
        assertThat(res.stream().allMatch(ItemDto::getAvailable), equalTo(true));
    }

    @Test
    void create_userNotFound_shouldThrow() {
        ItemDto dto = new ItemDto();
        dto.setName("n"); dto.setDescription("d"); dto.setAvailable(true);
        assertThrows(ru.practicum.shareit.exceptions.NotFoundException.class,
                () -> itemService.create(dto, 999_999L));
    }

    @Test
    void getItem_itemNotFound_shouldThrow() {
        User u = userRepository.save(makeUser("u@x", "U"));
        assertThrows(ru.practicum.shareit.exceptions.NotFoundException.class,
                () -> itemService.getItem(987_654_321L, u.getId()));
    }

    @Test
    void createComment_userNotFound_shouldThrow() {
        Item item = new Item();
        item.setName("x"); item.setDescription("y"); item.setAvailable(true);
        item.setOwner(userRepository.save(makeUser("o@x","O")));
        item = itemRepository.save(item);

        CommentDto comment = new CommentDto();
        comment.setText("hi");
        Item finalItem = item;
        assertThrows(ru.practicum.shareit.exceptions.NotFoundException.class,
                () -> itemService.createComment(comment, 999_999L, finalItem.getId()));
    }

    @Test
    void createComment_itemNotFound_shouldThrow() {
        User booker = userRepository.save(makeUser("b@x","B"));
        CommentDto comment = new CommentDto();
        comment.setText("hi");
        assertThrows(ru.practicum.shareit.exceptions.NotFoundException.class,
                () -> itemService.createComment(comment, booker.getId(), 999_999L));
    }

    @Test
    void searchItems_blankText_returnsEmptyList() {
        User u = userRepository.save(makeUser("s@x","S"));
        assertTrue(itemService.searchItems("   ", u.getId()).isEmpty());
    }

    @Test
    void getItems_userNotFound_shouldThrow() {
        assertTrue(itemService.getItems(999_999L).isEmpty());;
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