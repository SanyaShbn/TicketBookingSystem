package com.example.ticketbookingsystem.dao;

import com.example.ticketbookingsystem.dto.PurchasedTicketDto;
import com.example.ticketbookingsystem.entity.*;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.utils.HibernateUtil;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DAO class for managing {@link PurchasedTicket} entities.
 */
@Slf4j
public class PurchasedTicketsDao extends AbstractHibernateDao<PurchasedTicket>{
    private static final PurchasedTicketsDao INSTANCE = new PurchasedTicketsDao();

    private PurchasedTicketsDao(){
        super(PurchasedTicket.class);
    }

    /**
     * Returns the singleton instance of {@link PurchasedTicketsDao}.
     *
     * @return the singleton instance
     */
    public static PurchasedTicketsDao getInstance(){
        return INSTANCE;
    }

    /**
     * Saves the purchased tickets and updates their status to 'SOLD'.
     *
     * @param ticketIds the list of ticket IDs
     * @param userId the ID of the user
     */
    public void save(List<Long> ticketIds, Long userId) {
        Transaction transaction = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            transaction = session.beginTransaction();

            for (Long ticketId : ticketIds) {
                Ticket ticket = session.get(Ticket.class, ticketId);
                if (ticket != null) {
                    ticket.setStatus(TicketStatus.SOLD);
                    session.merge(ticket);

                    PurchasedTicket purchasedTicket = PurchasedTicket.builder()
                            .userId(userId)
                            .purchaseDate(LocalDateTime.now())
                            .ticket(ticket)
                            .build();
                    session.persist(purchasedTicket);
                }
            }
            transaction.commit();
            log.info("Ticket purchase saved");
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            log.error("Failed to save ticket purchase");
            throw new DaoCrudException(e);
        } finally {
            session.close();
        }
    }

    /**
     * Finds all purchased tickets by user ID.
     *
     * @param userId the ID of the user
     * @return the list of purchased tickets
     */
    public List<PurchasedTicketDto> findAllByUserId(Long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<PurchasedTicketDto> cq = cb.createQuery(PurchasedTicketDto.class);
            Root<PurchasedTicket> root = cq.from(PurchasedTicket.class);

            Join<PurchasedTicket, Ticket> ticketJoin = root.join("ticket");
            Join<Ticket, SportEvent> eventJoin = ticketJoin.join("sportEvent");
            Join<Ticket, Seat> seatJoin = ticketJoin.join("seat");
            Join<Seat, Row> rowJoin = seatJoin.join("row");
            Join<Row, Sector> sectorJoin = rowJoin.join("sector");
            Join<Sector, Arena> arenaJoin = sectorJoin.join("arena");

            cq.select(cb.construct(
                    PurchasedTicketDto.class,
                    ticketJoin.get("id"),
                    eventJoin.get("eventName"),
                    eventJoin.get("eventDateTime"),
                    arenaJoin.get("name"),
                    arenaJoin.get("city"),
                    sectorJoin.get("sectorName"),
                    rowJoin.get("rowNumber"),
                    seatJoin.get("seatNumber"),
                    ticketJoin.get("price")
            )).where(cb.equal(root.get("userId"), userId));

            return session.createQuery(cq).getResultList();
        } catch (HibernateException e) {
            log.error("Failed to get all purchased tickets by user ID: {}", userId);
            throw new DaoCrudException(e);
        }
    }
}