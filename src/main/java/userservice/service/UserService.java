package userservice.service;

import java.util.List;

import userservice.model.User;

public interface UserService {
    User create(String name, int age, String email);

    void update(Long id, String name, int age, String email);

    User findById(Long id);

    void deleteById(Long id);

    List<User> findAll();
}