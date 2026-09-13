
package userservice.util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

import java.util.Map;

public class HibernateUtil {

    private static final SessionFactory sessionFactory;

    static {
        try {
            sessionFactory = buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    private HibernateUtil() {
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static SessionFactory buildSessionFactory() {
        return buildSessionFactory(Map.of());
    }

    public static SessionFactory buildSessionFactory(Map<String, Object> properties) {
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().configure();
        builder.applySettings(properties);
        ServiceRegistry serviceRegistry = builder.build();
        try {
            return new MetadataSources(serviceRegistry)
                    .addAnnotatedClass(userservice.model.User.class)
                    .buildMetadata()
                    .buildSessionFactory();
        } catch (Throwable ex) {
            StandardServiceRegistryBuilder.destroy(serviceRegistry);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static void shutdown() {
        sessionFactory.close();
    }
}