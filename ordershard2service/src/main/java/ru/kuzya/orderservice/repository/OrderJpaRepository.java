package ru.kuzya.orderservice.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kuzya.orderservice.entity.Order;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderJpaRepository extends JpaRepository<Order, UUID> {
    List<Order> findByUserId(Long userId);
}