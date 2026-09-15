package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateItemDto {
    Long id;
    @NotBlank(message = "The name cannot be empty.")
    String name;
    @NotBlank(message = "The description cannot be empty.")
    String description;
    @NotNull(message = "The available cannot be null.")
    Boolean available;
    Long requestId;
}
