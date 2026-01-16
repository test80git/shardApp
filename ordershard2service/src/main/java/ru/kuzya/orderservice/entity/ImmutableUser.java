package ru.kuzya.orderservice.entity;

import lombok.Value;
import lombok.With;

@Value
@With
public class ImmutableUser {
    Long id;
    String name;
    String email;

    // Эквивалентно:
    // Все поля final + private
    // @Getter (но не @Setter)
    // @ToString
    // @EqualsAndHashCode
    // @AllArgsConstructor

    // Использование:
    // ImmutableUser user = new ImmutableUser(1L, "John", "john@email.com");
    // ImmutableUser updated = user.withName("John Updated");
    // user остается неизменным, updated - новая копия с измененным именем
}