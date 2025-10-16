package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class NewUserRequestJsonTest {

    @Autowired
    private JacksonTester<NewUserRequest> json;

    @Test
    void write_shouldSerializeAllFields() throws Exception {
        NewUserRequest dto = new NewUserRequest();
        dto.setEmail("user@mail.com");
        dto.setName("User Name");

        var content = json.write(dto);

        assertThat(content).extractingJsonPathStringValue("$.email").isEqualTo("user@mail.com");
        assertThat(content).extractingJsonPathStringValue("$.name").isEqualTo("User Name");
    }

    @Test
    void read_shouldDeserializeAllFields() throws Exception {
        String src = "{\"email\":\"user@mail.com\",\"name\":\"User Name\"}";
        NewUserRequest parsed = json.parseObject(src);

        assertThat(parsed.getEmail()).isEqualTo("user@mail.com");
        assertThat(parsed.getName()).isEqualTo("User Name");
    }

    @Test
    void read_missingFields_allowNullsHere_JSONOnly() throws Exception {
        String src = "{ }";

        NewUserRequest parsed = json.parseObject(src);

        assertThat(parsed.getEmail()).isNull();
        assertThat(parsed.getName()).isNull();
    }
}
