package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

import org.springframework.http.HttpStatus;


@RestClientTest(ItemRequestClient.class)
@TestPropertySource(properties = {
        "shareit-server.url=http://localhost:9090"
})
class ItemRequestClientTest {

    @Autowired
    private ItemRequestClient client;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void testCreate() throws Exception {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription("Нужна дрель");

        String responseJson = "{\"id\": 1, \"description\": \"Нужна дрель\"}";

        server.expect(once(), requestTo("http://localhost:9090/requests"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", "10"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.description").value("Нужна дрель"))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        var resp = client.create(10L, dto);

        server.verify();
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        JsonNode body = mapper.valueToTree(resp.getBody());
        assertThat(body.get("id").asLong()).isEqualTo(1L);
        assertThat(body.get("description").asText()).isEqualTo("Нужна дрель");
    }

    @Test
    void testGetOwn() {
        String responseJson = "[\n  {\"id\": 1, \"description\": \"Нужна дрель\"},\n  {\"id\": 2, \"description\": " +
                "\"Кейс для шуруповёрта\"}\n]";

        server.expect(once(), requestTo("http://localhost:9090/requests"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "20"))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        var resp = client.getOwn(20L);

        server.verify();
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        var list = mapper.convertValue(resp.getBody(), java.util.List.class);
        assertThat(list).hasSize(2);
    }

    @Test
    void testGetAll() {
        String responseJson = "[\n  {\"id\": 3},\n  {\"id\": 4},\n  {\"id\": 5}\n]";

        server.expect(once(), requestTo("http://localhost:9090/requests"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "30"))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        var resp = client.getAll(30L);

        server.verify();
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        var list = mapper.convertValue(resp.getBody(), java.util.List.class);
        assertThat(list).hasSize(3);
    }

    @Test
    void testGetById() {
        String responseJson = "{\"id\": 77, \"description\": \"Дрель\"}";

        server.expect(once(), requestTo("http://localhost:9090/requests/77"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "40"))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        var resp = client.getById(40L, 77L);

        server.verify();
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        JsonNode body = mapper.valueToTree(resp.getBody());
        assertThat(body.get("id").asLong()).isEqualTo(77L);
        assertThat(body.get("description").asText()).isEqualTo("Дрель");
    }

    @Test
    void testCreate_serverValidationError_400() throws Exception {
        ItemRequestDto dto = new ItemRequestDto(); // description = null

        server.expect(once(), requestTo("http://localhost:9090/requests"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", "10"))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"error\":\"description must not be null\"}"));

        var resp = client.create(10L, dto);

        server.verify();
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testGetById_notFound_404() {
        server.expect(once(), requestTo("http://localhost:9090/requests/999"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "40"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"error\":\"request not found\"}"));

        var resp = client.getById(40L, 999L);

        server.verify();
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testGetOwn_serverError_500() {
        server.expect(once(), requestTo("http://localhost:9090/requests"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "20"))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        var resp = client.getOwn(20L);

        server.verify();
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void testCreate_payloadContainsDescriptionUtf8() throws Exception {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription("Запрос: нужна дрель");

        String responseJson = "{\"id\": 10, \"description\": \"Запрос: нужна дрель\"}";

        server.expect(once(), requestTo("http://localhost:9090/requests"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", "15"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.description").value("Запрос: нужна дрель"))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        var resp = client.create(15L, dto);

        server.verify();
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        JsonNode body = mapper.valueToTree(resp.getBody());
        assertThat(body.get("id").asLong()).isEqualTo(10L);
        assertThat(body.get("description").asText()).isEqualTo("Запрос: нужна дрель");
    }

}
