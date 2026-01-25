package ru.kuzya.orderservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.time.LocalDateTime;

@Value
public class UserResponseDto {
    Long id;
    String name;
    @JsonProperty("fullname")
    String firstName;
    Integer age;
    String email;
    LocalDateTime createdAt;
}
