package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.mockito.Mockito.when;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemRequestService itemRequestService;

    @Test
    void create() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Нужна дрель");

        ItemRequestDto resp = new ItemRequestDto();
        resp.setId(1L);
        resp.setDescription("Нужна дрель");
        resp.setCreated(LocalDateTime.now());

        when(itemRequestService.create(anyLong(), any(ItemRequestDto.class))).thenReturn(resp);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Нужна дрель"));
    }

    @Test
    void getOwn() throws Exception {
        ItemRequestDto r1 = new ItemRequestDto();
        r1.setId(1L);
        r1.setDescription("Ищу перфоратор");
        ItemRequestDto r2 = new ItemRequestDto();
        r2.setId(2L);
        r2.setDescription("Нужна шлифмашинка");

        when(itemRequestService.getOwn(7L)).thenReturn(List.of(r1, r2));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "7")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Ищу перфоратор"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].description").value("Нужна шлифмашинка"));
    }

    @Test
    void getAll() throws Exception {
        ItemRequestDto r1 = new ItemRequestDto();
        r1.setId(10L);
        r1.setDescription("Нужна отвертка");
        ItemRequestDto r2 = new ItemRequestDto();
        r2.setId(11L);
        r2.setDescription("Нужен уровень");

        when(itemRequestService.getAll(99L)).thenReturn(List.of(r1, r2));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "99")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].description").value("Нужна отвертка"))
                .andExpect(jsonPath("$[1].id").value(11))
                .andExpect(jsonPath("$[1].description").value("Нужен уровень"));
    }

    @Test
    void getById() throws Exception {
        ItemRequestDto resp = new ItemRequestDto();
        resp.setId(5L);
        resp.setDescription("Нужна дрель");

        when(itemRequestService.getById(eq(12L), eq(5L))).thenReturn(resp);

        mvc.perform(get("/requests/{requestId}", 5L)
                        .header("X-Sharer-User-Id", "12")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.description").value("Нужна дрель"));
    }
}
