package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    @MockBean ItemClient itemClient;

    @Test
    void testCreate() throws Exception {
        ItemDto body = new ItemDto();
        body.setName("Дрель");
        body.setDescription("как новая");
        body.setAvailable(true);

        when(itemClient.create(anyLong(), any(ItemDto.class)))
                .thenReturn(ResponseEntity.ok(Map.of()));

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "10")
                        .contentType(APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().isOk());

        verify(itemClient).create(eq(10L), any(ItemDto.class));
        verifyNoMoreInteractions(itemClient);
    }

    @Test
    void testCreateValidationFail() throws Exception {
        ItemDto invalid = new ItemDto();

        when(itemClient.create(anyLong(), any(ItemDto.class)))
                .thenReturn(ResponseEntity.ok(Map.of()));

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "10")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemClient);
    }

    @Test
    void testUpdate() throws Exception {
        ItemDto body = new ItemDto();
        body.setDescription("обновлённое описание");

        when(itemClient.update(anyLong(), anyLong(), any(ItemDto.class)))
                .thenReturn(ResponseEntity.ok(Map.of()));

        mvc.perform(patch("/items/{itemId}", 5L)
                        .header("X-Sharer-User-Id", "10")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().isOk());

        verify(itemClient).update(eq(10L), eq(5L), any(ItemDto.class));
        verifyNoMoreInteractions(itemClient);
    }

    @Test
    void testGetItem() throws Exception {
        when(itemClient.getItem(anyLong(), anyLong())).thenReturn(ResponseEntity.ok(Map.of()));

        mvc.perform(get("/items/{itemId}", 7L)
                        .header("X-Sharer-User-Id", "10"))
                .andExpect(status().isOk());

        verify(itemClient).getItem(10L, 7L);
        verifyNoMoreInteractions(itemClient);
    }

    @Test
    void testGetItems() throws Exception {
        when(itemClient.getItems(anyLong())).thenReturn(ResponseEntity.ok(Map.of()));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", "10"))
                .andExpect(status().isOk());

        verify(itemClient).getItems(10L);
        verifyNoMoreInteractions(itemClient);
    }

    @Test
    void testSearchItems() throws Exception {
        when(itemClient.searchItems(anyLong(), anyString())).thenReturn(ResponseEntity.ok(Map.of()));

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", "10")
                        .param("text", "дрель"))
                .andExpect(status().isOk());

        verify(itemClient).searchItems(10L, "дрель");
        verifyNoMoreInteractions(itemClient);
    }

    @Test
    void testCreateComment() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Отличная вещь!");

        when(itemClient.createComment(anyLong(), anyLong(), any(CommentDto.class)))
                .thenReturn(ResponseEntity.ok(Map.of()));

        mvc.perform(post("/items/{itemId}/comment", 5L)
                        .header("X-Sharer-User-Id", "10")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk());

        verify(itemClient).createComment(eq(10L), eq(5L), any(CommentDto.class));
        verifyNoMoreInteractions(itemClient);
    }

    @Test
    void testCreateCommentValidationFail() throws Exception {
        CommentDto invalid = new CommentDto();

        when(itemClient.createComment(anyLong(), anyLong(), any(CommentDto.class)))
                .thenReturn(ResponseEntity.ok(Map.of()));

        mvc.perform(post("/items/{itemId}/comment", 5L)
                        .header("X-Sharer-User-Id", "10")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemClient);
    }
}
