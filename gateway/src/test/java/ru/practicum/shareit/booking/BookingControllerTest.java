package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.BookingState;

import java.time.LocalDateTime;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    BookingClient bookingClient;

    @Test
    void testCreate() throws Exception {
        NewBookingRequest req = new NewBookingRequest();
        req.setStart(LocalDateTime.now().plusDays(1));
        req.setEnd(LocalDateTime.now().plusDays(2));
        req.setItemId(123L);

        when(bookingClient.create(anyLong(), any(NewBookingRequest.class)))
                .thenReturn(ResponseEntity.ok(Map.of()));

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 10L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        verify(bookingClient).create(eq(10L), any(NewBookingRequest.class));
    }

    @Test
    void testCreateValidationFailItemIdMissing() throws Exception {
        NewBookingRequest req = new NewBookingRequest();
        req.setStart(LocalDateTime.now().plusDays(1));
        req.setEnd(LocalDateTime.now().plusDays(2));

        when(bookingClient.create(anyLong(), any(NewBookingRequest.class)))
                .thenReturn(ResponseEntity.ok(Map.of()));

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 10L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    void testCreateValidationFailDatesInPastOrWrong() throws Exception {
        NewBookingRequest req = new NewBookingRequest();
        req.setStart(LocalDateTime.now().minusHours(1));
        req.setEnd(LocalDateTime.now().minusHours(2));
        req.setItemId(5L);

        when(bookingClient.create(anyLong(), any(NewBookingRequest.class)))
                .thenReturn(ResponseEntity.ok(Map.of()));

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 10L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    void testApproveOrReject() throws Exception {
        when(bookingClient.approveOrReject(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(ResponseEntity.ok(Map.of()));

        mvc.perform(patch("/bookings/{bookingId}", 77L)
                        .header("X-Sharer-User-Id", 20L)
                        .param("approved", "true"))
                .andExpect(status().isOk());

        verify(bookingClient).approveOrReject(20L, 77L, true);
    }

    @Test
    void testGetById() throws Exception {
        when(bookingClient.getById(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok(Map.of()));

        mvc.perform(get("/bookings/{bookingId}", 42L)
                        .header("X-Sharer-User-Id", 11L))
                .andExpect(status().isOk());

        verify(bookingClient).getById(11L, 42L);
    }

    @Test
    void testGetAllByBookerId() throws Exception {
        when(bookingClient.getAllByBookerId(anyLong(), any(BookingState.class)))
                .thenReturn(ResponseEntity.ok(Map.of()));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 33L))
                .andExpect(status().isOk());

        verify(bookingClient).getAllByBookerId(33L, BookingState.ALL);
    }

    @Test
    void testGetAllByBookerIdBadState() throws Exception {
        when(bookingClient.getAllByBookerId(anyLong(), any(BookingState.class)))
                .thenReturn(ResponseEntity.ok(Map.of()));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 33L)
                        .param("state", "WRONG"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    void testGetAllByItemOwnerId() throws Exception {
        when(bookingClient.getAllByItemOwnerId(anyLong(), any(BookingState.class)))
                .thenReturn(ResponseEntity.ok(Map.of()));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 44L)
                        .param("state", "PAST"))
                .andExpect(status().isOk());

        verify(bookingClient).getAllByItemOwnerId(44L, BookingState.PAST);
    }

    @Test
    void getAllByItemOwnerIdBadState() throws Exception {
        when(bookingClient.getAllByItemOwnerId(anyLong(), any(BookingState.class)))
                .thenReturn(ResponseEntity.ok(Map.of()));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 44L)
                        .param("state", "WRONG"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }
}
