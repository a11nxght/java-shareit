package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemDtoTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void serialize_minimal_no_null_fields() throws Exception {
        ItemDto dto = new ItemDto();
        dto.setId(1L);
        dto.setName("Drill");
        dto.setDescription("Cordless");
        dto.setAvailable(true);

        String json = mapper.writeValueAsString(dto);
        JsonNode tree = mapper.readTree(json);

        assertEquals(1L, tree.get("id").asLong());
        assertEquals("Drill", tree.get("name").asText());
        assertEquals("Cordless", tree.get("description").asText());
        assertTrue(tree.get("available").asBoolean());
    }

    @Test
    void getters_setters_and_equals_hash() {
        ItemDto a = new ItemDto();
        a.setId(5L);
        a.setName("Box");
        a.setDescription("Big");
        a.setAvailable(false);
        a.setRequestId(null);
        a.setComments(List.of());

        ItemDto b = new ItemDto();
        b.setId(5L);
        b.setName("Box");
        b.setDescription("Big");
        b.setAvailable(false);
        b.setRequestId(null);
        b.setComments(List.of());

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, null);
    }
}
