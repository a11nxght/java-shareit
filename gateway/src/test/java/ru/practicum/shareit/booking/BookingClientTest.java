package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.BookingState;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest(BookingClient.class)
@TestPropertySource(properties = {
        "shareit-server.url=http://localhost:9090" // база, к которой будет бить клиент
})
class BookingClientTest {

    @Autowired
    private BookingClient bookingClient;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void testCreate() throws Exception {
        NewBookingRequest req = new NewBookingRequest();
        req.setItemId(11L);
        req.setStart(LocalDateTime.now().plusDays(1).withNano(0));
        req.setEnd(LocalDateTime.now().plusDays(2).withNano(0));

        String responseJson = "{\n" +
                "  \"id\": 1,\n" +
                "  \"status\": \"WAITING\",\n" +
                "  \"item\": {\"id\": 11},\n" +
                "  \"booker\": {\"id\": 10}\n" +
                "}";


        server.expect(once(), requestTo("http://localhost:9090/bookings"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", "10"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.itemId").value(11))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        var resp = bookingClient.create(10L, req);

        server.verify();
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        JsonNode body = mapper.valueToTree(resp.getBody());
        assertThat(body.get("id").asLong()).isEqualTo(1L);
        assertThat(body.get("status").asText()).isEqualTo("WAITING");
    }

    @Test
    void testApproveOrReject() throws Exception {
        String responseJson = "{ \"id\": 5, \"status\": \"APPROVED\" }";

        server.expect(once(), requestTo("http://localhost:9090/bookings/5?approved=true"))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(header("X-Sharer-User-Id", "20"))
                // тело нам не важно — клиент шлёт пустой DTO; можно не проверять
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        var resp = bookingClient.approveOrReject(20L, 5L, true);

        server.verify();
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        JsonNode body = mapper.valueToTree(resp.getBody());
        assertThat(body.get("id").asLong()).isEqualTo(5L);
        assertThat(body.get("status").asText()).isEqualTo("APPROVED");
    }

    @Test
    void testGetById() throws Exception {
        String responseJson = "{ \"id\": 7, \"status\": \"WAITING\" }";


        server.expect(once(), requestTo("http://localhost:9090/bookings/7"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "10"))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        var resp = bookingClient.getById(10L, 7L);

        server.verify();
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        JsonNode body = mapper.valueToTree(resp.getBody());
        assertThat(body.get("id").asLong()).isEqualTo(7L);
        assertThat(body.get("status").asText()).isEqualTo("WAITING");
    }

    @Test
    void testGetAllByBookerId() throws Exception {
        String responseJson = "[ { \"id\": 23 }, { \"id\": 34 } ]";

        server.expect(once(), requestTo("http://localhost:9090/bookings?state=PAST"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "44"))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        var resp = bookingClient.getAllByBookerId(44L, BookingState.PAST);

        server.verify();
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        // тело — массив; просто проверим размер и id
        var list = mapper.convertValue(resp.getBody(), java.util.List.class);
        assertThat(list).hasSize(2);
        Map<?, ?> first = (Map<?, ?>) list.get(0);
        Map<?, ?> second = (Map<?, ?>) list.get(1);
        assertThat(first.get("id")).isEqualTo(23);
        assertThat(second.get("id")).isEqualTo(34);
    }

    @Test
    void testGetAllByItemOwnerId() throws Exception {
        String responseJson = "[ { \"id\": 10 }, { \"id\": 68 } ]";


        server.expect(once(), requestTo("http://localhost:9090/bookings/owner?state=FUTURE"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "55"))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        var resp = bookingClient.getAllByItemOwnerId(55L, BookingState.FUTURE);

        server.verify();
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        var list = mapper.convertValue(resp.getBody(), java.util.List.class);
        assertThat(list).hasSize(2);
    }

    @Test
    void testCreate_bodySerialization() throws Exception {
        NewBookingRequest req = new NewBookingRequest();
        req.setItemId(42L);
        req.setStart(LocalDateTime.of(2030, 1, 2, 3, 4, 5)); // фикс время, чтобы легко матчить
        req.setEnd(LocalDateTime.of(2030, 1, 3, 4, 5, 6));

        String responseJson = "{ \"id\": 999, \"status\": \"WAITING\" }";

        server.expect(once(), requestTo("http://localhost:9090/bookings"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", "123"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // Полное совпадение тела запроса (ISO-строки, поля)
                .andExpect(content().json("{"
                        + "\"itemId\":42,"
                        + "\"start\":\"2030-01-02T03:04:05\","
                        + "\"end\":\"2030-01-03T04:05:06\""
                        + "}"))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        var resp = bookingClient.create(123L, req);

        server.verify();
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    void testApproveOrReject_false_sendsNullDto() {
        String responseJson = "{ \"id\": 5, \"status\": \"REJECTED\" }";

        server.expect(once(), requestTo("http://localhost:9090/bookings/5?approved=false"))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(header("X-Sharer-User-Id", "20"))
                // PATCH-тело: три поля с null
                .andExpect(content().json("{\"start\":null,\"end\":null,\"itemId\":null}"))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        var resp = bookingClient.approveOrReject(20L, 5L, false);

        server.verify();
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        JsonNode body = mapper.valueToTree(resp.getBody());
        assertThat(body.get("status").asText()).isEqualTo("REJECTED");
    }

    @org.junit.jupiter.params.ParameterizedTest
    @org.junit.jupiter.params.provider.EnumSource(BookingState.class)
    void testGetAllByBookerId_stateMapping(BookingState state) {
        String url = "http://localhost:9090/bookings?state=" + state.name();
        String responseJson = "[]";

        server.expect(once(), requestTo(url))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "777"))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        var resp = bookingClient.getAllByBookerId(777L, state);

        server.verify();
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @org.junit.jupiter.params.ParameterizedTest
    @org.junit.jupiter.params.provider.EnumSource(BookingState.class)
    void testGetAllByItemOwnerId_stateMapping(BookingState state) {
        String url = "http://localhost:9090/bookings/owner?state=" + state.name();
        String responseJson = "[]";

        server.expect(once(), requestTo(url))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "888"))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        var resp = bookingClient.getAllByItemOwnerId(888L, state);

        server.verify();
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
    }
}
