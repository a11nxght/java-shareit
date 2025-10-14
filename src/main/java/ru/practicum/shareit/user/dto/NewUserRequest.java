package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewUserRequest {
    @Email(message = "Некорректный email")
    @NotBlank(message = "Некорректный email")
    @Size(max = 255)
    private String email;
    @NotBlank(message = "Имя не может быть пустым")
    @Size(max = 255)
    private String name;
}
