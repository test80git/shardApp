package ru.kuzya.orderservice.dto;

import java.util.UUID;

public record OrderResponseForUser(
        UUID id,
        String productName,
        Integer quantity
) {
}