package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NewUserRequest {
    @Email(message = "Некорректный email")
    @NotBlank(message = "Некорректный email")
    private String email;
    private String name;
}
