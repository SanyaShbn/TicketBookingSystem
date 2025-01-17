package com.example.ticketbookingsystem.dao;

import com.example.ticketbookingsystem.entity.Ticket;
import com.example.ticketbookingsystem.entity.TicketStatus;
import com.example.ticketbookingsystem.entity.UserCart;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.utils.HibernateUtil;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

/**
 * DAO class for managing {@link UserCart} entities.
 */
@Slf4j
public class UserCartDao extends AbstractHibernateDao<UserCart>{
    private static final UserCartDao INSTANCE = new UserCartDao();

    private UserCartDao(){
        super(UserCart.class);
    }

    /**
     * Returns the singleton instance of {@link UserCartDao}.
     *
     * @return the singleton instance
     */
    public static UserCartDao getInstance(){
        return INSTANCE;
    }

    /**
     * Saves the record with ticket reservation into user cart and updates the ticket status to 'RESERVED'.
     *
     * @param userCart the user cart entity to save
     */
    public void save(UserCart userCart) {
        Transaction transaction = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            transaction = session.beginTransaction();

            Ticket ticket = session.get(Ticket.class, userCart.getId().getTicketId());
            if (ticket != null) {
                ticket.setStatus(TicketStatus.RESERVED);
                session.update(ticket);
            }

            session.save(userCart);

            transaction.commit();
            log.info("Record saved into user card: {}", userCart);
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            log.error("Failed to save record into user card: {}", userCart);
            throw new DaoCrudException(e);
        } finally {
            session.close();
        }
    }

    /**
     * Deletes single record with ticket reservation from user cart and updates the ticket status to 'AVAILABLE'.
     *
     * @param userCart the user cart entity to delete
     */
    public void delete(UserCart userCart){
        Transaction transaction = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            transaction = session.beginTransaction();

            Ticket ticket = session.get(Ticket.class, userCart.getId().getTicketId());
            if (ticket != null) {
                ticket.setStatus(TicketStatus.AVAILABLE);
                session.update(ticket);
            }

            session.delete(userCart);

            transaction.commit();
            log.info("Record removed from user card: {}", userCart);
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            log.error("Failed to remove record from user card: {}", userCart);
            throw new DaoCrudException(e);
        } finally {
            session.close();
        }
    }

    /**
     * Deletes all the records from user cart and updates the status of all tickets to 'AVAILABLE'.
     *
     * @param userId the ID of the user
     */
    public void clearCart(Long userId) {
        Transaction transaction = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            transaction = session.beginTransaction();

            updateTicketStatus(session, userId);
            deleteUserCart(session, userId);

            transaction.commit();
            log.info("Removed all records from user cart with user ID: {}", userId);
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            log.error("Failed to remove all records from user cart with user ID: {}", userId);
            throw new DaoCrudException(e);
        } finally {
            session.close();
        }
    }

    private void updateTicketStatus(Session session, Long userId) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaUpdate<Ticket> update = cb.createCriteriaUpdate(Ticket.class);
        Root<Ticket> ticketRoot = update.from(Ticket.class);

        Subquery<Long> subquery = update.subquery(Long.class);
        Root<UserCart> cartRoot = subquery.from(UserCart.class);
        subquery.select(cartRoot.get("id").get("ticketId"))
                .where(cb.equal(cartRoot.get("id").get("userId"), userId));

        update.set("status", TicketStatus.AVAILABLE)
                .where(cb.and(
                        cb.in(ticketRoot.get("id")).value(subquery),
                        cb.equal(ticketRoot.get("status"), TicketStatus.RESERVED)
                ));

        session.createQuery(update).executeUpdate();
    }

    private void deleteUserCart(Session session, Long userId) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaDelete<UserCart> delete = cb.createCriteriaDelete(UserCart.class);
        Root<UserCart> deleteRoot = delete.from(UserCart.class);
        delete.where(cb.equal(deleteRoot.get("id").get("userId"), userId));

        session.createQuery(delete).executeUpdate();
    }


    /**
     * Gets the list of ticket IDs from the user cart.
     *
     * @param userId the ID of the user
     * @return the list of ticket IDs
     */
    public List<Long> getTicketIdsFromUserCart(Long userId){
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<UserCart> root = cq.from(UserCart.class);

            cq.select(root.get("id").get("ticketId"))
                    .where(cb.equal(root.get("id").get("userId"), userId));

            return session.createQuery(cq).getResultList();
        } catch (HibernateException e) {
            log.error("Failed to retrieve tickets IDs from user cart by user ID: {}", userId);
            throw new DaoCrudException(e);
        }
    }
}