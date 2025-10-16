package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService bookingService;

    @Test
    void create() throws Exception {
        NewBookingRequest newBookingRequest = new NewBookingRequest();
        // подставь реальные сеттеры/поля:
        newBookingRequest.setItemId(11L);
        newBookingRequest.setStart(LocalDateTime.now().plusDays(1));
        newBookingRequest.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto resp = new BookingDto();
        resp.setId(1L);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(11L);
        resp.setItem(itemDto);

        UserDto userDto = new UserDto();
        userDto.setId(10L);
        resp.setBooker(userDto);
        resp.setStart(LocalDateTime.now().plusDays(1));
        resp.setEnd(LocalDateTime.now().plusDays(2));
        resp.setStatus(BookingStatus.WAITING);

        when(bookingService.create(any(NewBookingRequest.class), eq(10L))).thenReturn(resp);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(newBookingRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value(BookingStatus.WAITING.name()))
                .andExpect(jsonPath("$.start").exists())
                .andExpect(jsonPath("$.end").exists());
    }

    @Test
    void approve() throws Exception {
        BookingDto resp = new BookingDto();
        resp.setId(5L);
        resp.setStatus(BookingStatus.APPROVED);

        when(bookingService.approveOrReject(20L, 5L, true)).thenReturn(resp);

        mvc.perform(patch("/bookings/{bookingId}", 5L)
                        .header("X-Sharer-User-Id", "20")
                        .param("approved", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.status").value(BookingStatus.APPROVED.name()));
    }

    @Test
    void reject() throws Exception {
        BookingDto resp = new BookingDto();
        resp.setId(6L);
        resp.setStatus(BookingStatus.REJECTED);

        when(bookingService.approveOrReject(30L, 6L, false)).thenReturn(resp);

        mvc.perform(patch("/bookings/{bookingId}", 6L)
                        .header("X-Sharer-User-Id", "30")
                        .param("approved", "false")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(6))
                .andExpect(jsonPath("$.status").value(BookingStatus.REJECTED.name()));
    }

    @Test
    void getById() throws Exception {
        BookingDto resp = new BookingDto();
        resp.setId(7L);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(11L);
        resp.setItem(itemDto);

        UserDto userDto = new UserDto();
        userDto.setId(10L);
        resp.setBooker(userDto);
        resp.setStart(LocalDateTime.now().plusDays(1));
        resp.setEnd(LocalDateTime.now().plusDays(2));
        resp.setStatus(BookingStatus.WAITING);

        when(bookingService.getById(10L, 7L)).thenReturn(resp);

        mvc.perform(get("/bookings/{bookingId}", 7L)
                        .header("X-Sharer-User-Id", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.status").value(BookingStatus.WAITING.name()))
                .andExpect(jsonPath("$.start").exists())
                .andExpect(jsonPath("$.end").exists());
    }

    @Test
    void getAllByBookerId() throws Exception {
        BookingDto b1 = new BookingDto();
        b1.setId(23L);
        BookingDto b2 = new BookingDto();
        b2.setId(34L);

        when(bookingService.getALLByBookerId(44L, "All")).thenReturn(List.of(b1, b2));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "44")
                        .param("state", "All")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(23))
                .andExpect(jsonPath("$[1].id").value(34));
    }

    @Test
    void getAllByItemOwnerId() throws Exception {
        BookingDto b1 = new BookingDto();
        b1.setId(10L);
        BookingDto b2 = new BookingDto();
         b2.setId(68L);

        when(bookingService.getAllByItemOwnerId(55L, "PAST")).thenReturn(List.of(b1, b2));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "55")
                        .param("state", "PAST")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[1].id").value(68));
    }
}