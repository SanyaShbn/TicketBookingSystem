package com.example.ticketbookingsystem.dao;

import com.example.ticketbookingsystem.dto.ArenaFilter;
import com.example.ticketbookingsystem.entity.Arena;
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
 * DAO class for managing {@link Arena} entities.
 * Provides methods for performing CRUD operations and custom queries.
 */
@Slf4j
public class ArenaDao extends AbstractHibernateDao<Arena> {
    private static final ArenaDao INSTANCE = new ArenaDao();

    private ArenaDao() {
        super(Arena.class);
    }

    /**
     * Returns the singleton instance of {@link ArenaDao}.
     *
     * @return the singleton instance
     */
    public static ArenaDao getInstance() {
        return INSTANCE;
    }

    /**
     * Finds all {@link Arena} entities based on the specified filter.
     *
     * @param arenaFilter the filter to apply
     * @return the list of matching arenas
     */
    public List<Arena> findAll(ArenaFilter arenaFilter) {
        FiltrationSqlQueryParameters filtrationSqlQueryParameters = buildHqlQuery(arenaFilter);
        String hql = filtrationSqlQueryParameters.sql();

        return executeFilterQuery(hql, arenaFilter);
    }

    private FiltrationSqlQueryParameters buildHqlQuery(ArenaFilter arenaFilter) {
        List<Object> parameters = new ArrayList<>();
        List<String> whereHql = new ArrayList<>();
        List<String> sortHql = new ArrayList<>();

        if (arenaFilter.city() != null) {
            parameters.add("%" + arenaFilter.city() + "%");
            whereHql.add("city LIKE :city");
        }
        if (!arenaFilter.capacitySortOrder().isEmpty()) {
            sortHql.add("capacity " + arenaFilter.capacitySortOrder());
        }
        if (!arenaFilter.seatsNumbSortOrder().isEmpty()) {
            sortHql.add("generalSeatsNumb " + arenaFilter.seatsNumbSortOrder());
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

        String hql = "FROM Arena " + where + orderBy;

        return new FiltrationSqlQueryParameters(hql, parameters);
    }

    private List<Arena> executeFilterQuery(String hql, ArenaFilter arenaFilter) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Arena> query = session.createQuery(hql, Arena.class);

            if (arenaFilter.city() != null) {
                query.setParameter("city", "%" + arenaFilter.city() + "%");
            }

            query.setFirstResult(arenaFilter.offset());
            query.setMaxResults(arenaFilter.limit());

            return query.list();
        } catch (HibernateException e) {
            log.error("Failed to retrieve arenas, which are suitable for provided filter");
            throw new DaoCrudException(e);
        }
    }

    /**
     * Finds all distinct cities where arenas are located.
     *
     * @return the list of distinct city names
     */
    public List<String> findAllArenasCities() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<String> query = session.createQuery("select distinct city from Arena", String.class);
            return query.list();
        } catch (HibernateException e) {
            log.error("Failed to retrieve arena cities");
            throw new DaoCrudException(e);
        }
    }
}
