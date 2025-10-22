package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JsonTest
class CommentDtoJsonTest {

    @Autowired
    ObjectMapper mapper;

    @Test
    void serialize_ok_isoDatetime() throws Exception {
        CommentDto dto = new CommentDto();
        dto.setId(100L);
        dto.setText("Nice item!");
        dto.setAuthorName("Dmitry");
        dto.setCreated(LocalDateTime.of(2030, 1, 2, 10, 5, 30));

        String json = mapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":100");
        assertThat(json).contains("\"text\":\"Nice item!\"");
        assertThat(json).contains("\"authorName\":\"Dmitry\"");
        assertThat(json).contains("\"created\":\"2030-01-02T10:05:30\"");
    }

    @Test
    void deserialize_ok_isoDatetime() throws Exception {
        String json = "{\"id\":7,\"text\":\"Спасибо!\",\"authorName\":\"Ivan\",\"created\":\"2031-03-04T12:00:00\"}";


        CommentDto dto = mapper.readValue(json, CommentDto.class);

        assertThat(dto.getId()).isEqualTo(7L);
        assertThat(dto.getText()).isEqualTo("Спасибо!");
        assertThat(dto.getAuthorName()).isEqualTo("Ivan");
        assertThat(dto.getCreated()).isEqualTo(LocalDateTime.of(2031, 3, 4, 12, 0, 0));
    }

    @Test
    void deserialize_badDate_fails() {
        String json = "{\"id\":1,\"text\":\"ok\",\"created\":\"04-03-2031 12:00\"}";

        assertThatThrownBy(() -> mapper.readValue(json, CommentDto.class))
                .isInstanceOf(com.fasterxml.jackson.databind.exc.InvalidFormatException.class);
    }

    @Test
    void deserialize_missingOptionalFields_ok() throws Exception {
        String json = "{\"text\":\"only text\"}";

        CommentDto dto = mapper.readValue(json, CommentDto.class);

        assertThat(dto.getText()).isEqualTo("only text");
        assertThat(dto.getAuthorName()).isNull();
        assertThat(dto.getCreated()).isNull();
        assertThat(dto.getId()).isNull();
    }
}
