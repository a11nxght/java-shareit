package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Autowired
    private ObjectMapper objectMapper;

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void serialize_valid_minimal_ok() throws Exception {
        ItemDto dto = new ItemDto();
        dto.setId(1L);
        dto.setName("Drill");
        dto.setDescription("Cordless");
        dto.setAvailable(true);

        JsonContent<ItemDto> content = json.write(dto);

        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("$.name").isEqualTo("Drill");
        assertThat(content).extractingJsonPathStringValue("$.description").isEqualTo("Cordless");
        assertThat(content).extractingJsonPathBooleanValue("$.available").isTrue();
    }

    @Test
    void serialize_withComments_ok() throws Exception {
        CommentDto c1 = new CommentDto();
        c1.setId(10L);
        c1.setText("Nice item!");

        ItemDto dto = new ItemDto();
        dto.setId(2L);
        dto.setName("Saw");
        dto.setDescription("Sharp");
        dto.setAvailable(false);
        dto.setRequestId(77L);
        dto.setComments(List.of(c1));

        JsonContent<ItemDto> content = json.write(dto);

        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(2);
        assertThat(content).extractingJsonPathNumberValue("$.requestId").isEqualTo(77);
        assertThat(content).extractingJsonPathArrayValue("$.comments").hasSize(1);
        assertThat(content).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(10);
        assertThat(content).extractingJsonPathStringValue("$.comments[0].text").isEqualTo("Nice item!");
    }

    @Test
    void deserialize_valid_ok() throws Exception {
        String src = "{\n" +
                "  \"id\": 5,\n" +
                "  \"name\": \"Grinder\",\n" +
                "  \"description\": \"Like new\",\n" +
                "  \"available\": true,\n" +
                "  \"requestId\": 123,\n" +
                "  \"comments\": [ {\"id\": 1, \"text\": \"wow\"} ]\n" +
                "}";

        ItemDto dto = json.parseObject(src);

        assertThat(dto.getId()).isEqualTo(5L);
        assertThat(dto.getName()).isEqualTo("Grinder");
        assertThat(dto.getDescription()).isEqualTo("Like new");
        assertThat(dto.getAvailable()).isTrue();
        assertThat(dto.getRequestId()).isEqualTo(123L);
        assertThat(dto.getComments()).hasSize(1);
        assertThat(dto.getComments().get(0).getText()).isEqualTo("wow");
    }
}
