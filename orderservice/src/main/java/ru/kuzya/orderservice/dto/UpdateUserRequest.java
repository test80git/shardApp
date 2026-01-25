package ru.kuzya.orderservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;


public record UpdateUserRequest (
        @NotBlank(message = "Product name is required")
        String name,
        @NotBlank(message = "Product name is required")
        String firstName,
        @Min(value = 0, message = "Должно быть больше 0")
        @Max(value = 150, message = "Должно быть меньше 150")
        Integer age,
        @Email
        @Pattern(
                regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$",
                message = "Invalid email format. Example: user@example.com"
        )
        String email
){}
