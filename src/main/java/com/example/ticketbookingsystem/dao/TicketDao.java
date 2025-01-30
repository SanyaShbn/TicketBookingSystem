//package com.example.ticketbookingsystem.dao;
//
//import com.example.ticketbookingsystem.dto.ticket_dto.TicketFilter;
//import com.example.ticketbookingsystem.entity.Ticket;
//import com.example.ticketbookingsystem.entity.TicketStatus;
//import com.example.ticketbookingsystem.exception.DaoCrudException;
//import com.example.ticketbookingsystem.utils.FiltrationSqlQueryParameters;
//import com.example.ticketbookingsystem.utils.HibernateUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.hibernate.HibernateException;
//import org.hibernate.Session;
//import org.hibernate.query.Query;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//
///**
// * DAO class for managing {@link Ticket} entities.
// */
//@Slf4j
//public class TicketDao extends AbstractHibernateDao<Ticket>{
//    private static final TicketDao INSTANCE = new TicketDao();
//
//    private TicketDao(){
//        super(Ticket.class);
//    }
//
//    /**
//     * Returns the singleton instance of {@link TicketDao}.
//     *
//     * @return the singleton instance
//     */
//    public static TicketDao getInstance(){
//        return INSTANCE;
//    }
//
//    /**
//     * Finds all {@link Ticket} entities based on the specified filter.
//     *
//     * @param ticketFilter the filter to apply
//     * @return the list of matching tickets
//     */
//    public List<Ticket> findAll(TicketFilter ticketFilter, Long eventId) {
//        FiltrationSqlQueryParameters filtrationSqlQueryParameters = buildHqlQuery(ticketFilter);
//        String hql = filtrationSqlQueryParameters.sql();
//        List<Object> parameters = filtrationSqlQueryParameters.parameters();
//
//        return executeFilterQuery(hql, parameters, eventId, ticketFilter);
//    }
//
//    private FiltrationSqlQueryParameters buildHqlQuery(TicketFilter ticketFilter) {
//        List<Object> parameters = new ArrayList<>();
//        List<String> sortHql = new ArrayList<>();
//
//        if (!ticketFilter.priceSortOrder().isEmpty()) {
//            sortHql.add("price " + ticketFilter.priceSortOrder());
//        }
//
//        var orderBy = sortHql.stream().collect(Collectors.joining(
//                " , ",
//                !sortHql.isEmpty() ? " ORDER BY " : " ",
//                ""
//        ));
//
//        String hql = "FROM Ticket t JOIN FETCH t.sportEvent se JOIN FETCH t.seat s JOIN FETCH s.row r "
//        + "JOIN FETCH r.sector sec JOIN FETCH sec.arena a WHERE t.sportEvent.id = :eventId " + orderBy;
//
//        return new FiltrationSqlQueryParameters(hql, parameters);
//    }
//    private List<Ticket> executeFilterQuery(String hql, List<Object> parameters, Long eventId, TicketFilter ticketFilter) {
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            Query<Ticket> query = session.createQuery(hql, Ticket.class);
//            query.setParameter("eventId", eventId);
//
//            for (int i = 0; i < parameters.size(); i++) {
//                query.setParameter(i + 1, parameters.get(i));
//            }
//
//            query.setFirstResult(ticketFilter.offset());
//            query.setMaxResults(ticketFilter.limit());
//
//            return query.list();
//        } catch (HibernateException e) {
//            log.error("Failed to retrieve tickets matching the provided filter");
//            throw new DaoCrudException(e);
//        }
//    }
//
//    /**
//     * Finds all {@link Ticket} entities by specific event ID.
//     *
//     * @param eventId event ID to search specific event tickets
//     * @return the list of matching tickets
//     */
//    public List<Ticket> findAllByEventId(Long eventId){
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            Query<Ticket> query = session.createQuery("FROM Ticket WHERE sportEvent.id = :eventId",
//                    Ticket.class);
//            query.setParameter("eventId", eventId);
//            return query.list();
//        } catch (HibernateException e) {
//            log.error("Failed to retrieve tickets by event ID: {}", eventId);
//            throw new DaoCrudException(e);
//        }
//    }
//
//    /**
//     * Retrieves {@link Ticket} entity status.
//     *
//     * @param ticketId ticket ID to get status
//     * @return specific ticket's status
//     */
//    public String getTicketStatus(Long ticketId){
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            Query<TicketStatus> query = session.createQuery(
//                    "SELECT status FROM Ticket WHERE id = :ticketId", TicketStatus.class
//            );
//            query.setParameter("ticketId", ticketId);
//            return query.uniqueResult().name();
//        } catch (HibernateException e) {
//            log.error("Failed to retrieve status of ticket with ID: {}", ticketId);
//            throw new DaoCrudException(e);
//        }
//    }
//}