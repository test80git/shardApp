package ru.kuzya.orderservice.service;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.kuzya.orderservice.entity.Person;

import java.util.Random;

@Component
@Slf4j

public class PersonService {
    private final SessionFactory sessionFactory;

    public PersonService(@Qualifier("sessionFactory") SessionFactory sessionFactory) {  // Это JPA репозиторий
        this.sessionFactory = sessionFactory;

    }

    // Метод через Hibernate SessionFactory
    public void savePerson(Person person) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            Long id = (Long) session.save(person);
            tx.commit();
            log.info("Person saved with id: {}", id);
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback(); // Откат при ошибке
            }
            log.error("Error saving person: {}", e.getMessage());
            throw new RuntimeException("Failed to save person", e); // Пробрасываем дальше
        }
    }

    public Person getPerson(Long id) {
        try (Session session = sessionFactory.openSession()) {
            // Правильный вызов: session.get(Class, id)
            Person person = session.get(Person.class, id);
            log.info("Person get: {}", person);
            return person;
        } catch (Exception e) {
            log.error("Error finding person with id {}: {}", id, e.getMessage());
            return null;
        }
    }

    /**
     * этот метод возвращает не реальный объект, а proxy: виртуальную заглушку.
     * при использовании метода load() не происходит проверка, есть ли такая запись в базе. Вместо этого Hibernate сразу создает proxy-объект с переданным ID и возвращает его.
     * вся работа с базой данных будет происходить при вызове методов proxy-объекта. Если ты попытаешься вызвать, например, метод getName(), тогда и произойдет первое обращение к базе.
     *
     * @param id
     * @return
     */
    public Person loadedPerson(Long id) {
        Random random = new Random();
        int i = random.nextInt(100);

        Transaction tx = null;  // Объявляем транзакцию

        try (Session session = sessionFactory.openSession()) {
            // 1. НАЧИНАЕМ ТРАНЗАКЦИЮ (обязательно для изменений!)
            tx = session.beginTransaction();
            log.info("Транзакция начата");

            // Проверяем, есть ли уже открытая сессия
//            log.info("Сессия создана? true");
//            log.info("Сессия та же что и OEIV? {}",
//                    session.isOpen() ? "открыта" : "закрыта");

            // Шаг 1: load() - запроса в БД еще нет!
            Person proxy = session.load(Person.class, id);
//            log.info("После load() - запроса еще не было, но {}", proxy);
//            log.info("Прокси? {}", proxy.getClass().getName().contains("HibernateProxy"));
//            log.info("Инициализирован? {}", Hibernate.isInitialized(proxy));

            // Шаг 2: getName()
//            String name = proxy.getName();
//            log.info("После getName() - инициализирован? {}", Hibernate.isInitialized(proxy));

            proxy.setName("Новое Имя "+i);
//            session.flush();
            log.info("Pausa");
            tx.commit();
            return proxy;
        } catch (Exception e) {
            log.error("Error finding person with id {}: {}", id, e.getMessage());
            return null;
        }
    }

    public Person findPerson(Long id) {
        try (Session session = sessionFactory.openSession()) {
            // Правильный вызов: session.get(Class, id)
            Person find = session.find(Person.class, id);
            log.info("Person loaded: {}", find);
            return find;
        } catch (Exception e) {
            log.error("Error finding person with id {}: {}", id, e.getMessage());
            return null;
        }
    }

    public void updatePerson(Person person) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.update(person);
            tx.commit();
            log.info("Person updated: {}", person);
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            log.error("Error updating person: {}", e.getMessage());
            throw new RuntimeException("Failed to update person", e);
        }
    }

    public void deletePerson(Long id) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            Person person = session.get(Person.class, id);
            if (person != null) {
                session.delete(person);
                log.info("Person deleted with id: {}", id);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            log.error("Error deleting person: {}", e.getMessage());
            throw new RuntimeException("Failed to delete person", e);
        }
    }
}