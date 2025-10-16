package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JsonTest
class NewBookingRequestJsonTest {

    @Autowired
    ObjectMapper mapper;

    @Test
    void serialize_toIso8601_ok() throws Exception {
        NewBookingRequest dto = new NewBookingRequest();
        dto.setItemId(5L);
        dto.setStart(LocalDateTime.of(2030, 1, 2, 10, 0, 0));
        dto.setEnd(LocalDateTime.of(2030, 1, 3, 12, 30, 0));

        String json = mapper.writeValueAsString(dto);
        assertThat(json).contains("\"itemId\":5");
        assertThat(json).contains("\"start\":\"2030-01-02T10:00:00\"");
        assertThat(json).contains("\"end\":\"2030-01-03T12:30:00\"");
    }

    @Test
    void deserialize_fromIso8601_ok() throws Exception {
        String json = "{\"itemId\":5,\"start\":\"2030-01-02T10:00:00\",\"end\":\"2030-01-03T12:30:00\"}";
        NewBookingRequest dto = mapper.readValue(json, NewBookingRequest.class);
        assertThat(dto.getItemId()).isEqualTo(5L);
        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2030,1,2,10,0,0));
        assertThat(dto.getEnd()).isEqualTo(LocalDateTime.of(2030,1,3,12,30,0));
    }

    @Test
    void deserialize_badFormat_fails() {
        String json = "{\"itemId\":5,\"start\":\"02-01-2030 10:00\",\"end\":\"2030-01-03T12:30:00\"}";

        assertThatThrownBy(() -> mapper.readValue(json, NewBookingRequest.class))
                .isInstanceOf(com.fasterxml.jackson.databind.exc.InvalidFormatException.class);
    }
}
