package ru.kuzya.orderservice.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;


public record CreateUserRequestDto(
        @NotBlank(message = "Требуется имя")
        String name,
        @NotBlank(message = "Требуется указать полное имя")
        @JsonAlias("fullName")
        String firstName,
        // ПРАВИЛЬНО: @Min и @Max для чисел
        @Min(value = 1, message = "Возраст должен быть больше 0")
        @Max(value = 150, message = "Возраст должен быть меньше 150")
        Integer age,
        @Email
        @Pattern(
                regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$",
                message = "Invalid email format. Example: user@example.com"
        )
        String email
) {
}

