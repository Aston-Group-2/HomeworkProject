package userservice.dao;

import userservice.model.User;
import userservice.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class UserDaoImpl implements UserDao {

    private static final Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);
    private final SessionFactory sessionFactory;

    public UserDaoImpl() {
        this(HibernateUtil.getSessionFactory());
    }

    public UserDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public User save(User user) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            logger.info("User saved successfully: {}", user.getEmail());
            return user;
        } catch (Exception e) {
            if (transaction != null && transaction.getStatus().canRollback())
                transaction.rollback();
            logger.error("Error saving user: {}", e.getMessage());
            throw new RuntimeException("Failed to save user", e);
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            User user = session.get(User.class, id);
            transaction.commit();
            return Optional.ofNullable(user);
        } catch (Exception e) {
            if (transaction != null && transaction.getStatus().canRollback())
                transaction.rollback();
            logger.error("Error finding user by ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to find user", e);
        }
    }

    @Override
    public List<User> findAll() {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            List<User> users = session.createQuery("FROM User", User.class).getResultList();
            transaction.commit();
            return users;
        } catch (Exception e) {
            if (transaction != null && transaction.getStatus().canRollback())
                transaction.rollback();
            logger.error("Error finding all users: {}", e.getMessage());
            throw new RuntimeException("Failed to find all users", e);
        }
    }

    @Override
    public User update(User user) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            User mergedUser = session.merge(user);
            transaction.commit();
            logger.info("User updated successfully: {}", user.getId());
            return mergedUser;
        } catch (Exception e) {
            if (transaction != null && transaction.getStatus().canRollback())
                transaction.rollback();
            logger.error("Error updating user {}: {}", user.getId(), e.getMessage());
            throw new RuntimeException("Failed to update user", e);
        }
    }

    @Override
    public void delete(Long id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            User user = session.get(User.class, id);
            if (user != null)
                session.remove(user);
            else
                logger.warn("Attempted to delete non-existent user with ID: {}", id);
            transaction.commit();
            logger.info("User deleted successfully: {}", id);
        } catch (Exception e) {
            if (transaction != null && transaction.getStatus().canRollback())
                transaction.rollback();
            logger.error("Error deleting user {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to delete user", e);
        }
    }
}