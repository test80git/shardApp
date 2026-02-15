package ru.kuzya.orderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(
        basePackages = "ru.kuzya.orderservice.repository",
        entityManagerFactoryRef = "entityManagerFactory",  // Явно указываем бин
        transactionManagerRef = "transactionManager"       // Явно указываем бин
)
@EntityScan(basePackages = "ru.kuzya.orderservice.entity")
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }

}
