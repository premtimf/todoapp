package com.amigox.todo.Entity;

import com.amigox.todo.Util.DbUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LabelDao {

    private Session session;

    public LabelDao() {
        try {
            this.session = DbUtil.getSessionFactory().getCurrentSession();
        } catch (HibernateException e) {
            e.printStackTrace();
//            if (transaction != null) {
//                transaction.rollback();
//            }
        }
    }

    public void save(Label label) {
        var transaction = session.beginTransaction();
        session.save(label);
        transaction.commit();
    }

    public List<Label> getAll() {
        List<Label> labelList = new ArrayList<>();
        Transaction transaction = null;

        try (var session = DbUtil.getSessionFactory().getCurrentSession()) {
            transaction = session.beginTransaction();
            labelList = session.createQuery("from Label l", Label.class).getResultList();
            transaction.commit();
        } catch (HibernateException e) {
            e.printStackTrace();
        }

        return labelList;
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
