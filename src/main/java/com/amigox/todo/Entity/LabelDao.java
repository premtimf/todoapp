package com.amigox.todo.Entity;

import com.amigox.todo.Util.DbUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

public class LabelDao implements BaseDao<Label> {

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


    public void update(Label label) {
        var transaction = session.beginTransaction();
        session.update(label);
        transaction.commit();
    }

    public void delete(Label label) {
        var transaction = session.beginTransaction();
        session.delete(label);
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


}
