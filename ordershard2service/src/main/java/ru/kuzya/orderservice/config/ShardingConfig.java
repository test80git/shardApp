package ru.kuzya.orderservice.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class ShardingConfig {

    @Value("${spring.shard.datasource.shard1.jdbc-url}")
    private String shard1Url;

    @Value("${spring.shard.datasource.shard1.username}")
    private String shard1Username;

    @Value("${spring.shard.datasource.shard1.password}")
    private String shard1Password;

    @Value("${spring.shard.datasource.shard2.jdbc-url}")
    private String shard2Url;

    @Value("${spring.shard.datasource.shard2.username}")
    private String shard2Username;

    @Value("${spring.shard.datasource.shard2.password}")
    private String shard2Password;


    @Bean(name = "shard1DataSource")
    public DataSource shard1DataSource() {
        log.info("Creating shard1DataSource with URL: {}", shard1Url);

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(shard1Url);
        dataSource.setUsername(shard1Username);
        dataSource.setPassword(shard1Password);
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setMaximumPoolSize(10);
        dataSource.setMinimumIdle(5);
        dataSource.setConnectionTestQuery("SELECT 1");
        dataSource.setPoolName("HikariPool-Shard1");

        return dataSource;
    }

    @Bean(name = "shard2DataSource")
    public DataSource shard2DataSource() {
        log.info("Creating shard2DataSource with URL: {}", shard2Url);

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(shard2Url);
        dataSource.setUsername(shard2Username);
        dataSource.setPassword(shard2Password);
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setMaximumPoolSize(10);
        dataSource.setMinimumIdle(5);
        dataSource.setConnectionTestQuery("SELECT 1");
        dataSource.setPoolName("HikariPool-Shard2");

        return dataSource;
    }

    @Primary
    @Bean(name = "routingDataSource")
    public DataSource routingDataSource() {
        DataSource shard1 = shard1DataSource();
        DataSource shard2 = shard2DataSource();

        log.info("Creating RoutingDataSource with shards:");
        log.info("  SHARD_1: {}", shard1Url);
        log.info("  SHARD_2: {}", shard2Url);

        AbstractRoutingDataSource routingDataSource =
                new AbstractRoutingDataSource() {
                    private boolean connectionResetRequested = false;

                    @Override
                    protected Object determineCurrentLookupKey() {
                        Shard shard = ShardContextHolder.getShardKey();
                        log.info("RoutingDataSource: Current shard key = {}", shard);
                        // Если запрошен сброс соединения
                        if (connectionResetRequested) {
                            connectionResetRequested = false;
                            log.info("Connection reset requested - creating new connection");
                        }

                        return shard;
                    }

                    public void requestConnectionReset() {
                        this.connectionResetRequested = true;
                    }
                };

        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put(Shard.SHARD_1, shard1);
        dataSourceMap.put(Shard.SHARD_2, shard2);

        routingDataSource.setDefaultTargetDataSource(shard1);
        routingDataSource.setTargetDataSources(dataSourceMap);
        routingDataSource.afterPropertiesSet();

        return routingDataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(@Qualifier("routingDataSource") DataSource routingDataSource) {
        return new JdbcTemplate(routingDataSource);
    }

}
