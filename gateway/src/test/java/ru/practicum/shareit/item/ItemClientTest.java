package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.util.UriUtils;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest(ItemClient.class)
@TestPropertySource(properties = {
        "shareit-server.url=http://localhost:9090"
})
class ItemClientTest {

    @Autowired
    private ItemClient itemClient;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void testCreate() throws Exception {
        ItemDto dto = new ItemDto();
        dto.setName("Drill");
        dto.setDescription("Cordless");
        dto.setAvailable(true);

        String responseJson = "{\"id\": 1, \"name\": \"Drill\", \"description\":\"Cordless\", \"available\": true}";

        server.expect(once(), requestTo("http://localhost:9090/items"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", "10"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Drill"))
                .andExpect(jsonPath("$.available").value(true))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        var resp = itemClient.create(10L, dto);

        server.verify();
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        JsonNode body = mapper.valueToTree(resp.getBody());
        assertThat(body.get("id").asLong()).isEqualTo(1L);
        assertThat(body.get("name").asText()).isEqualTo("Drill");
    }

    @Test
    void testUpdate() throws Exception {
        ItemDto dto = new ItemDto();
        dto.setName("New name");

        String responseJson = "{\"id\": 5, \"name\":\"New name\"}";

        server.expect(once(), requestTo("http://localhost:9090/items/5"))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(header("X-Sharer-User-Id", "20"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("New name"))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        var resp = itemClient.update(20L, 5L, dto);

        server.verify();
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        JsonNode body = mapper.valueToTree(resp.getBody());
        assertThat(body.get("id").asLong()).isEqualTo(5L);
        assertThat(body.get("name").asText()).isEqualTo("New name");
    }

    @Test
    void testGetItem() throws Exception {
        String responseJson = "{\"id\": 7, \"name\":\"Saw\"}";

        server.expect(once(), requestTo("http://localhost:9090/items/7"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "30"))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        var resp = itemClient.getItem(30L, 7L);

        server.verify();
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        JsonNode body = mapper.valueToTree(resp.getBody());
        assertThat(body.get("id").asLong()).isEqualTo(7L);
        assertThat(body.get("name").asText()).isEqualTo("Saw");
    }

    @Test
    void testGetItems() throws Exception {
        String responseJson = "[ {\"id\":1}, {\"id\":2}, {\"id\":3} ]";

        server.expect(once(), requestTo("http://localhost:9090/items"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "40"))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        var resp = itemClient.getItems(40L);

        server.verify();
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        var list = mapper.convertValue(resp.getBody(), java.util.List.class);
        assertThat(list).hasSize(3);
    }

    @Test
    void testSearchItems() throws Exception {
        String query = "drill";
        String responseJson = "[ {\"id\":10}, {\"id\":11} ]";

        server.expect(once(), requestTo("http://localhost:9090/items/search?text=" + query))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "50"))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        var resp = itemClient.searchItems(50L, query);

        server.verify();
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        var list = mapper.convertValue(resp.getBody(), java.util.List.class);
        assertThat(list).hasSize(2);
    }

    @Test
    void testCreateComment() throws Exception {
        CommentDto dto = new CommentDto();
        dto.setText("Nice item!");

        String responseJson = "{\"id\": 100, \"text\": \"Nice item!\"}";

        server.expect(once(), requestTo("http://localhost:9090/items/77/comment"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", "60"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.text").value("Nice item!"))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        var resp = itemClient.createComment(60L, 77L, dto);

        server.verify();
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        JsonNode body = mapper.valueToTree(resp.getBody());
        assertThat(body.get("id").asLong()).isEqualTo(100L);
        assertThat(body.get("text").asText()).isEqualTo("Nice item!");
    }

    @Test
    void testGetItem_notFound404() throws Exception {
        server.expect(once(), requestTo("http://localhost:9090/items/999"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON));

        var resp = itemClient.getItem(1L, 999L);

        server.verify();
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testCreate_validationError400() throws Exception {
        ItemDto dto = new ItemDto();
        // нарушаем валидацию на сервере, например нет name
        dto.setAvailable(true);

        server.expect(once(), requestTo("http://localhost:9090/items"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", "10"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON));

        var resp = itemClient.create(10L, dto);

        server.verify();
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testServerError500() throws Exception {
        server.expect(once(), requestTo("http://localhost:9090/items"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "77"))
                .andRespond(withServerError()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"error\":\"Internal\"}"));

        var resp = itemClient.getItems(77L);

        server.verify();
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void testSearchItems_urlEncoding() throws Exception {
        String text = "дрель 18V";
        String encoded = UriUtils.encodeQueryParam(text, StandardCharsets.UTF_8);
        server.expect(once(), requestTo("http://localhost:9090/items/" +
                        "search?text=" + encoded))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "5"))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        var resp = itemClient.searchItems(5L, text);

        server.verify();
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        var list = mapper.convertValue(resp.getBody(), java.util.List.class);
        assertThat(list).isEmpty();
    }

    @Test
    void testUpdate_acceptHeaderPresent() throws Exception {
        ItemDto dto = new ItemDto();
        dto.setDescription("New desc");

        server.expect(once(), requestTo("http://localhost:9090/items/12"))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(header("X-Sharer-User-Id", "2"))
                .andExpect(header("Accept", "application/json"))
                .andRespond(withSuccess("{\"id\":12}", MediaType.APPLICATION_JSON));

        var resp = itemClient.update(2L, 12L, dto);

        server.verify();
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
    }
}
