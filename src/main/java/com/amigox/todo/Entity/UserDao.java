package com.amigox.todo.Entity;

import com.amigox.todo.Util.DbUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.Optional;

public class UserDao {

    private Session session;

    public UserDao() {
        try {
            this.session = DbUtil.getSessionFactory().getCurrentSession();
        } catch (HibernateException e) {
            e.printStackTrace();
//            if (transaction != null) {
//                transaction.rollback();
//            }
        }
    }

    public void save(User user) {
        var transaction = session.beginTransaction();
        session.persist(user);
        transaction.commit();
    }

    public User myUser(String username) {
        Transaction transaction;
        User user = null;

        try (var session = DbUtil.getSessionFactory().getCurrentSession()) {
            transaction = session.beginTransaction();
            user = session.createQuery("from User a where a.username = :username", User.class)
                    .setParameter("username", username)
                    .setMaxResults(1).uniqueResult();
            transaction.commit();
        } catch (HibernateException e) {
            e.printStackTrace();
        }

        return user;
    }

    public Optional<User> findByUsername(String username) {
        Optional<User> user = Optional.empty();
        Transaction transaction;

        try (var session = DbUtil.getSessionFactory().getCurrentSession()) {
            transaction = session.beginTransaction();
            user = session.createQuery("from User a where a.username = :username", User.class)
                    .setParameter("username", username)
                    .setMaxResults(1)
                    .getResultList()
                    .stream()
                    .findFirst();
            transaction.commit();
        } catch (HibernateException e) {
            e.printStackTrace();
        }

        return user;
    }




    public Optional<User> findByEmail(String email) {

        Optional<User> user = Optional.empty();
        Transaction transaction;

        try (var session = DbUtil.getSessionFactory().getCurrentSession()) {
            transaction = session.beginTransaction();
            user = session.createQuery("from User a where a.email = :email", User.class)
                    .setParameter("email", email)
                    .setMaxResults(1)
                    .getResultList()
                    .stream()
                    .findFirst();
            transaction.commit();
        } catch (HibernateException e) {
            e.printStackTrace();
        }

        return user;

    }


}
