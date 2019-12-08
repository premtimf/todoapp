package com.amigox.todo.Entity;

import com.amigox.todo.Util.DbUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDao implements BaseDao<User> {

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
        session.save(user);
        transaction.commit();
    }

    @Override
    public void update(User user) {

    }

    @Override
    public void delete(User user) {

    }

    @Override
    public List<User> getAll() {
        List<User> userList = new ArrayList<>();
        Transaction transaction = null;

        try (var session = DbUtil.getSessionFactory().getCurrentSession()) {
            transaction = session.beginTransaction();
            userList = session.createQuery("from User u", User.class).getResultList();
            transaction.commit();
        } catch (HibernateException e) {
            e.printStackTrace();
        }
        return userList;
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
