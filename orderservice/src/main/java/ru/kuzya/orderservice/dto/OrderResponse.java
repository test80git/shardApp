package ru.kuzya.orderservice.dto;
import java.time.LocalDateTime;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        Long userId,
        String productName,
        Integer quantity,
        LocalDateTime createdAt
) {}