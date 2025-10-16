package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void serialize_ok() throws Exception {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(77L);
        dto.setDescription("Нужна дрель");
        dto.setCreated(LocalDateTime.of(2025, 1, 2, 3, 4, 5));
        // items оставляем null

        JsonContent<ItemRequestDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(77);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Нужна дрель");

        // LocalDateTime сериализуется в ISO-8601 без наносекунд
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo("2025-01-02T03:04:05");

        // items отсутствует/равно null (на усмотрение Jackson) — assert не обязателен
    }

    @Test
    void deserialize_ok_ignoresUnknown() throws Exception {
        // created как ISO-строка; items отдадим как массив неизвестных объектов — Jackson их проигнорирует, если тип несовместим
        String jsonStr = "{\n" +
                "  \"id\": 5,\n" +
                "  \"description\": \"Кейс для шуруповёрта\",\n" +
                "  \"created\": \"2025-10-16T12:00:00\",\n" +
                "  \"unknown\": 123\n" +
                "}";

        ItemRequestDto dto = json.parseObject(jsonStr);

        assertThat(dto.getId()).isEqualTo(5L);
        assertThat(dto.getDescription()).isEqualTo("Кейс для шуруповёрта");
        assertThat(dto.getCreated()).isEqualTo(LocalDateTime.of(2025, 10, 16, 12, 0, 0));
        // items не присылали — остаётся null
        assertThat(dto.getItems()).isNull();
    }
}
