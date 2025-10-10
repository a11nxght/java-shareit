package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class NewBookingRequest {
    @NotBlank(message = "Начало аренды не может быть пустым.")
    private LocalDateTime start;
    @NotBlank(message = "Окончание аренды не может быть пустым.")
    private LocalDateTime end;
    @NotNull(message = "Id вещи надо обязательно указывать.")
    private Long itemId;
}
