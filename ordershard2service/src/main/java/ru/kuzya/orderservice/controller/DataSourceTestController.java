package ru.kuzya.orderservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.kuzya.orderservice.config.Shard;
import ru.kuzya.orderservice.config.ShardContextHolder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class DataSourceTestController {

    private final DataSource routingDataSource;
    private final DataSource shard1DataSource;
    private final DataSource shard2DataSource;

    @GetMapping("/connection")
    public String testConnection(@RequestParam Long userId) throws SQLException {
        Shard shard = (userId % 2 == 0) ? Shard.SHARD_1 : Shard.SHARD_2;
        ShardContextHolder.setShardKey(shard);

        try (Connection conn = routingDataSource.getConnection()) {
            String url = conn.getMetaData().getURL();
            log.info("Connection URL for userId {}: {}", userId, url);
            return "Connected to: " + url;
        } finally {
            ShardContextHolder.clear();
        }
    }

    @GetMapping("/shard1")
    public String testShard1() throws SQLException {
        ShardContextHolder.setShardKey(Shard.SHARD_1);
        try (Connection conn = shard1DataSource.getConnection()) {
            String url = conn.getMetaData().getURL();
            log.info("Shard1 URL: {}", url);
            return "Shard1: " + url;
        }
    }

    @GetMapping("/shard2")
    public String testShard2() throws SQLException {
        ShardContextHolder.setShardKey(Shard.SHARD_2);
        try (Connection conn = shard2DataSource.getConnection()) {
            String url = conn.getMetaData().getURL();
            log.info("Shard2 URL: {}", url);
            return "Shard2: " + url;
        }
    }
}