package com.example.ticketbookingsystem.dao;

import com.example.ticketbookingsystem.entity.User;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.utils.HibernateUtil;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Optional;

@Slf4j
public class UserDao{
    private static final UserDao INSTANCE = new UserDao();

    public static UserDao getInstance(){
        return INSTANCE;
    }
    private UserDao() {}

    public Optional<User> findByEmail(String email){
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            User user = session.createQuery("from User where email = :email", User.class)
                    .setParameter("email", email)
                    .uniqueResult();
            return Optional.ofNullable(user);
        } catch (HibernateException e) {
            log.error("Failed to find user with such email: {}", email, e);
            throw new DaoCrudException(e);
        }
    }

    public void registerUser(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.save(user);
            transaction.commit();
            log.info("User saved: {}", user);
        } catch (HibernateException e) {
            log.error("Failed to save user: {}", user);
            throw new DaoCrudException(e);
        }
    }
}
