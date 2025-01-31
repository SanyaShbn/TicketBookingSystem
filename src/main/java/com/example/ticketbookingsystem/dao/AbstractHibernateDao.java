//package com.example.ticketbookingsystem.dao;
//
//import com.example.ticketbookingsystem.exception.DaoCrudException;
//import com.example.ticketbookingsystem.utils.HibernateUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.hibernate.HibernateException;
//import org.hibernate.Session;
//import org.hibernate.SessionFactory;
//import org.hibernate.Transaction;
//
//import java.util.List;
//import java.util.Optional;
//
///**
// * Abstract class that provides generic CRUD operations for Hibernate.
// *
// * @param <T> The type of the entity.
// */
//@Slf4j
//public abstract class AbstractHibernateDao<T> {
//
//    private static final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
//
//    private final Class<T> entityClass;
//
//    public AbstractHibernateDao(Class<T> entityClass) {
//        this.entityClass = entityClass;
//    }
//
//    /**
//     * Finds all entities of provided class.
//     *
//     * @return A list of all entities.
//     * @throws DaoCrudException If there is an error during the retrieval process.
//     */
//    public List<T> findAll() {
//        try (Session session = sessionFactory.openSession()) {
//            return session.createQuery("from " + entityClass.getName(), entityClass).list();
//        } catch (HibernateException e) {
//            log.error("Failed to get all {} entities", entityClass.getSimpleName(), e);
//            throw new DaoCrudException(e);
//        }
//    }
//
//    /**
//     * Retrieves an entity by its ID.
//     *
//     * @param id The ID of the entity.
//     * @return An Optional containing the entity if found, or an empty Optional if not.
//     * @throws DaoCrudException If there is an error during the retrieval process.
//     */
//    public Optional<T> findById(Long id) {
//        try (Session session = sessionFactory.openSession()) {
//            return Optional.ofNullable(session.get(entityClass, id));
//        } catch (HibernateException e) {
//            log.error("Failed to get {} entity by id {}", entityClass.getSimpleName(), id, e);
//            throw new DaoCrudException(e);
//        }
//    }
//
//    /**
//     * Saves a new entity to the database.
//     *
//     * @param entity The entity to be saved.
//     * @throws DaoCrudException If there is an error during the save process.
//     */
//    public void save(T entity) {
//        Transaction transaction = null;
//        Session session = sessionFactory.openSession();
//        try {
//            transaction = session.beginTransaction();
//            session.persist(entity);
//            transaction.commit();
//            log.info("Entity {} saved", entityClass.getSimpleName());
//        } catch (HibernateException e) {
//            if(transaction != null){
//                transaction.rollback();
//            }
//            log.error("Failed to save {} entity", entityClass.getSimpleName());
//            throw new DaoCrudException(e);
//        } finally {
//            session.close();
//        }
//    }
//
//    /**
//     * Updates an existing entity in the database.
//     *
//     * @param entity The entity to be updated.
//     * @throws DaoCrudException If there is an error during the update process.
//     */
//    public void update(T entity) {
//        Transaction transaction = null;
//        Session session = sessionFactory.openSession();
//        try {
//            transaction = session.beginTransaction();
//            session.merge(entity);
//            transaction.commit();
//            log.info("Entity {} updated", entityClass.getSimpleName());
//        } catch (HibernateException e) {
//            if(transaction != null){
//                transaction.rollback();
//            }
//            log.error("Failed to update {} entity", entityClass.getSimpleName());
//            throw new DaoCrudException(e);
//        } finally {
//            session.close();
//        }
//    }
//
//    /**
//     * Deletes an entity by its ID.
//     *
//     * @param id The ID of the entity to be deleted.
//     * @throws DaoCrudException If there is an error during the delete process.
//     */
//    public void delete(Long id) {
//        Transaction transaction = null;
//        Session session = sessionFactory.openSession();
//        try {
//            transaction = session.beginTransaction();
//            T entity = session.find(entityClass, id);
//            if (entity != null) {
//                session.remove(entity);
//                transaction.commit();
//                log.info("Entity {} deleted with given id: {}", entityClass.getSimpleName(), id);
//            }
//            else {
//                transaction.rollback();
//                log.error("Failed to find entity to be deleted");
//            }
//        } catch (HibernateException e) {
//            if(transaction != null){
//                transaction.rollback();
//            }
//            log.error("Failed to delete entity: {}", entityClass.getSimpleName());
//            throw new DaoCrudException(e);
//        } finally {
//            session.close();
//        }
//    }
//}