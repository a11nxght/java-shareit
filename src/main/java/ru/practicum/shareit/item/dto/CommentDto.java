package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CommentDto {
    private Long id;
    @NotBlank(message = "текст не может быть пустым")
    @Size(max = 2000)
    private String text;
    private String authorName;
    private LocalDateTime created;
}
