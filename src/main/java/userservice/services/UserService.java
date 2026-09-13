package userservice.services;

import userservice.dao.UserDao;
import userservice.model.User;

import java.util.List;
import java.util.Optional;

public class UserService {

    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User createUser(String name, String email, int age) {
        if (name == null || name.isBlank() || email == null || email.isBlank()) {
            throw new IllegalArgumentException("Name and email cannot be empty");
        }
        if (age <= 0) {
            throw new IllegalArgumentException("Age must be positive");
        }
        User user = new User(name, email, age);
        return userDao.save(user);
    }

    public Optional<User> getUserById(Long id) {
        return userDao.findById(id);
    }

    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    public User updateUser(Long id, String name, String email, int age) {
        Optional<User> existing = userDao.findById(id);
        if (existing.isEmpty()) {
            throw new RuntimeException("User not found with id: " + id);
        }
        User user = existing.get();
        user.setName(name);
        user.setEmail(email);
        user.setAge(age);
        return userDao.update(user);
    }

    public void deleteUser(Long id) {
        userDao.delete(id);
    }
}