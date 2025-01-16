package com.example.ticketbookingsystem.dao;

import com.example.ticketbookingsystem.dto.SportEventFilter;
import com.example.ticketbookingsystem.entity.SportEvent;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.utils.FiltrationSqlQueryParameters;
import com.example.ticketbookingsystem.utils.HibernateUtil;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DAO class for managing {@link SportEvent} entities.
 */
@Slf4j
public class SportEventDao extends AbstractHibernateDao<SportEvent>{
    private static final SportEventDao INSTANCE = new SportEventDao();

    private SportEventDao(){
        super(SportEvent.class);
    }

    /**
     * Returns the singleton instance of {@link SportEventDao}.
     *
     * @return the singleton instance
     */
    public static SportEventDao getInstance(){
        return INSTANCE;
    }

    /**
     * Finds all {@link SportEvent} entities based on the specified filter.
     *
     * @param sportEventFilter the filter to apply
     * @return the list of matching sport events
     */
    public List<SportEvent> findAll(SportEventFilter sportEventFilter) {
        FiltrationSqlQueryParameters filtrationSqlQueryParameters = buildHqlQuery(sportEventFilter);
        String hql = filtrationSqlQueryParameters.sql();

        return executeFilterQuery(hql, sportEventFilter);
    }

    private FiltrationSqlQueryParameters buildHqlQuery(SportEventFilter sportEventFilter) {
        List<Object> parameters = new ArrayList<>();
        List<String> whereHql = new ArrayList<>();
        List<String> sortHql = new ArrayList<>();

        if (sportEventFilter.startDate() != null) {
            parameters.add(sportEventFilter.startDate());
            whereHql.add("eventDateTime >= :startDate");
        }
        if (sportEventFilter.endDate() != null) {
            parameters.add(sportEventFilter.endDate());
            whereHql.add("eventDateTime <= :endDate");
        }
        if (sportEventFilter.arenaId() != null) {
            parameters.add(sportEventFilter.arenaId());
            whereHql.add("arena.id = :arenaId");
        }
        if (!sportEventFilter.sortOrder().isEmpty()) {
            sortHql.add("eventDateTime " + sportEventFilter.sortOrder());
        }

        var orderBy = sortHql.stream().collect(Collectors.joining(
                ", ",
                !sortHql.isEmpty() ? " ORDER BY " : "",
                ""
        ));

        var where = whereHql.stream().collect(Collectors.joining(
                " AND ",
                !whereHql.isEmpty() ? " WHERE " : "",
                ""
        ));

        String hql = "FROM SportEvent se JOIN FETCH se.arena a " + where + orderBy;

        return new FiltrationSqlQueryParameters(hql, parameters);
    }
    private List<SportEvent> executeFilterQuery(String hql, SportEventFilter sportEventFilter) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<SportEvent> query = session.createQuery(hql, SportEvent.class);

            if (sportEventFilter.startDate() != null) {
                query.setParameter("startDate", sportEventFilter.startDate());
            }
            if (sportEventFilter.endDate() != null) {
                query.setParameter("endDate", sportEventFilter.endDate());
            }
            if (sportEventFilter.arenaId() != null) {
                query.setParameter("arenaId", sportEventFilter.arenaId());
            }

            query.setFirstResult(sportEventFilter.offset());
            query.setMaxResults(sportEventFilter.limit());

            return query.list();
        } catch (HibernateException e) {
            log.error("Failed to retrieve sport events matching the provided filter");
            throw new DaoCrudException(e);
        }
    }
}