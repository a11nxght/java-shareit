package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UpdateUserRequestJsonTest {

    @Autowired
    private JacksonTester<UpdateUserRequest> json;

    @Test
    void write_shouldSerializeFields_includingNulls() throws Exception {
        UpdateUserRequest dto = new UpdateUserRequest();
        dto.setName("New Name");
        dto.setEmail(null);

        var content = json.write(dto);

        assertThat(content).extractingJsonPathStringValue("$.name").isEqualTo("New Name");
        assertThat(content).extractingJsonPathValue("$.email").isNull();
    }

    @Test
    void read_shouldDeserializeAndExposeHelperMethods_true() throws Exception {
        String src = "{\"name\":\"Alice\",\"email\":\"alice@mail.com\"}";


        UpdateUserRequest parsed = json.parseObject(src);

        assertThat(parsed.getName()).isEqualTo("Alice");
        assertThat(parsed.getEmail()).isEqualTo("alice@mail.com");
        assertThat(parsed.hasName()).isTrue();
        assertThat(parsed.hasEmail()).isTrue();
    }

    @Test
    void read_blankAndNull_shouldMakeHelpersFalse() throws Exception {
        String src = "{\"name\":\"   \",\"email\":null}";


        UpdateUserRequest parsed = json.parseObject(src);

        assertThat(parsed.getName()).isEqualTo("   ");
        assertThat(parsed.getEmail()).isNull();
        assertThat(parsed.hasName()).isFalse();
        assertThat(parsed.hasEmail()).isFalse();
    }

    @Test
    void read_missingFields_shouldBeNull_andHelpersFalse() throws Exception {
        String src = "{ }";

        UpdateUserRequest parsed = json.parseObject(src);

        assertThat(parsed.getName()).isNull();
        assertThat(parsed.getEmail()).isNull();
        assertThat(parsed.hasName()).isFalse();
        assertThat(parsed.hasEmail()).isFalse();
    }
}
