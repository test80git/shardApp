package ru.kuzya.orderservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true) // включать только помеченные
@EqualsAndHashCode(exclude = {"id"}) // исключить из equals/hashCode
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ToString.Include
    private UUID id;

    @Column(name = "user_id", nullable = false)
    @ToString.Include
    private Long userId;

    @Column(name = "product_name", nullable = false)
    @ToString.Include
    @Builder.Default // значение по умолчанию
    private String productName = "Имя продукта";

    @Column(nullable = false)
    @ToString.Include
    private Integer quantity;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }


    // Статический фабричный метод для удобства
    public static Order create(Long userId, String productName, Integer quantity) {
        return Order.builder()
                .userId(userId)
                .productName(productName)
                .quantity(quantity)
                .build();
    }

}
