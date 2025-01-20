package com.example.ticketbookingsystem.dao;

import com.example.ticketbookingsystem.dto.RowFilter;
import com.example.ticketbookingsystem.entity.Row;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.exception.DaoResourceNotFoundException;
import com.example.ticketbookingsystem.utils.FiltrationSqlQueryParameters;
import com.example.ticketbookingsystem.utils.HibernateUtil;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * DAO class for managing {@link Row} entities.
 */
@Slf4j
public class RowDao extends AbstractHibernateDao<Row>{
    private static final RowDao INSTANCE = new RowDao();

    private RowDao(){super(Row.class);}

    /**
     * Returns the singleton instance of {@link RowDao}.
     *
     * @return the singleton instance
     */
    public static RowDao getInstance(){
        return INSTANCE;
    }

    /**
     * Retrieves all rows based on the specified filter and sector ID.
     *
     * @param rowFilter the filter criteria for rows
     * @param sectorId the ID of the sector
     * @return a list of rows matching the criteria
     */
    public List<Row> findAll(RowFilter rowFilter, Long sectorId) {
        FiltrationSqlQueryParameters filtrationSqlQueryParameters = buildHqlQuery(rowFilter);
        String sql = filtrationSqlQueryParameters.sql();
        List<Object> parameters = filtrationSqlQueryParameters.parameters();

        return executeFilterQuery(sql, parameters, sectorId, rowFilter);
    }

    private FiltrationSqlQueryParameters buildHqlQuery(RowFilter rowFilter) {
        List<Object> parameters = new ArrayList<>();
        List<String> sortHql = new ArrayList<>();

        if (!rowFilter.rowNumberOrder().isEmpty()) {
            sortHql.add("rowNumber " + rowFilter.rowNumberOrder());
        }
        if (!rowFilter.seatsNumbOrder().isEmpty()) {
            sortHql.add("seatsNumb " + rowFilter.seatsNumbOrder());
        }

        var orderBy = sortHql.stream().collect(Collectors.joining(
                ", ",
                !sortHql.isEmpty() ? " ORDER BY " : " ",
                ""
        ));

        String hql = "FROM Row r JOIN FETCH r.sector WHERE sector.id = :sectorId " + orderBy;

        return new FiltrationSqlQueryParameters(hql, parameters);
    }
    private List<Row> executeFilterQuery(String hql, List<Object> parameters, Long sectorId, RowFilter rowFilter) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Row> query = session.createQuery(hql, Row.class);
            query.setParameter("sectorId", sectorId);

            for (int i = 0; i < parameters.size(); i++) {
                query.setParameter(i + 1, parameters.get(i));
            }

            query.setFirstResult(rowFilter.offset());
            query.setMaxResults(rowFilter.limit());

            return query.list();
        } catch (HibernateException e) {
            log.error("Failed to retrieve rows, which are suitable for provided filter");
            throw new DaoCrudException(e);
        }
    }

    @Override
    public Optional<Row> findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Row> query = session.createQuery("FROM Row r JOIN FETCH r.sector WHERE r.id = :id", Row.class);
            query.setParameter("id", id);
            Row row = query.uniqueResult();
            return Optional.ofNullable(row);
        }
        catch (HibernateException e) {
            log.error("Failed to retrieve row by provided ID: {}", id);
            throw new DaoCrudException(e);
        }
    }

    @Override
    public void save(Row row) {
        Transaction transaction = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            transaction = session.beginTransaction();
            session.persist(row);
            updateSectorAfterRowSave(session, row);
            transaction.commit();
            log.info("Row saved");
        } catch (HibernateException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            log.error("Failed to save row");
            throw new DaoCrudException(e);
        } finally {
            session.close();
        }
    }

    @Override
    public void update(Row row) {
        Optional<Row> rowBeforeUpdate = findById(row.getId());
        if (rowBeforeUpdate.isEmpty()) {
            log.error("Failed to find row");
            throw new DaoResourceNotFoundException("Row not found");
        }
        Transaction transaction = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            transaction = session.beginTransaction();
            updateSectorBeforeRowUpdate(session, rowBeforeUpdate.get(), row);
            session.merge(row);
            transaction.commit();
            log.info("Row updated");
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            log.error("Failed to update row");
            throw new DaoCrudException(e);
        } finally {
            session.close();
        }
    }
    @Override
    public void delete(Long id) {
        Transaction transaction = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            transaction = session.beginTransaction();
            Row row = session.find(Row.class, id);
            if (row != null) {
                updateSectorAfterRowDelete(session, row);
                session.remove(row);
                transaction.commit();
                log.info("Row deleted with given id: {}", id);
            }
            else {
                transaction.rollback();
                log.error("Failed to find row to be deleted");
            }
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            log.error("Failed to delete row");
            throw new DaoCrudException(e);
        } finally {
            session.close();
        }
    }

    private void updateSectorAfterRowSave(Session session, Row row) throws HibernateException {
        Query<?> query = session.createQuery("UPDATE Sector SET availableRowsNumb = availableRowsNumb + 1, "
                + "availableSeatsNumb = availableSeatsNumb + :seatsNumb WHERE id = :sectorId");
        query.setParameter("seatsNumb", row.getSeatsNumb());
        query.setParameter("sectorId", row.getSector().getId());
        query.executeUpdate();
        log.info("Sector associated with saved row has been updated");
    }

    private void updateSectorBeforeRowUpdate(Session session, Row rowBeforeUpdate, Row row) throws HibernateException {
        Query<?> query = session.createQuery("UPDATE Sector SET availableSeatsNumb = "
                + "availableSeatsNumb - :oldSeatsNumb + :newSeatsNumb WHERE id = :sectorId");
        query.setParameter("oldSeatsNumb", rowBeforeUpdate.getSeatsNumb());
        query.setParameter("newSeatsNumb", row.getSeatsNumb());
        query.setParameter("sectorId", row.getSector().getId());
        query.executeUpdate();
        log.info("Sector associated with updating row has been updated");
    }

    private void updateSectorAfterRowDelete(Session session, Row row) throws HibernateException {
        Query<?> query = session.createQuery("UPDATE Sector SET availableRowsNumb = availableRowsNumb - 1, "
                + "availableSeatsNumb = availableSeatsNumb - :seatsNumb WHERE id = :sectorId");
        query.setParameter("seatsNumb", row.getSeatsNumb());
        query.setParameter("sectorId", row.getSector().getId());
        query.executeUpdate();
        log.info("Sector associated with deleted row has been updated");
    }
}