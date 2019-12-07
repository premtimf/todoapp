package com.amigox.todo.Entity;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import com.amigox.todo.Util.DbUtil;

import java.util.List;

public class RoleDao implements BaseDao<Role> {
    private Session session;

    public RoleDao() {
        try {
            this.session = DbUtil.getSessionFactory().getCurrentSession();
        } catch (HibernateException e) {
            e.printStackTrace();
//            if (transaction != null) {
//                transaction.rollback();
//            }
        }
    }
    @Override
    public void save(Role role) {
        var transaction = session.beginTransaction();
        session.save(role);
        transaction.commit();
    }

    @Override
    public void update(Role role) {

    }

    @Override
    public void delete(Role role) {

    }

    @Override
    public List<Role> getAll() {
        return null;
    }
}
