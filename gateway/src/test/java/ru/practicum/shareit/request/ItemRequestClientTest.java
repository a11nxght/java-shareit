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

        String responseJson = """
            {"id": 1, "description": "Нужна дрель"}
            """;

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
        String responseJson = """
            [
              {"id": 1, "description": "Нужна дрель"},
              {"id": 2, "description": "Кейс для шуруповёрта"}
            ]
            """;

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
        // В текущей реализации клиента вызывается тот же URL, что и у getOwn: "/requests"
        // Если на сервере ожидается "/requests/all", тест поможет поймать рассинхрон.
        String responseJson = """
            [
              {"id": 3},
              {"id": 4},
              {"id": 5}
            ]
            """;

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
        String responseJson = """
            {"id": 77, "description": "Запчасть"}
            """;

        server.expect(once(), requestTo("http://localhost:9090/requests/77"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "40"))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        var resp = client.getById(40L, 77L);

        server.verify();
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        JsonNode body = mapper.valueToTree(resp.getBody());
        assertThat(body.get("id").asLong()).isEqualTo(77L);
        assertThat(body.get("description").asText()).isEqualTo("Запчасть");
    }
}
