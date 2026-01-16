package ru.kuzya.orderservice.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kuzya.orderservice.aspect.DetermineShard;
import ru.kuzya.orderservice.aspect.ShardKey;
import ru.kuzya.orderservice.config.Shard;
import ru.kuzya.orderservice.config.ShardContextHolder;
import ru.kuzya.orderservice.dto.OrderRequest;
import ru.kuzya.orderservice.dto.OrderResponse;
import ru.kuzya.orderservice.entity.Order;
import ru.kuzya.orderservice.repository.ShardOrderRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {


    private final ShardOrderService shardOrderService;
    private final ShardOrderRepository shardOrderRepository;


    // Этот метод только определяет шард, НЕ транзакционный
    @DetermineShard
    public OrderResponse createOrder(@ShardKey Long userId, OrderRequest request) {
        log.info("Creating order. UserId: {}, Current shard: {}",
                userId, ShardContextHolder.getShardKey());
        // Шард уже установлен в аспекте
        Order savedOrder = shardOrderService.saveOrderInTransaction(userId, request);
        return mapToResponse(savedOrder);
    }


    public OrderResponse getOrder(UUID id) {
        log.info("Getting order by ID: {}", id);
        return shardOrderService.findOrderById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    // Метод получения заказов пользователя
    @DetermineShard
    public List<OrderResponse> getOrdersByUser(@ShardKey Long userId) {
        log.info("=== OrderService.getOrdersByUser: UserId: {}, Current shard: {} ===",
                userId, ShardContextHolder.getShardKey());
        // Делегируем в сервис с транзакцией
        return shardOrderService.findOrdersByUserInTransaction(userId);
    }


    public void deleteOrder(@NonNull UUID id) {
        log.info("Deleting order: {}", id);
        int i = 0;
        int i1 = 0;
        // Пробуем SHARD_1
        i = shardOrderService.deleteOrderInShard(id, Shard.SHARD_1);
        // Пробуем SHARD_2
        if (i == 0) {
            i1 = shardOrderService.deleteOrderInShard(id, Shard.SHARD_2);
        }
        // Если не удалили ни из одного шарда
        if (i1 == 0 && i == 0) {
            throw new RuntimeException("Order not found with id: " + id);
        }
    }


    public List<OrderResponse> getAllOrders() {
        log.info("Getting all orders from all shards");

        List<Order> allOrders = new ArrayList<>();

        // Получаем из шарда 1
        List<Order> shard1Orders = shardOrderRepository.getOrdersFromShard(Shard.SHARD_1);
        log.info("Found {} orders in SHARD_1", shard1Orders.size());
        allOrders.addAll(shard1Orders);

        // Получаем из шарда 2
        List<Order> shard2Orders = shardOrderRepository.getOrdersFromShard(Shard.SHARD_2);
        log.info("Found {} orders in SHARD_2", shard2Orders.size());
        allOrders.addAll(shard2Orders);

        log.info("Total orders from all shards: {}", allOrders.size());

        return allOrders.stream()
                .map(this::mapToResponse)
                .sorted(Comparator.comparing(OrderResponse::userId))
                .toList();
    }


    private OrderResponse mapToResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getProductName(),
                order.getQuantity(),
                order.getCreatedAt()
        );
    }

}
