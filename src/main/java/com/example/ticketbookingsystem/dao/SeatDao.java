package com.example.ticketbookingsystem.dao;

import com.example.ticketbookingsystem.entity.Seat;
import com.example.ticketbookingsystem.entity.SportEvent;
import com.example.ticketbookingsystem.entity.Ticket;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.utils.HibernateUtil;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import java.util.List;

/**
 * DAO class for managing {@link Seat} entities.
 */
@Slf4j
public class SeatDao extends AbstractHibernateDao<Seat>{
    private static final SeatDao INSTANCE = new SeatDao();

    private SeatDao(){
        super(Seat.class);
    }

    /**
     * Returns the singleton instance of {@link SeatDao}.
     *
     * @return the singleton instance
     */
    public static SeatDao getInstance(){
        return INSTANCE;
    }

    /**
     * Retrieves all seats by event ID.
     *
     * @param eventId the ID of the event
     * @return a list of seats for the event
     */
    public List<Seat> findAllByEventId(Long eventId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Seat> cq = cb.createQuery(Seat.class);
            Root<Seat> seatRoot = cq.from(Seat.class);

            seatRoot.fetch("row", JoinType.INNER).fetch("sector", JoinType.INNER)
                    .fetch("arena", JoinType.INNER);

            Join<Object, Object> rowJoin = seatRoot.join("row", JoinType.INNER);
            Join<Object, Object> sectorJoin = rowJoin.join("sector", JoinType.INNER);
            Join<Object, Object> arenaJoin = sectorJoin.join("arena", JoinType.INNER);

            Subquery<Long> sportEventSubquery = cq.subquery(Long.class);
            Root<SportEvent> eventRoot = sportEventSubquery.from(SportEvent.class);
            sportEventSubquery.select(eventRoot.get("arena").get("id"))
                    .where(cb.equal(eventRoot.get("id"), eventId));

            cq.select(seatRoot)
                    .where(cb.and(
                            cb.equal(arenaJoin.get("id"), sportEventSubquery)
                    ))
                    .orderBy(cb.asc(
                            sectorJoin.get("sectorName")),
                            cb.asc(rowJoin.get("id")),
                            cb.asc(seatRoot.get("id"))
                    );

            return session.createQuery(cq).getResultList();
        } catch (HibernateException e) {
            log.error("Failed to find seats by provided event ID: {}", eventId);
            throw new DaoCrudException(e);
        }
    }

    /**
     * Retrieves all seats by event ID that do not have tickets.
     *
     * @param eventId the ID of the event
     * @return a list of seats with no tickets for the event
     */
    public List<Seat> findByEventIdWithNoTickets(Long eventId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Seat> cq = cb.createQuery(Seat.class);
            Root<Seat> seatRoot = cq.from(Seat.class);

            seatRoot.fetch("row", JoinType.INNER).fetch("sector", JoinType.INNER)
                    .fetch("arena", JoinType.INNER);

            Join<Object, Object> rowJoin = seatRoot.join("row", JoinType.INNER);
            Join<Object, Object> sectorJoin = rowJoin.join("sector", JoinType.INNER);
            Join<Object, Object> arenaJoin = sectorJoin.join("arena", JoinType.INNER);

            Subquery<Long> sportEventSubquery = cq.subquery(Long.class);
            Root<SportEvent> eventRoot = sportEventSubquery.from(SportEvent.class);
            sportEventSubquery.select(eventRoot.get("arena").get("id"))
                    .where(cb.equal(eventRoot.get("id"), eventId));

            Subquery<Long> ticketSubquery = cq.subquery(Long.class);
            Root<Ticket> ticketRoot = ticketSubquery.from(Ticket.class);
            ticketSubquery.select(ticketRoot.get("seat").get("id"))
                    .where(cb.and(
                            cb.equal(ticketRoot.get("sportEvent").get("id"), eventId),
                            cb.equal(ticketRoot.get("seat").get("id"), seatRoot.get("id"))
                    ));

            cq.select(seatRoot)
                    .where(cb.and(
                            cb.equal(arenaJoin.get("id"), sportEventSubquery),
                            cb.not(cb.exists(ticketSubquery))
                    ))
                    .orderBy(cb.asc(
                            sectorJoin.get("sectorName")),
                            cb.asc(rowJoin.get("id")),
                            cb.asc(seatRoot.get("id"))
                    );

            return session.createQuery(cq).getResultList();
        } catch (HibernateException e) {
            log.error("Failed to find available seats by provided event ID: {}", eventId);
            throw new DaoCrudException(e);
        }
    }

    /**
     * Retrieves all seats by event ID when updating a ticket.
     *
     * @param eventId the ID of the event
     * @param seatId the ID of the seat
     * @return a list of seats for the event when updating
     */
    public List<Seat> findAllByEventIdWhenUpdate(Long eventId, Long seatId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Seat> cq = cb.createQuery(Seat.class);
            Root<Seat> seatRoot = cq.from(Seat.class);

            seatRoot.fetch("row", JoinType.INNER).fetch("sector", JoinType.INNER)
                    .fetch("arena", JoinType.INNER);

            Join<Object, Object> rowJoin = seatRoot.join("row", JoinType.INNER);
            Join<Object, Object> sectorJoin = rowJoin.join("sector", JoinType.INNER);
            Join<Object, Object> arenaJoin = sectorJoin.join("arena", JoinType.INNER);

            Subquery<Long> sportEventSubquery = cq.subquery(Long.class);
            Root<SportEvent> eventRoot = sportEventSubquery.from(SportEvent.class);
            sportEventSubquery.select(eventRoot.get("arena").get("id"))
                    .where(cb.equal(eventRoot.get("id"), eventId));

            Subquery<Long> ticketSubquery = cq.subquery(Long.class);
            Root<Ticket> ticketRoot = ticketSubquery.from(Ticket.class);
            ticketSubquery.select(ticketRoot.get("seat").get("id"))
                    .where(cb.and(
                            cb.equal(ticketRoot.get("sportEvent").get("id"), eventId),
                            cb.equal(ticketRoot.get("seat").get("id"), seatRoot.get("id"))
                    ));

            cq.select(seatRoot).where(cb.and(
                            cb.equal(arenaJoin.get("id"), sportEventSubquery),
                            cb.or(
                                    cb.isNull(ticketRoot.get("id")),
                                    cb.notEqual(ticketRoot.get("sportEvent").get("id"), eventId),
                                    cb.equal(seatRoot.get("id"), seatId)
                            )
                    ))
                    .orderBy(cb.asc(
                            sectorJoin.get("sectorName")),
                            cb.asc(rowJoin.get("id")),
                            cb.asc(seatRoot.get("id"))
                    );

            return session.createQuery(cq).getResultList();
        } catch (HibernateException e) {
            log.error("Failed to find seats by provided event ID: {}", eventId);
            throw new DaoCrudException(e);
        }
    }
}