package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentDtoTest {

    @Test
    void getters_setters_and_basic_equality() {
        CommentDto dto1 = new CommentDto();
        dto1.setId(10L);
        dto1.setText("ok");
        dto1.setAuthorName("Ann");
        dto1.setCreated(LocalDateTime.of(2030, 1, 2, 3, 4, 5));

        assertEquals(10L, dto1.getId());
        assertEquals("ok", dto1.getText());
        assertEquals("Ann", dto1.getAuthorName());
        assertEquals(LocalDateTime.of(2030, 1, 2, 3, 4, 5), dto1.getCreated());

        CommentDto dto2 = new CommentDto();
        dto2.setId(10L);
        dto2.setText("ok");
        dto2.setAuthorName("Ann");
        dto2.setCreated(LocalDateTime.of(2030, 1, 2, 3, 4, 5));

        // Lombok @Data: equals/hashCode по всем полям
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, new Object());
    }
}
