package userservice.service;

import userservice.model.User;

import java.util.Collections;
import java.util.List;

import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import userservice.Dao.UserDao;
import userservice.Dao.UserDaoImpl;

public class UserServiceImpl implements UserService {
    private final UserDao userDao;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl() {
        this.userDao = new UserDaoImpl();
    }

    private void validateUserData(String name, int age, String email) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя пользователя не может быть пустым!");
        }

        if (age < 0 || age > 120) {
            throw new IllegalArgumentException("Возраст должен быть в диапазоне от 0 до 120 лет!");
        }

        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Некорректный формат email!");
        }
    }

    @Override
    public User create(String name, int age, String email) {
        validateUserData(name, age, email);

        try {
            logger.info("Создание пользователя {} {} {}", name, age, email);
            User user = new User(name, email, age);
            User createdUser = userDao.save(user);
            logger.info("Пользователь успешно создан с ID {}", createdUser.getId());
            return createdUser;
        } catch (HibernateException e) {
            logger.error("Ошибка Hibernate при создании пользоветеля: {}", e.getMessage());
            throw new HibernateException("Ошибка БД, не удалось создать пользователя!", e);
        }
    }

    @Override
    public void update(Long id, String name, int age, String email) {
        validateUserData(name, age, email);

        try {
            logger.info("Обновление пользователя с ID {}", id);
            User user = userDao.findById(id);
            if (user == null) {
                logger.warn("Пользователь с ID {} не найден для обновления", id);
                throw new IllegalArgumentException("Пользователь с ID " + id + " не найден!");
            }
            user.setName(name);
            user.setAge(age);
            user.setEmail(email);
            userDao.update(user);
            logger.info("Пользователь с ID {} успешно обновлен", id);
        } catch (HibernateException e) {
            logger.error("Ошибка Hibernate при обновлении пользователя с ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Не удалось обновить данные пользователя из-за ошибки БД", e);
        }
    }

    @Override
    public User findById(Long id) {
        try {
            return userDao.findById(id);
        } catch (HibernateException e) {
            logger.error("Ошибка поиска пользователя по ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Ошибка при чтении из базы данных", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        try {
            logger.info("Попытка удаления пользователя с ID: {}", id);
            userDao.delete(id);
            logger.info("Пользователь с ID: {} удален", id);

        } catch (Exception e) {
            logger.error("Ошибка Hibernate при удалении пользователя с ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Не удалось удалить пользователя из-за ошибки БД", e);
        }
    }

    @Override
    public List<User> findAll() {
        try {
            return userDao.findAll();
        } catch (HibernateException e) {
            logger.error("Ошибка при получении списка всех пользователей: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}
