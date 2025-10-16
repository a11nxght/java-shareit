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
        when(userClient.deleteUser(anyLong())).thenReturn(ResponseEntity.ok(Map.of()));;

        mvc.perform(delete("/users/{id}", 9L))
                .andExpect(status().isOk());

        verify(userClient).deleteUser(9L);
        verifyNoMoreInteractions(userClient);
    }
}
