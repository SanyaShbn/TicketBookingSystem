//package com.example.ticketbookingsystem.dao;
//
//import com.example.ticketbookingsystem.entity.*;
//import com.example.ticketbookingsystem.exception.DaoCrudException;
//import com.example.ticketbookingsystem.utils.HibernateUtil;
//import jakarta.persistence.criteria.*;
//import lombok.extern.slf4j.Slf4j;
//import org.hibernate.HibernateException;
//import org.hibernate.Session;
//
//import java.util.List;
//
///**
// * DAO class for managing {@link Seat} entities.
// */
//@Slf4j
//public class SeatDao extends AbstractHibernateDao<Seat>{
//    private static final SeatDao INSTANCE = new SeatDao();
//
//    private SeatDao(){
//        super(Seat.class);
//    }
//
//    /**
//     * Returns the singleton instance of {@link SeatDao}.
//     *
//     * @return the singleton instance
//     */
//    public static SeatDao getInstance(){
//        return INSTANCE;
//    }
//
//    /**
//     * Retrieves all seats by event ID.
//     *
//     * @param eventId the ID of the event
//     * @return a list of seats for the event
//     */
//    public List<Seat> findAllByEventId(Long eventId) {
//        CriteriaBuilder cb = HibernateUtil.getSessionFactory().getCriteriaBuilder();
//        CriteriaQuery<Seat> cq = cb.createQuery(Seat.class);
//        return findSeats(cq, eventId, false, null);
//    }
//
//    /**
//     * Retrieves all seats by event ID that do not have tickets.
//     *
//     * @param eventId the ID of the event
//     * @return a list of seats with no tickets for the event
//     */
//    public List<Seat> findByEventIdWithNoTickets(Long eventId) {
//        CriteriaBuilder cb = HibernateUtil.getSessionFactory().getCriteriaBuilder();
//        CriteriaQuery<Seat> cq = cb.createQuery(Seat.class);
//        return findSeats(cq, eventId, true, null);
//    }
//
//    /**
//     * Retrieves all seats by event ID when updating a ticket.
//     *
//     * @param eventId the ID of the event
//     * @param seatId the ID of the seat
//     * @return a list of seats for the event when updating
//     */
//    public List<Seat> findAllByEventIdWhenUpdate(Long eventId, Long seatId) {
//        CriteriaBuilder cb = HibernateUtil.getSessionFactory().getCriteriaBuilder();
//        CriteriaQuery<Seat> cq = cb.createQuery(Seat.class);
//        return findSeats(cq, eventId, false, seatId);
//    }
//
//    private List<Seat> findSeats(CriteriaQuery<Seat> cq, Long eventId, boolean withTickets, Long seatId) {
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            CriteriaBuilder cb = session.getCriteriaBuilder();
//            Root<Seat> seatRoot = cq.from(Seat.class);
//
//            setupFetches(seatRoot);
//
//            Join<Object, Object> arenaJoin = setupJoins(seatRoot);
//
//            Subquery<Long> sportEventSubquery = setupSportEventSubquery(cb, cq, eventId);
//            Subquery<Long> ticketSubquery = setupTicketSubquery(cb, cq, eventId, seatRoot);
//
//            setupWhereClauses(cb, cq, seatRoot, arenaJoin, sportEventSubquery, ticketSubquery, withTickets, seatId);
//
//            setupOrderBy(cb, cq, seatRoot);
//
//            return session.createQuery(cq).getResultList();
//        } catch (HibernateException e) {
//            log.error("Failed to find seats by provided event ID: {}", eventId, e);
//            throw new DaoCrudException(e);
//        }
//    }
//
//    private void setupFetches(Root<Seat> seatRoot) {
//        seatRoot.fetch("row", JoinType.INNER).fetch("sector", JoinType.INNER).fetch("arena", JoinType.INNER);
//    }
//
//    private Join<Object, Object> setupJoins(Root<Seat> seatRoot) {
//        Join<Object, Object> rowJoin = seatRoot.join("row", JoinType.INNER);
//        Join<Object, Object> sectorJoin = rowJoin.join("sector", JoinType.INNER);
//        return sectorJoin.join("arena", JoinType.INNER);
//    }
//
//    private Subquery<Long> setupSportEventSubquery(CriteriaBuilder cb, CriteriaQuery<?> cq, Long eventId) {
//        Subquery<Long> sportEventSubquery = cq.subquery(Long.class);
//        Root<SportEvent> eventRoot = sportEventSubquery.from(SportEvent.class);
//        sportEventSubquery.select(eventRoot.get("arena").get("id"))
//                .where(cb.equal(eventRoot.get("id"), eventId));
//        return sportEventSubquery;
//    }
//
//    private Subquery<Long> setupTicketSubquery(CriteriaBuilder cb, CriteriaQuery<?> cq, Long eventId, Root<Seat> seatRoot) {
//        Subquery<Long> ticketSubquery = cq.subquery(Long.class);
//        Root<Ticket> ticketRoot = ticketSubquery.from(Ticket.class);
//        ticketSubquery.select(ticketRoot.get("seat").get("id"))
//                .where(cb.and(cb.equal(ticketRoot.get("sportEvent").get("id"), eventId),
//                        cb.equal(ticketRoot.get("seat").get("id"), seatRoot.get("id"))));
//        return ticketSubquery;
//    }
//
//    private void setupWhereClauses(CriteriaBuilder cb, CriteriaQuery<Seat> cq, Root<Seat> seatRoot, Join<Object, Object> arenaJoin,
//                                   Subquery<Long> sportEventSubquery, Subquery<Long> ticketSubquery, boolean withTickets, Long seatId) {
//        if (seatId != null) {
//            cq.where(cb.and(cb.equal(arenaJoin.get("id"), sportEventSubquery),
//                    cb.or(cb.not(cb.exists(ticketSubquery)), cb.equal(seatRoot.get("id"), seatId))));
//        } else if (withTickets) {
//            cq.where(cb.and(cb.equal(arenaJoin.get("id"), sportEventSubquery), cb.not(cb.exists(ticketSubquery))));
//        } else {
//            cq.where(cb.equal(arenaJoin.get("id"), sportEventSubquery));
//        }
//    }
//
//    private void setupOrderBy(CriteriaBuilder cb, CriteriaQuery<Seat> cq, Root<Seat> seatRoot) {
//        Join<Object, Object> rowJoin = seatRoot.join("row", JoinType.INNER);
//        Join<Object, Object> sectorJoin = rowJoin.join("sector", JoinType.INNER);
//        cq.orderBy(cb.asc(sectorJoin.get("sectorName")), cb.asc(rowJoin.get("id")), cb.asc(seatRoot.get("id")));
//    }
//}