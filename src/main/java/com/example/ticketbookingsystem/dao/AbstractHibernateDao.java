package com.example.ticketbookingsystem.dao;

import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.utils.HibernateUtil;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

@Slf4j
public abstract class AbstractHibernateDao<T> {
    private final Class<T> entityClass;

    public AbstractHibernateDao(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public List<T> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from " + entityClass.getName(), entityClass).list();
        } catch (HibernateException e) {
            log.error("Failed to get all {} entities", entityClass.getSimpleName(), e);
            throw new DaoCrudException(e);
        }
    }

    public Optional<T> findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(entityClass, id));
        } catch (HibernateException e) {
            log.error("Failed to get {} entity by id {}", entityClass.getSimpleName(), id, e);
            throw new DaoCrudException(e);
        }
    }

    public void save(T entity) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.save(entity);
            transaction.commit();
            log.info("Entity saved: {}", entity);
        } catch (HibernateException e) {
            log.error("Failed to save entity: {}", entity);
            throw new DaoCrudException(e);
        }
    }

    public void update(T entity) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.update(entity);
            transaction.commit();
            log.info("Entity updated: {}", entity);
        } catch (HibernateException e) {
            log.error("Failed to update entity: {}", entity);
            throw new DaoCrudException(e);
        }
    }

    public void delete(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            T entity = session.load(entityClass, id);
            if (entity != null) {
                session.delete(entity);
                log.info("Entity {} deleted with given id: {}", entityClass.getSimpleName(), id);
            }
            else {
                log.error("Failed to find entity to be deleted: {}", entity);
            }
            transaction.commit();
        } catch (HibernateException e) {
            log.error("Failed to delete entity: {}", entityClass.getSimpleName());
            throw new DaoCrudException(e);
        }
    }
}