package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceImplTest {

    @Autowired
    UserService userService;

    @Test
    void testCreateUpdateGetUser() {
        NewUserRequest newUserRequest = createNewUser("qwe@ya.ru", "Иван Иванович");
        UserDto createdUser = userService.create(newUserRequest);
        assertThat(createdUser.getId(), notNullValue());
        assertThat(createdUser.getName(), equalTo(newUserRequest.getName()));
        assertThat(createdUser.getEmail(), equalTo(newUserRequest.getEmail()));

        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setName("Петр Петрович");
        UserDto updatedUser = userService.update(updateUserRequest, createdUser.getId());
        assertThat(updatedUser.getName(), equalTo(updateUserRequest.getName()));

        UserDto getUserDto = userService.getUser(createdUser.getId());
        assertThat(getUserDto.getName(), equalTo(updatedUser.getName()));
        assertThat(getUserDto.getEmail(), equalTo(updatedUser.getEmail()));
    }

    @Test
    void testFindAllAndDelete () {
        NewUserRequest newUserRequest1 = createNewUser("qwe@ya.ru", "Иван Иванович");
        NewUserRequest newUserRequest2 = createNewUser("asd@ya.ru", "Петр Петрович");
        UserDto createdUser1 = userService.create(newUserRequest1);
        userService.create(newUserRequest2);
        List<UserDto> users = userService.findAll();
        assertThat(users.getFirst().getName(), equalTo(newUserRequest1.getName()));
        assertThat(users.getLast().getEmail(), equalTo(newUserRequest2.getEmail()));

        userService.deleteUser(createdUser1.getId());
        List<UserDto> users2 = userService.findAll();
        assertThat(users2.getFirst().getName(), equalTo(newUserRequest2.getName()));

    }

    private NewUserRequest createNewUser(String email, String name) {
        NewUserRequest newUserRequest = new NewUserRequest();
        newUserRequest.setEmail(email);
        newUserRequest.setName(name);
        return newUserRequest;
    }
}