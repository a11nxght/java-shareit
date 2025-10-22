package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exceptions.DuplicatedDataException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserService userService;

    @Test
    void create() throws Exception {
        NewUserRequest newUser = new NewUserRequest();
        newUser.setName("Ivan");
        newUser.setEmail("ivan@ya.ru");

        UserDto resp = new UserDto();
        resp.setId(1L);
        resp.setName("Ivan");
        resp.setEmail("ivan@ya.ru");

        when(userService.create(any(NewUserRequest.class))).thenReturn(resp);

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Ivan"))
                .andExpect(jsonPath("$.email").value("ivan@ya.ru"));
    }

    @Test
    void update() throws Exception {
        UpdateUserRequest updateUser = new UpdateUserRequest();
        updateUser.setName("Petr");
        updateUser.setEmail("petr@ya.ru");

        UserDto resp = new UserDto();
        resp.setId(5L);
        resp.setName("Petr");
        resp.setEmail("petr@ya.ru");

        when(userService.update(any(UpdateUserRequest.class), eq(5L))).thenReturn(resp);

        mvc.perform(patch("/users/{userId}", 5L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("Petr"))
                .andExpect(jsonPath("$.email").value("petr@ya.ru"));
    }

    @Test
    void findAll() throws Exception {
        UserDto u1 = new UserDto();
        u1.setId(1L);
        u1.setName("A");
        u1.setEmail("a@ya.ru");
        UserDto u2 = new UserDto();
        u2.setId(2L);
        u2.setName("B");
        u2.setEmail("b@ya.ru");

        when(userService.findAll()).thenReturn(List.of(u1, u2));

        mvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("A"))
                .andExpect(jsonPath("$[0].email").value("a@ya.ru"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("B"))
                .andExpect(jsonPath("$[1].email").value("b@ya.ru"));
    }

    @Test
    void getUser() throws Exception {
        UserDto resp = new UserDto();
        resp.setId(9L);
        resp.setName("Ivan");
        resp.setEmail("asdas@ya.ru");

        when(userService.getUser(9L)).thenReturn(resp);

        mvc.perform(get("/users/{id}", 9L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(9))
                .andExpect(jsonPath("$.name").value("Ivan"))
                .andExpect(jsonPath("$.email").value("asdas@ya.ru"));
    }

    @Test
    void deleteUser() throws Exception {
        doNothing().when(userService).deleteUser(12L);

        mvc.perform(delete("/users/{id}", 12L))
                .andExpect(status().isOk());
    }

    @Test
    void create_duplicatedEmail_returns409() throws Exception {
        NewUserRequest body = new NewUserRequest();
        body.setName("A");
        body.setEmail("dup@a");

        when(userService.create(any(NewUserRequest.class)))
                .thenThrow(new DuplicatedDataException("email"));

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().isConflict());
    }

    @Test
    void update_notFound_returns404() throws Exception {
        UpdateUserRequest patch = new UpdateUserRequest();
        patch.setName("New");

        when(userService.update(any(UpdateUserRequest.class), eq(999L)))
                .thenThrow(new NotFoundException("user"));

        mvc.perform(patch("/users/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(patch)))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_duplicatedEmail_returns409() throws Exception {
        UpdateUserRequest patch = new UpdateUserRequest();
        patch.setEmail("busy@a");

        when(userService.update(any(UpdateUserRequest.class), eq(5L)))
                .thenThrow(new DuplicatedDataException("dup"));

        mvc.perform(patch("/users/{id}", 5L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(patch)))
                .andExpect(status().isConflict());
    }

    @Test
    void getUser_notFound_returns404() throws Exception {
        when(userService.getUser(777L)).thenThrow(new NotFoundException("user"));
        mvc.perform(get("/users/{id}", 777L))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_notFound_returns404() throws Exception {
        doThrow(new NotFoundException("user"))
                .when(userService).deleteUser(321L);

        mvc.perform(delete("/users/{id}", 321L))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAll_empty_returnsEmptyArray() throws Exception {
        when(userService.findAll()).thenReturn(List.of());
        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

}