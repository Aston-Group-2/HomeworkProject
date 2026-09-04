package userservice.Dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import userservice.model.User;
import userservice.util.HibernateUtil;

import org.hibernate.query.Query;
import java.util.List;

public class UserDaoImpl implements UserDao {

    @Override
    public User save(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil
                .getSessionFactory()
                .openSession()){
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            return user;
        }
        catch (Exception e){
            if(transaction != null){
                transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    public User findById(Long id) {
        try (Session session = HibernateUtil
                .getSessionFactory()
                .openSession()){
            return session.get(User.class, id);
        }
    }

    @Override
    public List<User> findAll() {
        try (Session session = HibernateUtil
                .getSessionFactory()
                .openSession()){
            Query<User> userQuery = session.createQuery("from User", User.class);

            return userQuery.getResultList();
        }
    }

    @Override
    public User update(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil
                .getSessionFactory()
                .openSession()){
            transaction = session.beginTransaction();
            User managedUser = session.merge(user);
            transaction.commit();
            return managedUser;
        }catch (Exception e){
            if (transaction != null){
                transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    public void delete(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil
                .getSessionFactory()
                .openSession()){
            transaction = session.beginTransaction();
            User user = session.get(User.class, id);
            if(user != null){
            session.remove(user);
            transaction.commit();
            }else {
                throw new RuntimeException("User with id " + id + " not found");
            }
        }catch (Exception e){
            if(transaction != null){
                transaction.rollback();
            }
            throw e;
        }
    }
}
