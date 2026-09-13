
package userservice.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import userservice.model.User;
import userservice.util.HibernateUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class UserDaoImplIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("userservice_test")
                    .withUsername("test")
                    .withPassword("test");

    private static SessionFactory sessionFactory;

    private UserDao userDao;

    @BeforeAll
    static void setUpDatabase() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.connection.url", postgres.getJdbcUrl());
        properties.put("hibernate.connection.username", postgres.getUsername());
        properties.put("hibernate.connection.password", postgres.getPassword());
        properties.put("hibernate.hbm2ddl.auto", "create");
        sessionFactory = HibernateUtil.buildSessionFactory(properties);
    }

    @BeforeEach
    void setUp() {
        userDao = new UserDaoImpl(sessionFactory);
        cleanDatabase();
    }

    private void cleanDatabase() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createMutationQuery("DELETE FROM User").executeUpdate();
            session.getTransaction().commit();
        }
    }

    @AfterAll
    static void tearDownDatabase() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @Test
    void save_shouldPersistUser() {
        User user = new User("Vasya", "vasya@example.com", 25);
        User savedUser = userDao.save(user);
        assertTrue(savedUser.getId() > 0);
        assertEquals("Vasya", savedUser.getName());
        assertEquals("vasya@example.com", savedUser.getEmail());
        assertEquals(25, savedUser.getAge());
        assertNotNull(savedUser.getCreatedAt());
    }

    @Test
    void findById_shouldReturnUserWhenExists() {
        User user = new User("Vasya", "vasya@example.com", 30);
        User savedUser = userDao.save(user);
        Optional<User> result = userDao.findById(savedUser.getId());
        assertTrue(result.isPresent());
        assertEquals("Vasya", result.get().getName());
        assertEquals("vasya@example.com", result.get().getEmail());
    }

    @Test
    void findById_shouldReturnEmptyWhenUserDoesNotExist() {
        Optional<User> result = userDao.findById(999999L);
        assertTrue(result.isEmpty());
    }

    @Test
    void findAll_shouldReturnAllUsers() {
        userDao.save(new User("Vasya", "vasya@example.com", 25));
        userDao.save(new User("Ivan", "ivan@example.com", 30));
        List<User> users = userDao.findAll();
        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(u -> u.getEmail().equals("vasya@example.com")));
        assertTrue(users.stream().anyMatch(u -> u.getEmail().equals("ivan@example.com")));
    }

    @Test
    void update_shouldChangeUserData() {
        User user = userDao.save(new User("Vasya", "vasya@example.com", 25));
        user.setName("Updated Vasya");
        user.setAge(26);
        userDao.update(user);
        Optional<User> result = userDao.findById(user.getId());
        assertTrue(result.isPresent());
        assertEquals("Updated Vasya", result.get().getName());
        assertEquals(26, result.get().getAge());
    }

    @Test
    void delete_shouldRemoveUser() {
        User user = userDao.save(new User("Vasya", "andrey@example.com", 25));
        Long userId = user.getId();
        userDao.delete(userId);
        assertTrue(userDao.findById(userId).isEmpty());
    }
}