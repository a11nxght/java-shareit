package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateUserDto {
    @NotBlank(message = "The name cannot be empty.")
    private String name;
    @NotBlank(message = "The email cannot be empty.")
    @Email(message = "Invalid email format")
    private String email;
}
