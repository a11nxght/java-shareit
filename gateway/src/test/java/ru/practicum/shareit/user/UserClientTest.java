package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest(UserClient.class)
@TestPropertySource(properties = {
        "shareit-server.url=http://localhost:9090"
})
class UserClientTest {

    @Autowired
    private UserClient client;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void testCreate() throws Exception {
        NewUserRequest req = new NewUserRequest();
        req.setName("Dmitry");
        req.setEmail("dmitry@ya.ru");

        String responseJson = "{\"id\": 101, \"name\": \"Dmitry\", \"email\": \"dmitry@ya.ru\"}";


        server.expect(once(), requestTo("http://localhost:9090/users"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Dmitry"))
                .andExpect(jsonPath("$.email").value("dmitry@ya.ru"))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        var resp = client.create(req);

        server.verify();
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        JsonNode body = mapper.valueToTree(resp.getBody());
        assertThat(body.get("id").asLong()).isEqualTo(101L);
        assertThat(body.get("name").asText()).isEqualTo("Dmitry");
        assertThat(body.get("email").asText()).isEqualTo("dmitry@ya.ru");
    }

    @Test
    void testUpdate() throws Exception {
        UpdateUserRequest req = new UpdateUserRequest();
        req.setName("New Name");
        req.setEmail("new@mail.com");

        String responseJson = "{\"id\": 42, \"name\": \"New Name\", \"email\": \"new@mail.com\"}";

        server.expect(once(), requestTo("http://localhost:9090/users/42"))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("New Name"))
                .andExpect(jsonPath("$.email").value("new@mail.com"))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        var resp = client.update(42L, req);

        server.verify();
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        JsonNode body = mapper.valueToTree(resp.getBody());
        assertThat(body.get("id").asLong()).isEqualTo(42L);
        assertThat(body.get("name").asText()).isEqualTo("New Name");
    }

    @Test
    void testFindAll() {
        String responseJson = "[{\"id\":1,\"name\":\"A\"}, {\"id\":2,\"name\":\"B\"}]";


        server.expect(once(), requestTo("http://localhost:9090/users"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        var resp = client.findAll();

        server.verify();
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        var list = mapper.convertValue(resp.getBody(), java.util.List.class);
        assertThat(list).hasSize(2);
    }

    @Test
    void testGetUser() {
        String responseJson = "{\"id\":7,\"name\":\"User7\"}";

        server.expect(once(), requestTo("http://localhost:9090/users/7"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        var resp = client.getUser(7L);

        server.verify();
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        JsonNode body = mapper.valueToTree(resp.getBody());
        assertThat(body.get("id").asLong()).isEqualTo(7L);
    }

    @Test
    void testDeleteUser() {
        server.expect(once(), requestTo("http://localhost:9090/users/42"))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withNoContent());

        var resp = client.deleteUser(42L);

        server.verify();
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
    }
}
