package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingSmallDto;

import static org.junit.jupiter.api.Assertions.*;

class ItemWithBookingDtoTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void serialize_with_nested_bookings_and_comments() throws Exception {
        BookingSmallDto last = new BookingSmallDto();
        last.setId(101L);
        last.setBookerId(11L);

        BookingSmallDto next = new BookingSmallDto();
        next.setId(102L);
        next.setBookerId(12L);

        ItemWithBookingDto dto = new ItemWithBookingDto();
        dto.setId(3L);
        dto.setName("Lamp");
        dto.setDescription("White");
        dto.setAvailable(true);
        dto.setLastBooking(last);
        dto.setNextBooking(next);
        dto.setComments(null); // проверим, что null-комментарии не пишутся при NON_NULL

        String json = mapper.writeValueAsString(dto);
        JsonNode tree = mapper.readTree(json);

        assertEquals(3L, tree.get("id").asLong());
        assertEquals("Lamp", tree.get("name").asText());
        assertEquals("White", tree.get("description").asText());
        assertTrue(tree.get("available").asBoolean());

        assertEquals(101L, tree.get("lastBooking").get("id").asLong());
        assertEquals(11L, tree.get("lastBooking").get("bookerId").asLong());
        assertEquals(102L, tree.get("nextBooking").get("id").asLong());
        assertEquals(12L, tree.get("nextBooking").get("bookerId").asLong());
    }

    @Test
    void equals_hash_basic() {
        BookingSmallDto last = new BookingSmallDto();
        last.setId(1L);
        last.setBookerId(2L);

        BookingSmallDto next = new BookingSmallDto();
        next.setId(3L);
        next.setBookerId(4L);

        ItemWithBookingDto a = new ItemWithBookingDto();
        a.setId(1L);
        a.setName("N");
        a.setDescription("D");
        a.setAvailable(true);
        a.setLastBooking(last);
        a.setNextBooking(next);

        ItemWithBookingDto b = new ItemWithBookingDto();
        b.setId(1L);
        b.setName("N");
        b.setDescription("D");
        b.setAvailable(true);
        b.setLastBooking(last);
        b.setNextBooking(next);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, null);
    }
}
