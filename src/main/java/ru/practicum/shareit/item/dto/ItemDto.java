package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank(message = "Имя не может быть пустым")
    @Size(max = 255)
    private String name;
    @NotBlank(message = "Описание не может быть пустым")
    @Size(max = 2000)
    private String description;
    @NotNull(message = "Обязательно надо заполнить доступна вещь или нет")
    private Boolean available;
    private Long request;
    private List<CommentDto> comments;
}
