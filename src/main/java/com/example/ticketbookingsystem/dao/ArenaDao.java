package com.example.ticketbookingsystem.dao;

import com.example.ticketbookingsystem.dto.ArenaFilter;
import com.example.ticketbookingsystem.entity.Arena;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.utils.FiltrationSqlQueryParameters;
import com.example.ticketbookingsystem.utils.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ArenaDao extends AbstractHibernateDao<Arena> {
    private static final ArenaDao INSTANCE = new ArenaDao();

    private ArenaDao() {
        super(Arena.class);
    }

    public static ArenaDao getInstance() {
        return INSTANCE;
    }

    public List<Arena> findAll(ArenaFilter arenaFilter) {
        FiltrationSqlQueryParameters filtrationSqlQueryParameters = buildSqlQuery(arenaFilter);
        String hql = filtrationSqlQueryParameters.sql();
        List<Object> parameters = filtrationSqlQueryParameters.parameters();

        return executeFilterQuery(hql, arenaFilter, parameters);
    }

    private FiltrationSqlQueryParameters buildSqlQuery(ArenaFilter arenaFilter) {
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

    private List<Arena> executeFilterQuery(String hql, ArenaFilter arenaFilter, List<Object> parameters) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Arena> query = session.createQuery(hql, Arena.class);

            if (arenaFilter.city() != null) {
                query.setParameter("city", "%" + arenaFilter.city() + "%");
            }

            query.setFirstResult(arenaFilter.offset());
            query.setMaxResults(arenaFilter.limit());

            return query.list();
        } catch (HibernateException e) {
            throw new DaoCrudException(e);
        }
    }

    public List<String> findAllArenasCities() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<String> query = session.createQuery("select distinct city from Arena", String.class);
            return query.list();
        } catch (HibernateException e) {
            throw new DaoCrudException(e);
        }
    }
}
