package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class NewBookingRequest {
    @NotNull(message = "начало аренды не может быть пустым.")
    @FutureOrPresent
    private LocalDateTime start;
    @NotNull(message = "Окончание аренды не может быть пустым.")
    @Future
    private LocalDateTime end;
    @NotNull(message = "Id вещи надо обязательно указывать.")
    private Long itemId;
}
