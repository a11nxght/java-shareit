package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemShortDtoJsonTest {

    @Autowired
    private JacksonTester<ItemShortDto> json;

    @Test
    void serialize_ok() throws Exception {
        ItemShortDto dto = new ItemShortDto(10L, "Drill", 5L);

        JsonContent<ItemShortDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(10);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Drill");
        assertThat(result).extractingJsonPathNumberValue("$.ownerId").isEqualTo(5);
    }

    @Test
    void deserialize_ok() throws Exception {
        String jsonStr = "{ \"id\": 77, \"name\": \"Saw\", \"ownerId\": 9 }";

        ItemShortDto dto = json.parseObject(jsonStr);

        assertThat(dto.getId()).isEqualTo(77L);
        assertThat(dto.getName()).isEqualTo("Saw");
        assertThat(dto.getOwnerId()).isEqualTo(9L);
    }
}
