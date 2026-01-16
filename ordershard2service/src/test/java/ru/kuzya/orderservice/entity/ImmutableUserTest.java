package ru.kuzya.orderservice.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ImmutableUserTest {

    @Test
    void test() {
        ImmutableUser tom = new ImmutableUser(2L, "Tom", "123@ya.com");
        ImmutableUser immutableUser = tom.withEmail("123@ya.com");

        System.out.println(tom);
        System.out.println(immutableUser);

        Assertions.assertEquals(tom, immutableUser);

    }

}