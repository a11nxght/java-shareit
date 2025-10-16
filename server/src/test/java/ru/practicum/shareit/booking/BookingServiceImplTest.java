package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BookingServiceImplTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

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
    void testApproveAndReject() {
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
        BookingDto rejectedBookingDto = bookingService.approveOrReject(user.getId(), bookingDto.getId(), false);
        assertThat(rejectedBookingDto.getStatus(), equalTo(BookingStatus.REJECTED));
    }

    @Test
    void testGetById() {
        User user = saveUser("Ivan", "qwer@ya.ru");
        Item item = saveItem("pila", "ostraya", user);
        User user2 = saveUser("Petr", "gdfsf@ya.ry");
        NewBookingRequest newBookingRequest = new NewBookingRequest();
        newBookingRequest.setStart(LocalDateTime.now().plusDays(1));
        newBookingRequest.setEnd(LocalDateTime.now().plusDays(2));
        newBookingRequest.setItemId(item.getId());
        BookingDto bookingDto = bookingService.create(newBookingRequest, user2.getId());
        BookingDto getsBookingDto = bookingService.getById(user.getId(), bookingDto.getId());
        assertThat(bookingDto.getId(), equalTo(bookingDto.getId()));
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