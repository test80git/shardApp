package ru.kuzya.orderservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.kuzya.orderservice.config.Shard;
import ru.kuzya.orderservice.config.ShardContextHolder;
import ru.kuzya.orderservice.dto.OrderRequest;
import ru.kuzya.orderservice.dto.OrderResponse;
import ru.kuzya.orderservice.entity.Order;
import ru.kuzya.orderservice.repository.OrderJpaRepository;
import ru.kuzya.orderservice.repository.ShardOrderRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShardOrderService {

    private final OrderJpaRepository orderJpaRepository;
    private final ShardOrderRepository shardOrderRepository;


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Order saveOrderInTransaction(Long userId, OrderRequest request) {
        Order order = Order.create(userId, request.productName(), request.quantity());
        Order savedOrder = orderJpaRepository.save(order);
        log.info("Order saved. ID: {}, Shard used: {}",
                savedOrder.getId(), ShardContextHolder.getShardKey());
        return savedOrder;
    }

    @Transactional(readOnly = true)
    public Optional<Order> findOrderById(UUID id) {
              // Пробуем через транзакционный сервис в SHARD_1
        log.info("Trying SHARD_1...");
        ShardContextHolder.setShardKey(Shard.SHARD_1);
        try {
            List<Order> orders = shardOrderRepository.getOrdersFromShardWithFilter(id);
            if (!orders.isEmpty()) {
                log.info("Found order {} in SHARD_1", id);
                return Optional.of(orders.get(0));
            }
        } finally {
            ShardContextHolder.clear();
        }

        // Пробуем через транзакционный сервис в SHARD_2
        log.info("Trying SHARD_2...");
        ShardContextHolder.setShardKey(Shard.SHARD_2);
        try {
            List<Order> orders = shardOrderRepository.getOrdersFromShardWithFilter(id);
            if (!orders.isEmpty()) {
                log.info("Found order {} in SHARD_2", id);
                return Optional.of(orders.get(0));
            }
        } finally {
            ShardContextHolder.clear();
        }

        log.info("Order {} not found in any shard", id);
        return Optional.empty();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> findOrdersByUserInTransaction(Long userId) {
        // Здесь шард уже установлен, транзакция открывается ПОСЛЕ
        List<Order> orders = orderJpaRepository.findByUserId(userId);
        log.info("=== Found {} orders in transaction ===", orders.size());

        return orders.stream()
                .map(order -> new OrderResponse(
                        order.getId(),
                        order.getUserId(),
                        order.getProductName(),
                        order.getQuantity(),
                        order.getCreatedAt()
                ))
                .toList();
    }

    public int deleteOrderInShard(UUID id, Shard shard) {
        log.info("Deleting order {} from shard {}", id, shard);
        return shardOrderRepository.deleteOrdersFromShard(id, shard);
    }

}
