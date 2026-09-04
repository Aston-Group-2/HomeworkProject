package userservice.Dao;

import userservice.model.User;

import java.util.List;

public interface UserDao {
    User save(User user);
    User findById(Long id);
    List<User> findAll();
    User update(User user);
    void delete(Long id);


}
