package ru.kuzya.orderservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.kuzya.orderservice.config.Shard;
import ru.kuzya.orderservice.config.ShardContextHolder;
import ru.kuzya.orderservice.entity.Order;

import java.util.List;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ShardOrderRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<Order> getOrdersFromShard(Shard shard) {
        // Устанавливаем шард
        ShardContextHolder.setShardKey(shard);
        try {
            log.info("Querying shard: {}", shard);
            String sql = "SELECT id, user_id, product_name, quantity, created_at FROM orders";

            return jdbcTemplate.query(sql, (rs, rowNum) ->
                    Order.builder()
                            .id(UUID.fromString(rs.getString("id")))
                            .userId(rs.getLong("user_id"))
                            .productName(rs.getString("product_name"))
                            .quantity(rs.getInt("quantity"))
                            .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                            .build()
            );
        } finally {
            ShardContextHolder.clear();
        }
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public List<Order> getOrdersFromShardWithFilter(UUID id) {
        log.info("Querying shard with filter for ID: {}", id);

        String sql = "SELECT id, user_id, product_name, quantity, created_at " +
                     "FROM orders WHERE id = ?";

        return jdbcTemplate.query(sql,
                (rs, rowNum) -> Order.builder()
                        .id((UUID) rs.getObject("id"))
                        .userId(rs.getLong("user_id"))
                        .productName(rs.getString("product_name"))
                        .quantity(rs.getInt("quantity"))
                        .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                        .build(),
                id);
    }


    public int deleteOrdersFromShard(UUID id, Shard shard) {
        int deleted=0;
        ShardContextHolder.setShardKey(shard);
        try {
            String sql = "DELETE FROM orders WHERE id = ?";
            deleted = jdbcTemplate.update(sql, id);
            log.info("Delete affected {} rows in shard {}", deleted, shard);

            if (deleted > 0) {
                log.info("Successfully deleted order {} from shard {}", id, shard);
                return deleted;
            } else {
                log.warn("No order deleted with id {} from shard {}", id, shard);
            }
        } finally {
            ShardContextHolder.clear();
        }
        return deleted;
    }
}
