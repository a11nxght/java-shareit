package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemRequestClient itemRequestClient;

    @Test
    void testCreate() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Нужна дрель");

        when(itemRequestClient.create(anyLong(), any(ItemRequestDto.class)))
                .thenReturn(ResponseEntity.ok(Map.of()));

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", "10")
                        .contentType(APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk());

        verify(itemRequestClient).create(anyLong(), any(ItemRequestDto.class));
        verifyNoMoreInteractions(itemRequestClient);
    }

    @Test
    void testCreateValidationFail() throws Exception {
        ItemRequestDto invalid = new ItemRequestDto();

        when(itemRequestClient.create(anyLong(), any(ItemRequestDto.class)))
                .thenReturn(ResponseEntity.ok(Map.of()));

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", "10")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemRequestClient);
    }

    @Test
    void testGetOwn() throws Exception {
        when(itemRequestClient.getOwn(anyLong())).thenReturn(ResponseEntity.ok(Map.of()));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "10"))
                .andExpect(status().isOk());

        verify(itemRequestClient).getOwn(anyLong());
        verifyNoMoreInteractions(itemRequestClient);
    }

    @Test
    void testGetAll() throws Exception {
        when(itemRequestClient.getAll(anyLong())).thenReturn(ResponseEntity.ok(Map.of()));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "10"))
                .andExpect(status().isOk());

        verify(itemRequestClient).getAll(anyLong());
        verifyNoMoreInteractions(itemRequestClient);
    }

    @Test
    void testGetById() throws Exception {
        when(itemRequestClient.getById(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok(Map.of()));

        mvc.perform(get("/requests/{requestId}", 5L)
                        .header("X-Sharer-User-Id", "10"))
                .andExpect(status().isOk());

        verify(itemRequestClient).getById(10L, 5L);
        verifyNoMoreInteractions(itemRequestClient);
    }
}
