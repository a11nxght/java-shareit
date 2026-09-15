package ru.practicum.shareit.exeptions;

public class ItemOwnershipException extends RuntimeException {
    public ItemOwnershipException(String message) {
        super(message);
    }
}
