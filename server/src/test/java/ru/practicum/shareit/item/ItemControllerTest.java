package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemService itemService;

    @Test
    void testCreate() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("УШМ");
        itemDto.setDescription("как новая");
        itemDto.setAvailable(true);
        when(itemService.create(any(ItemDto.class), anyLong())).thenReturn(itemDto);

        mvc.perform(post("/items")
                .header("X-Sharer-User-Id", 10)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("УШМ"));
    }

    @Test
    void update() throws Exception {
        ItemDto patch = new ItemDto();
        patch.setName("Шуруповерт");

        ItemDto resp = new ItemDto();
        resp.setId(5L);
        resp.setName("Шуруповерт");
        resp.setDescription("новый desc");
        resp.setAvailable(true);

        when(itemService.update(any(ItemDto.class), eq(5L), eq(10L))).thenReturn(resp);

        mvc.perform(patch("/items/{itemId}", 5L)
                        .header("X-Sharer-User-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(patch)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("Шуруповерт"));
    }

    @Test
    void getItem() throws Exception {
        ItemWithBookingDto resp = new ItemWithBookingDto();
        resp.setId(7L);
        resp.setName("Дрель");

        when(itemService.getItem(7L, 15L)).thenReturn(resp);

        mvc.perform(get("/items/{itemId}", 7L)
                        .header("X-Sharer-User-Id", "15")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.name").value("Дрель"));
    }

    @Test
    void getItems() throws Exception {
        ItemWithBookingDto a = new ItemWithBookingDto();
        a.setId(1L); a.setName("Перфоратор");
        ItemWithBookingDto b = new ItemWithBookingDto();
        b.setId(2L); b.setName("Лобзик");

        when(itemService.getItems(52L)).thenReturn(List.of(a, b));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", "52")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Перфоратор"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Лобзик"));
    }

    @Test
    void searchItems() throws Exception {
        ItemDto i1 = new ItemDto();
        i1.setId(10L); i1.setName("Отвертка"); i1.setAvailable(true);
        ItemDto i2 = new ItemDto();
        i2.setId(11L); i2.setName("Очень большая отвертка"); i2.setAvailable(true);

        when(itemService.searchItems(eq("отв"), eq(5L))).thenReturn(List.of(i1, i2));

        mvc.perform(get("/items/search")
                        .param("text", "отв")
                        .header("X-Sharer-User-Id", "5")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].name").value("Отвертка"))
                .andExpect(jsonPath("$[1].id").value(11))
                .andExpect(jsonPath("$[1].name").value("Очень большая отвертка"));
    }

    @Test
    void createComment() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Отличная вещь!");

        CommentDto resp = new CommentDto();
        resp.setId(123L);
        resp.setText("Отличная вещь!");
        resp.setAuthorName("Dmitry");
        resp.setCreated(LocalDateTime.now());

        when(itemService.createComment(any(CommentDto.class), eq(7L), eq(3L)))
                .thenReturn(resp);

        mvc.perform(post("/items/{itemId}/comment", 3L)
                        .header("X-Sharer-User-Id", "7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(123))
                .andExpect(jsonPath("$.text").value("Отличная вещь!"))
                .andExpect(jsonPath("$.authorName").value("Dmitry"));
    }

}