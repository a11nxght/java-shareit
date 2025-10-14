package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemRequestDto {
    private Long id;
    @NotBlank(message = "Описание не может быть пустым")
    @Size(max = 2000)
    private String description;
    private LocalDateTime created;
    private List<ItemShortDto> items;
}
