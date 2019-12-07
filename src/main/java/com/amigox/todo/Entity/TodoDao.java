package com.amigox.todo.Entity;

import com.amigox.todo.Util.DbUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.NoResultException;
import java.util.*;

public class TodoDao {

    private Session session;

    public TodoDao() {
        try {
            this.session = DbUtil.getSessionFactory().getCurrentSession();
        } catch (HibernateException e) {
            e.printStackTrace();
//            if (transaction != null) {
//                transaction.rollback();
//            }
        }
    }

    public List<Todo> getAll() {
        List<Todo> todoList = new ArrayList<>();
        Transaction transaction = null;

        try (var session = DbUtil.getSessionFactory().getCurrentSession()) {
            transaction = session.beginTransaction();
            todoList = session.createQuery("from Todo t", Todo.class).getResultList();
            transaction.commit();
        } catch (HibernateException e) {
            e.printStackTrace();
        }

        return todoList;
    }

    public Optional<Todo> findByUsername(Integer id) {
        Optional<Todo> todo = Optional.empty();
        Transaction transaction;

        try (var session = DbUtil.getSessionFactory().getCurrentSession()) {
            transaction = session.beginTransaction();
            todo = session.createQuery("from Todo t where t.id = :id", Todo.class)
                    .setParameter("id", id)
                    .setMaxResults(1)
                    .getResultList()
                    .stream()
                    .findFirst();
            transaction.commit();
        } catch (HibernateException e) {
            e.printStackTrace();
        }

        return todo;
    }


    public List<Todo> getAllByUsername(String username) {



        List<Todo> todoList = new ArrayList<>();
        Transaction transaction = null;

        try (var session = DbUtil.getSessionFactory().getCurrentSession()) {
            transaction = session.beginTransaction();
            todoList = session.createQuery("from Todo t where t.user.username = :username", Todo.class)
                    .setParameter("username", username).getResultList();
            transaction.commit();
        } catch (HibernateException e) {
            e.printStackTrace();
        }

        return todoList;
    }






    public void save(Todo todo) {
        var transaction = session.beginTransaction();
        session.save(todo);
        transaction.commit();
    }

    public void update(Todo todo){
        var transaction = session.beginTransaction();
        session.update(todo);
        transaction.commit();
    }

    public static <T> Optional<T> findOrEmpty(final DaoRetriever<T> retriever) {
        try {
            return Optional.of(retriever.retrieve());
        } catch (NoResultException ex) {
            //log
        }
        return Optional.empty();
    }
}
