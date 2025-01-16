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
//    private static final String SAVE_SQL = """
//            INSERT INTO user_cart (user_id, ticket_id) VALUES (?, ?)
//            """;
//    private static final String DELETE_SQL = """
//            DELETE FROM user_cart WHERE user_id = ? AND ticket_id = ?
//            """;
//    private static final String UPDATE_TICKET_STATUS_SQL = """
//            UPDATE ticket SET status = ? WHERE id = ?
//            """;
//    private static final String CLEAR_USER_CART_SQL = """
//            DELETE FROM user_cart WHERE user_id = ?
//            """;
//    private static final String UPDATE_ALL_TICKET_STATUS_SQL = """
//            UPDATE ticket SET status = 'AVAILABLE'
//            WHERE id IN (SELECT ticket_id FROM user_cart WHERE user_id = ?) AND status = 'RESERVED'
//            """;
//
//    private static final String GET_TICKET_IDS = """
//            SELECT ticket_id FROM user_cart WHERE user_id = ?
//            """;

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
     * Saves the user cart and updates the ticket status to 'RESERVED'.
     *
     * @param userCart the user cart entity to save
     */
    public void save(UserCart userCart) {
        Transaction transaction = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            transaction = session.beginTransaction();

            Ticket ticket = userCart.getTicket();
            ticket.setStatus(TicketStatus.RESERVED);
            session.update(ticket);

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
     * Deletes the user cart and updates the ticket status to 'AVAILABLE'.
     *
     * @param userCart the user cart entity to delete
     */
    public void delete(UserCart userCart){
        Transaction transaction = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            transaction = session.beginTransaction();

            Ticket ticket = userCart.getTicket();
            ticket.setStatus(TicketStatus.AVAILABLE);
            session.update(ticket);

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
     * Clears the user cart and updates the status of all tickets to 'AVAILABLE'.
     *
     * @param userId the ID of the user
     */
    public void clearCart(Long userId) {
        Transaction transaction = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            transaction = session.beginTransaction();

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaUpdate<Ticket> update = cb.createCriteriaUpdate(Ticket.class);
            Root<Ticket> root = update.from(Ticket.class);

            Subquery<Long> subquery = update.subquery(Long.class);
            Root<UserCart> cartRoot = subquery.from(UserCart.class);
            subquery.select(cartRoot.get("ticket").get("id"))
                    .where(cb.equal(cartRoot.get("user").get("id"), userId));

            update.set("status", TicketStatus.AVAILABLE)
                    .where(cb.and(
                            cb.in(root.get("id")).value(subquery),
                            cb.equal(root.get("status"), TicketStatus.RESERVED)
                    ));

            session.createQuery(update).executeUpdate();

            CriteriaDelete<UserCart> delete = cb.createCriteriaDelete(UserCart.class);
            Root<UserCart> deleteRoot = delete.from(UserCart.class);
            delete.where(cb.equal(deleteRoot.get("user").get("id"), userId));

            session.createQuery(delete).executeUpdate();

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

            cq.select(root.get("ticket").get("id"))
                    .where(cb.equal(root.get("user").get("id"), userId));

            return session.createQuery(cq).getResultList();
        } catch (HibernateException e) {
            log.error("Failed to retrieve tickets IDs from user cart by user ID: {}", userId);
            throw new DaoCrudException(e);
        }
    }
}