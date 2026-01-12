package ru.kuzya.orderservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kuzya.orderservice.dto.OrderRequest;
import ru.kuzya.orderservice.dto.OrderResponse;
import ru.kuzya.orderservice.entity.Order;
import ru.kuzya.orderservice.repository.OrderRepository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        log.info("Creating order for user: {}", request.userId());

        Order order = Order.create(
                request.userId(),
                request.productName(),
                request.quantity()
        );

        Order savedOrder = orderRepository.save(order);
        log.info("Order created with id: {}", savedOrder.getId());

        return mapToResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(UUID id) {
        log.info("Fetching order with id: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Order not found with id: {}", id);
                    return new RuntimeException("Order not found with id: " + id);
                });

        return mapToResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUser(Long userId) {
        log.info("Fetching orders for user: {}", userId);

        return orderRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public void deleteOrder(UUID id) {
        log.info("Deleting order with id: {}", id);

        if (!orderRepository.existsById(id)) {
            log.error("Order not found with id: {}", id);
            throw new RuntimeException("Order not found with id: " + id);
        }

        orderRepository.deleteById(id);
        log.info("Order deleted with id: {}", id);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        log.info("Fetching all orders");

        return orderRepository.findAll().stream()
                .map(this::mapToResponse)
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