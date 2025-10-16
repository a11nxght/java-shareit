package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserClient userClient;

    @Test
    void testCreate() throws Exception {
        NewUserRequest newUserRequest = new NewUserRequest();
        newUserRequest.setName("Ivan");
        newUserRequest.setEmail("vanya@ya.ru");

        when(userClient.create(any(NewUserRequest.class)))
                .thenReturn(ResponseEntity.ok(Map.of()));

        mvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(newUserRequest)))
                .andExpect(status().isOk());

        verify(userClient).create(any(NewUserRequest.class));
        verifyNoMoreInteractions(userClient);
    }

    @Test
    void testCreateValidationFail() throws Exception {
        NewUserRequest invalid = new NewUserRequest();

        mvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userClient);
    }

    @Test
    void testUpdate() throws Exception {
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setName("Petr");
        updateUserRequest.setEmail("petr@ya.ru");

        when(userClient.update(anyLong(), any(UpdateUserRequest.class)))
                .thenReturn(ResponseEntity.ok(Map.of()));

        mvc.perform(patch("/users/{userId}", 5L)
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isOk());

        verify(userClient).update(anyLong(), any(UpdateUserRequest.class));
        verifyNoMoreInteractions(userClient);
    }

    @Test
    void testFindAll() throws Exception {
        when(userClient.findAll())
                .thenReturn(ResponseEntity.ok(List.of(Map.of(), Map.of())));

        mvc.perform(get("/users"))
                .andExpect(status().isOk());

        verify(userClient).findAll();
        verifyNoMoreInteractions(userClient);
    }

    @Test
    void testGetUser() throws Exception {
        when(userClient.getUser(anyLong()))
                .thenReturn(ResponseEntity.ok(Map.of()));

        mvc.perform(get("/users/{id}", 77L))
                .andExpect(status().isOk());

        verify(userClient).getUser(77L);
        verifyNoMoreInteractions(userClient);
    }

    @Test
    void testDeleteUser() throws Exception {
        when(userClient.deleteUser(anyLong())).thenReturn(ResponseEntity.ok(Map.of()));
        ;

        mvc.perform(delete("/users/{id}", 9L))
                .andExpect(status().isOk());

        verify(userClient).deleteUser(9L);
        verifyNoMoreInteractions(userClient);
    }

    @Test
    void testCreate_invalidEmail_400() throws Exception {
        NewUserRequest invalid = new NewUserRequest();
        invalid.setName("Ivan");
        invalid.setEmail("bad-email"); // нет @

        mvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userClient);
    }

    @Test
    void testCreate_blankName_400() throws Exception {
        NewUserRequest invalid = new NewUserRequest();
        invalid.setName(""); // пусто
        invalid.setEmail("vanya@ya.ru");

        mvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userClient);
    }

    @Test
    void testUpdate_onlyName_ok() throws Exception {
        UpdateUserRequest req = new UpdateUserRequest();
        req.setName("OnlyName");

        when(userClient.update(eq(5L), any(UpdateUserRequest.class)))
                .thenReturn(ResponseEntity.ok(Map.of("id", 5, "name", "OnlyName")));

        mvc.perform(patch("/users/{userId}", 5L)
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        verify(userClient).update(eq(5L), any(UpdateUserRequest.class));
        verifyNoMoreInteractions(userClient);
    }

    @Test
    void testUpdate_onlyEmail_ok() throws Exception {
        UpdateUserRequest req = new UpdateUserRequest();
        req.setEmail("only@mail.com");

        when(userClient.update(eq(6L), any(UpdateUserRequest.class)))
                .thenReturn(ResponseEntity.ok(Map.of("id", 6, "email", "only@mail.com")));

        mvc.perform(patch("/users/{userId}", 6L)
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        verify(userClient).update(eq(6L), any(UpdateUserRequest.class));
        verifyNoMoreInteractions(userClient);
    }

    @Test
    void testGetUser_notFound_404() throws Exception {
        when(userClient.getUser(777L)).thenReturn(ResponseEntity.status(404).build());

        mvc.perform(get("/users/{id}", 777L))
                .andExpect(status().isNotFound());

        verify(userClient).getUser(777L);
        verifyNoMoreInteractions(userClient);
    }

    @Test
    void testDeleteUser_noContent_204() throws Exception {
        when(userClient.deleteUser(9L)).thenReturn(ResponseEntity.noContent().build());

        mvc.perform(delete("/users/{id}", 9L))
                .andExpect(status().isOk());

        verify(userClient).deleteUser(9L);
        verifyNoMoreInteractions(userClient);
    }

    @Test
    void testCreate_responseBodyPassThrough_ok() throws Exception {
        NewUserRequest req = new NewUserRequest();
        req.setName("Ivan");
        req.setEmail("vanya@ya.ru");

        when(userClient.create(any(NewUserRequest.class)))
                .thenReturn(ResponseEntity.ok(Map.of("id", 101, "name", "Ivan")));

        mvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers
                        .content().json("{\"id\":101,\"name\":\"Ivan\"}"));

        verify(userClient).create(any(NewUserRequest.class));
        verifyNoMoreInteractions(userClient);
    }
}
