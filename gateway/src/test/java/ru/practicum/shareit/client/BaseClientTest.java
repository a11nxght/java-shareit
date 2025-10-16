package ru.practicum.shareit.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

/**
 * Тесты для BaseClient.
 * Проверяем: заголовки, методы, параметры URL, сериализацию тела, маппинг ошибок.
 */
class BaseClientTest {

    private RestTemplate restTemplate;
    private MockRestServiceServer server;
    private BaseClient client;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        server = MockRestServiceServer.createServer(restTemplate);
        client = new BaseClient(restTemplate);
    }

    @Test
    void get_withUserId_andTemplateParams_ok() {
        server.expect(once(),
                        requestTo("http://localhost:9090/items/5?state=PAST"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "10"))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andRespond(withSuccess("{\"id\":5}", MediaType.APPLICATION_JSON));

        var resp = client.get(
                "http://localhost:9090/items/{id}?state={state}",
                10L,
                Map.of("id", 5, "state", "PAST")
        );

        server.verify();
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isInstanceOf(Map.class);
        assertThat(((Map<?, ?>) resp.getBody()).get("id")).isEqualTo(5);
    }

    @Test
    void get_withoutUserId_noSharerHeader() {
        server.expect(once(),
                        requestTo("http://localhost:9090/ping"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(headerDoesNotExist("X-Sharer-User-Id"))
                .andRespond(withSuccess("{\"ok\":true}", MediaType.APPLICATION_JSON));

        var resp = client.get("http://localhost:9090/ping");

        server.verify();
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void post_withBody_andUserId_ok() {
        server.expect(once(),
                        requestTo("http://localhost:9090/items"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", "99"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Drill"))
                .andRespond(withStatus(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"id\":1,\"name\":\"Drill\"}"));

        var resp = client.post("http://localhost:9090/items", 99L, Map.of("name", "Drill"));

        server.verify();
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(((Map<?, ?>) resp.getBody()).get("name")).isEqualTo("Drill");
    }

    @Test
    void patch_withoutUserId_headerAbsent() {
        server.expect(once(),
                        requestTo("http://localhost:9090/items/7"))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(headerDoesNotExist("X-Sharer-User-Id"))
                .andExpect(jsonPath("$.name").value("New"))
                .andRespond(withSuccess("{\"id\":7,\"name\":\"New\"}", MediaType.APPLICATION_JSON));

        var resp = client.patch("http://localhost:9090/items/7", Map.of("name", "New"));

        server.verify();
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((Map<?, ?>) resp.getBody()).get("id")).isEqualTo(7);
    }

    @Test
    void delete_withUserId_ok_noBody() {
        server.expect(once(),
                        requestTo("http://localhost:9090/items/13"))
                .andExpect(method(HttpMethod.DELETE))
                .andExpect(header("X-Sharer-User-Id", "5"))
                .andRespond(withNoContent());

        var resp = client.delete("http://localhost:9090/items/13", 5L);

        server.verify();
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(resp.hasBody()).isFalse();
    }

    @Test
    void anyMethod_errorFromServer_mapsToResponse_withBodyBytes() {
        // RestTemplate по умолчанию бросит исключение на 404; BaseClient должен поймать и вернуть статус + body (byte[])
        server.expect(once(),
                        requestTo("http://localhost:9090/items/404"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"error\":\"not found\"}"));

        var resp = client.get("http://localhost:9090/items/404", 1L);

        server.verify();
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(resp.getBody()).isInstanceOf(byte[].class);
        String body = new String((byte[]) resp.getBody(), StandardCharsets.UTF_8);
        assertThat(body).contains("not found");
    }

    @Test
    void non2xx_withoutBody_returnsSameStatus_emptyBody() {
        server.expect(once(),
                        requestTo("http://localhost:9090/fail503"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE)); // без тела

        var resp = client.get("http://localhost:9090/fail503");

        server.verify();
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
    }

    @Test
    void get_queryTemplateOnly_ok() {
        // Проверяем подстановку значений только в query-параметры (?state={state})
        server.expect(once(),
                        requestTo("http://localhost:9090/bookings?state=PAST"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "44"))
                .andRespond(withSuccess("[{\"id\":1},{\"id\":2}]", MediaType.APPLICATION_JSON));

        var resp = client.get("http://localhost:9090/bookings?state={state}", 44L,
                Map.of("state", "PAST"));

        server.verify();
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        var list = (List<?>) resp.getBody();
        assertThat(list).hasSize(2);
    }
}
