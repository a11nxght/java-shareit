package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemShortDtoTest {

    @Test
    void allArgsConstructor_getters_equals_hash() {
        ItemShortDto a = new ItemShortDto(7L, "Chair", 33L);
        ItemShortDto b = new ItemShortDto(7L, "Chair", 33L);

        assertEquals(7L, a.getId());
        assertEquals("Chair", a.getName());
        assertEquals(33L, a.getOwnerId());

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, new ItemShortDto(8L, "Chair", 33L));
    }
}
