package ru.kuzya.orderservice.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@SpringBootTest
@Transactional
class OrderTest {
    @PersistenceContext
    private EntityManager em;

    @Deprecated
    @Test
    void orderDetachTest() {
        Order order = new Order(/*UUID.fromString("0d7f6592-f583-46b2-832f-64d43abc2fab"),
                555L,
                "Mouse",
                2,
                LocalDateTime.now()*/);
        em.persist(order);

        Order firstOrder = em.find(Order.class, order.getId());

        em.detach(firstOrder);

        Order secondOrder = em.find(Order.class, order.getId());

        Assertions.assertEquals(firstOrder, secondOrder);

    }

}
