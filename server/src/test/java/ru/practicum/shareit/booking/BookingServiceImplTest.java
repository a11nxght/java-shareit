package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BookingServiceImplTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    BookingServiceImpl bookingService;

    @Test
    void testCreate() {
        User user = saveUser("Ivan", "qwer@ya.ru");
        Item item = saveItem("pila", "ostraya", user);
        User user2 = saveUser("Petr", "gdfsf@ya.ry");
        NewBookingRequest newBookingRequest = new NewBookingRequest();
        newBookingRequest.setStart(LocalDateTime.now().plusDays(1));
        newBookingRequest.setEnd(LocalDateTime.now().plusDays(2));
        newBookingRequest.setItemId(item.getId());
        BookingDto bookingDto = bookingService.create(newBookingRequest, user2.getId());
        assertThat(bookingDto.getId(), notNullValue());
        assertThat(bookingDto.getBooker().getName(), equalTo(user2.getName()));
    }

    @Test
    void testApprove() {
        User user = saveUser("Ivan", "qwer@ya.ru");
        Item item = saveItem("pila", "ostraya", user);
        User user2 = saveUser("Petr", "gdfsf@ya.ry");
        NewBookingRequest newBookingRequest = new NewBookingRequest();
        newBookingRequest.setStart(LocalDateTime.now().plusDays(1));
        newBookingRequest.setEnd(LocalDateTime.now().plusDays(2));
        newBookingRequest.setItemId(item.getId());
        BookingDto bookingDto = bookingService.create(newBookingRequest, user2.getId());
        BookingDto approvedBookingDto = bookingService.approveOrReject(user.getId(), bookingDto.getId(), true);
        assertThat(approvedBookingDto.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    void testReject() {
        User user = saveUser("Ivan", "qwer@ya.ru");
        Item item = saveItem("pila", "ostraya", user);
        User user2 = saveUser("Petr", "gdfsf@ya.ry");
        NewBookingRequest newBookingRequest = new NewBookingRequest();
        newBookingRequest.setStart(LocalDateTime.now().plusDays(1));
        newBookingRequest.setEnd(LocalDateTime.now().plusDays(2));
        newBookingRequest.setItemId(item.getId());
        BookingDto bookingDto = bookingService.create(newBookingRequest, user2.getId());
        BookingDto rejectedBookingDto = bookingService.approveOrReject(user.getId(), bookingDto.getId(), false);
        assertThat(rejectedBookingDto.getStatus(), equalTo(BookingStatus.REJECTED));
    }

    @Test
    void testGetById() {
        User owner = saveUser("Ivan", "qwer@ya.ru");
        Item item = saveItem("pila", "ostraya", owner);
        User booker = saveUser("Petr", "gdfsf@ya.ry");

        NewBookingRequest req = new NewBookingRequest();
        req.setStart(LocalDateTime.now().plusDays(1));
        req.setEnd(LocalDateTime.now().plusDays(2));
        req.setItemId(item.getId());

        BookingDto created = bookingService.create(req, booker.getId());

        BookingDto byOwner = bookingService.getById(owner.getId(), created.getId());
        assertThat(byOwner.getId(), equalTo(created.getId()));
        assertThat(byOwner.getItem().getId(), equalTo(item.getId()));
        assertThat(byOwner.getBooker().getId(), equalTo(booker.getId()));

        BookingDto byBooker = bookingService.getById(booker.getId(), created.getId());
        assertThat(byBooker.getId(), equalTo(created.getId()));
    }

    @Test
    void testGetAllByBookerIdAndGetAllByItemOwnerId() {
        User user = saveUser("Ivan", "qwer@ya.ru");
        Item item = saveItem("pila", "ostraya", user);
        User user2 = saveUser("Petr", "gdfsf@ya.ry");
        NewBookingRequest newBookingRequest = new NewBookingRequest();
        newBookingRequest.setStart(LocalDateTime.now().plusDays(1));
        newBookingRequest.setEnd(LocalDateTime.now().plusDays(2));
        newBookingRequest.setItemId(item.getId());
        BookingDto bookingDto = bookingService.create(newBookingRequest, user2.getId());

        User user3 = saveUser("Egor", "gor@ya.ru");
        Item item2 = saveItem("drill", "novaya", user3);
        User user4 = saveUser("Zahar", "aha@ya.ry");
        NewBookingRequest newBookingRequest2 = new NewBookingRequest();
        newBookingRequest2.setStart(LocalDateTime.now().plusDays(3));
        newBookingRequest2.setEnd(LocalDateTime.now().plusDays(4));
        newBookingRequest2.setItemId(item2.getId());
        BookingDto bookingDto2 = bookingService.create(newBookingRequest2, user4.getId());
        List<BookingDto> bookingDtos = bookingService.getALLByBookerId(user4.getId(), "ALL");
        assertThat(bookingDtos.getFirst().getItem().getName(), equalTo(item2.getName()));
        List<BookingDto> bookingDtos2 = bookingService.getAllByItemOwnerId(user.getId(), "All");
        assertThat(bookingDtos2.getFirst().getItem().getName(), equalTo(item.getName()));
    }

    @Test
    void testCreate_byOwnerItself_shouldFail() {
        User owner = saveUser("Owner", "own@ya.ru");
        Item item = saveItem("pila", "ostraya", owner);

        NewBookingRequest req = new NewBookingRequest();
        req.setStart(LocalDateTime.now().plusDays(1));
        req.setEnd(LocalDateTime.now().plusDays(2));
        req.setItemId(item.getId());

        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> bookingService.create(req, owner.getId())
        );

        assertThat(ex.getMessage(), equalTo("Owner can not book."));
    }

    @Test
    void testApproveByNonOwner_shouldFail() {
        User owner = saveUser("Owner", "o@ya.ru");
        Item item = saveItem("drill", "new", owner);
        User booker = saveUser("Booker", "b@ya.ru");

        NewBookingRequest req = new NewBookingRequest();
        req.setStart(LocalDateTime.now().plusDays(1));
        req.setEnd(LocalDateTime.now().plusDays(2));
        req.setItemId(item.getId());

        BookingDto created = bookingService.create(req, booker.getId());

        assertThrows(
                ForbiddenException.class,
                () -> bookingService.approveOrReject(booker.getId(), created.getId(), true)
        );
    }

    @Test
    void testApproveTwice_shouldFail() {
        User owner = saveUser("Owner2", "o2@ya.ru");
        Item item = saveItem("saw", "sharp", owner);
        User booker = saveUser("Booker2", "b2@ya.ru");

        NewBookingRequest req = new NewBookingRequest();
        req.setStart(LocalDateTime.now().plusDays(1));
        req.setEnd(LocalDateTime.now().plusDays(2));
        req.setItemId(item.getId());

        BookingDto created = bookingService.create(req, booker.getId());
        BookingDto approved = bookingService.approveOrReject(owner.getId(), created.getId(), true);
        assertThat(approved.getStatus(), equalTo(BookingStatus.APPROVED));

        org.junit.jupiter.api.Assertions.assertThrows(
                BadRequestException.class,
                () -> bookingService.approveOrReject(owner.getId(), created.getId(), true)
        );
    }

    @Test
    void testGetById_notOwnerNorBooker_shouldFail() {
        User owner = saveUser("Owner3", "o3@ya.ru");
        Item item = saveItem("hammer", "heavy", owner);
        User booker = saveUser("Booker3", "b3@ya.ru");
        User stranger = saveUser("Stranger", "s@ya.ru");

        NewBookingRequest req = new NewBookingRequest();
        req.setStart(LocalDateTime.now().plusDays(1));
        req.setEnd(LocalDateTime.now().plusDays(2));
        req.setItemId(item.getId());

        BookingDto created = bookingService.create(req, booker.getId());

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> bookingService.getById(stranger.getId(), created.getId())
        );

        assertThat(ex.getMessage(),
                equalTo("Unable to get booking. Only the owner or the booker of the item can get the booking."));
    }

    @Test
    void testGetAllByBooker_unknownState_shouldFail() {
        User u = saveUser("U", "u@ya.ru");
        org.junit.jupiter.api.Assertions.assertThrows(
                BadRequestException.class, // либо UnsupportedStatusException — подставь своё
                () -> bookingService.getALLByBookerId(u.getId(), "UNKNOWN")
        );
    }

    @Test
    void testGetAllByOwner_unknownState_shouldFail() {
        User u = saveUser("O", "owner@ya.ru");
        org.junit.jupiter.api.Assertions.assertThrows(
                BadRequestException.class,
                () -> bookingService.getAllByItemOwnerId(u.getId(), "???")
        );
    }

    @Test
    void testGetAllByBooker_filtersByState() {
        LocalDateTime now = LocalDateTime.now();

        User owner  = saveUser("OwnF",  "ownf@ya.ru");
        Item item   = saveItem("lamp", "desk", owner);
        User booker = saveUser("BookF", "bookf@ya.ru");

        // 1) Создаём ТРИ валидных будущих брони
        NewBookingRequest futureReq = new NewBookingRequest();
        futureReq.setStart(now.plusDays(3));
        futureReq.setEnd(now.plusDays(4));
        futureReq.setItemId(item.getId());
        BookingDto future = bookingService.create(futureReq, booker.getId());

        NewBookingRequest currentReq = new NewBookingRequest();
        currentReq.setStart(now.plusHours(3)); // пока будущее — пройдёт валидацию
        currentReq.setEnd(now.plusHours(5));
        currentReq.setItemId(item.getId());
        BookingDto current = bookingService.create(currentReq, booker.getId());

        NewBookingRequest pastReq = new NewBookingRequest();
        pastReq.setStart(now.plusDays(6));
        pastReq.setEnd(now.plusDays(7));
        pastReq.setItemId(item.getId());
        BookingDto past = bookingService.create(pastReq, booker.getId());

        var pastEntity = bookingRepository.findById(past.getId()).orElseThrow();
        pastEntity.setStart(now.minusDays(3));
        pastEntity.setEnd(now.minusDays(2));
        bookingRepository.save(pastEntity);

        var currentEntity = bookingRepository.findById(current.getId()).orElseThrow();
        currentEntity.setStart(now.minusHours(1));
        currentEntity.setEnd(now.plusHours(2));
        bookingRepository.save(currentEntity);

        BookingDto rejected = bookingService.approveOrReject(owner.getId(), future.getId(), false);
        assertThat(rejected.getStatus(), equalTo(BookingStatus.REJECTED));

        List<BookingDto> waiting = bookingService.getALLByBookerId(booker.getId(), "WAITING");
        assertThat(waiting, notNullValue());

        List<BookingDto> rejectedList = bookingService.getALLByBookerId(booker.getId(), "REJECTED");
        assertThat(rejectedList.stream().anyMatch(b -> b.getId().equals(rejected.getId())), equalTo(true));

        List<BookingDto> futureList  = bookingService.getALLByBookerId(booker.getId(), "FUTURE");
        List<BookingDto> pastList    = bookingService.getALLByBookerId(booker.getId(), "PAST");
        List<BookingDto> currentList = bookingService.getALLByBookerId(booker.getId(), "CURRENT");

        assertThat(futureList.stream().allMatch(b -> b.getStart().isAfter(now)), equalTo(true));
        assertThat(pastList.stream().allMatch(b -> b.getEnd().isBefore(now)), equalTo(true));
        assertThat(currentList.stream().allMatch(b ->
                !b.getStart().isAfter(now) && !b.getEnd().isBefore(now)
        ), equalTo(true));
    }

    @Test
    void testCreate_whenItemUnavailable_shouldFail() {
        User owner = saveUser("OwnX", "ownx@ya.ru");
        Item item = saveItem("box", "closed", owner);
        item.setAvailable(false);
        itemRepository.save(item);

        User booker = saveUser("BookX", "bookx@ya.ru");

        NewBookingRequest req = new NewBookingRequest();
        req.setStart(LocalDateTime.now().plusDays(1));
        req.setEnd(LocalDateTime.now().plusDays(2));
        req.setItemId(item.getId());

        org.junit.jupiter.api.Assertions.assertThrows(
                BadRequestException.class,
                () -> bookingService.create(req, booker.getId())
        );
    }

    @Test
    void create_nullStartOrEnd_shouldThrow() {
        User owner = saveUser("Own2","o2@end");
        Item item = saveItem("i2","d2", owner);
        User booker = saveUser("B2","b2@end");

        NewBookingRequest noStart = new NewBookingRequest();
        noStart.setItemId(item.getId());
        noStart.setEnd(LocalDateTime.now().plusDays(1));
        assertThrows(ru.practicum.shareit.exceptions.BadRequestException.class,
                () -> bookingService.create(noStart, booker.getId()));

        NewBookingRequest noEnd = new NewBookingRequest();
        noEnd.setItemId(item.getId());
        noEnd.setStart(LocalDateTime.now().plusDays(1));
        assertThrows(ru.practicum.shareit.exceptions.BadRequestException.class,
                () -> bookingService.create(noEnd, booker.getId()));
    }

    @Test
    void create_itemNotFound_orUserNotFound_shouldThrow() {
        NewBookingRequest req = new NewBookingRequest();
        req.setItemId(999_999L);
        req.setStart(LocalDateTime.now().plusDays(1));
        req.setEnd(LocalDateTime.now().plusDays(2));

        // user не найден
        assertThrows(ru.practicum.shareit.exceptions.NotFoundException.class,
                () -> bookingService.create(req, 999_999L));
    }

    @Test
    void getAllByItemOwner_eachState_hasResultOrEmpty() {
        // минимально прогоняем несколько состояний для owner-веток
        User owner  = saveUser("OwnerX","ox@x");
        Item item   = saveItem("Lamp","d", owner);
        User booker = saveUser("BookX","bx@x");

        // WAITING
        NewBookingRequest w = new NewBookingRequest();
        w.setItemId(item.getId());
        w.setStart(LocalDateTime.now().plusDays(1));
        w.setEnd(LocalDateTime.now().plusDays(2));
        BookingDto waiting = bookingService.create(w, booker.getId());

        // REJECTED
        bookingService.approveOrReject(owner.getId(), waiting.getId(), false);

        // FUTURE
        NewBookingRequest f = new NewBookingRequest();
        f.setItemId(item.getId());
        f.setStart(LocalDateTime.now().plusDays(5));
        f.setEnd(LocalDateTime.now().plusDays(6));
        bookingService.create(f, booker.getId());

        // Проверяем несколько состояний у owner
        bookingService.getAllByItemOwnerId(owner.getId(), "WAITING");
        bookingService.getAllByItemOwnerId(owner.getId(), "REJECTED");
        bookingService.getAllByItemOwnerId(owner.getId(), "FUTURE");
    }


    private User saveUser(String name, String email) {
        User u = new User();
        u.setName(name);
        u.setEmail(email);
        return userRepository.save(u);
    }

    private Item saveItem(String name, String description, User owner) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(true);
        item.setOwner(owner);
        return itemRepository.save(item);
    }

    private BookingDto createBookingDto() {
        return new BookingDto();
    }
}