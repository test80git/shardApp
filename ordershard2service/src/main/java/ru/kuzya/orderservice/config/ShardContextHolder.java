package ru.kuzya.orderservice.config;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ShardContextHolder {

    private static final ThreadLocal<Shard> CONTEXT = new ThreadLocal<>();

    public static void setShardKey(Shard shard) {
        log.info("=== ShardContextHolder: Setting shard key: {} ===", shard);
        log.info("Thread: {}", Thread.currentThread().getName());
        CONTEXT.set(shard);
    }

    public static Shard getShardKey() {
        Shard shard = CONTEXT.get();
        log.info("=== ShardContextHolder: Getting shard key: {} ===", shard);
        log.info("Thread: {}", Thread.currentThread().getName());
        return shard;
    }

    public static void clear() {
        log.info("=== ShardContextHolder: Clearing shard key ===");
        log.info("Thread: {}", Thread.currentThread().getName());
        CONTEXT.remove();
    }

}