//package com.example.ticketbookingsystem.dao;
//
//import com.example.ticketbookingsystem.dto.RowFilter;
//import com.example.ticketbookingsystem.entity.Row;
//import com.example.ticketbookingsystem.exception.CreateUpdateEntityException;
//import com.example.ticketbookingsystem.exception.DaoCrudException;
//import com.example.ticketbookingsystem.service.SectorService;
//import com.example.ticketbookingsystem.utils.ConnectionManager;
//import com.example.ticketbookingsystem.utils.FiltrationSqlQueryParameters;
//
//import java.sql.*;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//public class RowDao implements DaoCrud<Long, Row>{
//    private static final RowDao INSTANCE = new RowDao();
//    private static final SectorService sectorService = SectorService.getInstance();
//    private static final String SAVE_SQL = """
//            INSERT INTO row (row_number, seats_numb, sector_id)
//            VALUES (?, ?, ?)
//            """;
//    private static final String UPDATE_SECTOR_AFTER_ROW_SAVE_SQL = """
//            UPDATE sector
//            SET available_rows_numb=available_rows_numb + 1,
//                available_seats_numb=available_seats_numb + ?
//            WHERE id=?
//            """;
//    private static final String UPDATE_SECTOR_AFTER_ROW_DELETE_SQL = """
//            UPDATE sector
//            SET available_rows_numb=available_rows_numb - 1,
//                available_seats_numb=available_seats_numb - ?
//            WHERE id=?
//            """;
//    private static final String UPDATE_SECTOR_AFTER_ROW_UPDATE_SQL = """
//            UPDATE sector
//            SET available_seats_numb=available_seats_numb - ? + ?
//            WHERE id=?
//            """;
//    private static final String DELETE_SQL = """
//            DELETE FROM row WHERE id=?
//            """;
//    private static final String UPDATE_SQL = """
//            UPDATE row
//            SET row_number=?,
//                seats_numb=?,
//                sector_id=?
//            WHERE id=?
//            """;
//    private static final String FIND_ALL_SQL = """
//            SELECT r.id, r.row_number, r.seats_numb, r.sector_id
//            FROM public.row r
//            JOIN public.sector s on s.id = r.sector_id
//            """;
//
//    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
//            WHERE r.id=?
//            """;
//
//    private static final String FIND_ALL_BY_SECTOR_ID_SQL = FIND_ALL_SQL + """
//            WHERE r.sector_id=?
//            """;
//    public static RowDao getInstance(){
//        return INSTANCE;
//    }
//    private RowDao(){
//    }
//    public List<Row> findAll(RowFilter rowFilter, Long sectorId) {
//        FiltrationSqlQueryParameters filtrationSqlQueryParameters = buildSqlQuery(rowFilter);
//        String sql = filtrationSqlQueryParameters.sql();
//        List<Object> parameters = filtrationSqlQueryParameters.parameters();
//
//        return executeFilterQuery(sql, parameters, sectorId);
//    }
//
//    private FiltrationSqlQueryParameters buildSqlQuery(RowFilter rowFilter) {
//        List<Object> parameters = new ArrayList<>();
//        List<String> sortSql = new ArrayList<>();
//
//        if (!rowFilter.rowNumberOrder().isEmpty()) {
//            sortSql.add("row_number " + rowFilter.rowNumberOrder());
//        }
//        if (!rowFilter.seatsNumbOrder().isEmpty()) {
//            sortSql.add("seats_numb " + rowFilter.seatsNumbOrder());
//        }
//
//        var orderBy = sortSql.stream().collect(Collectors.joining(
//                " , ",
//                !sortSql.isEmpty() ? " ORDER BY " : " ",
//                " LIMIT ? OFFSET ? "
//        ));
//
//        parameters.add(rowFilter.limit());
//        parameters.add(rowFilter.offset());
//
//        String sql = FIND_ALL_BY_SECTOR_ID_SQL + orderBy;
//
//        return new FiltrationSqlQueryParameters(sql, parameters);
//    }
//    private List<Row> executeFilterQuery(String sql, List<Object> parameters, Long sectorId) {
//        try (var connection = ConnectionManager.get();
//             var statement = connection.prepareStatement(sql)) {
//
//            statement.setLong(1, sectorId);
//
//            for (int i = 1; i <= parameters.size(); i++) {
//                statement.setObject(i + 1, parameters.get(i - 1));
//            }
//
//            try (var result = statement.executeQuery()) {
//                List<Row> rowList = new ArrayList<>();
//                while (result.next()) {
//                    rowList.add(buildRow(result));
//                }
//                return rowList;
//            }
//        } catch (SQLException e) {
//            throw new DaoCrudException(e);
//        }
//    }
//
//    @Override
//    public List<Row> findAll() {
//        try(var connection = ConnectionManager.get();
//            var statement = connection.prepareStatement(FIND_ALL_SQL)){
//            List<Row> rowsList = new ArrayList<>();
//
//            var result = statement.executeQuery();
//            while (result.next()){
//                rowsList.add(buildRow(result));
//            }
//            return rowsList;
//        } catch (SQLException e) {
//            throw new DaoCrudException(e);
//        }
//    }
//    @Override
//    public Optional<Row> findById(Long id) {
//        try(var connection = ConnectionManager.get();
//            var statement = connection.prepareStatement(FIND_BY_ID_SQL)){
//            statement.setLong(1, id);
//            var result = statement.executeQuery();
//            Row row = null;
//            if (result.next()){
//                row = buildRow(result);
//            }
//            return Optional.ofNullable(row);
//        } catch (SQLException e) {
//            throw new DaoCrudException(e);
//        }
//    }
//    @Override
//    public Row save(Row row) {
//        try (Connection connection = ConnectionManager.get()) {
//            connection.setAutoCommit(false);
//            try {
//                performSave(row, connection);
//                updateSectorAfterRowSave(connection, row);
//                connection.commit();
//            } catch (SQLException e) {
//                handleRollback(connection);
//                throw new CreateUpdateEntityException(e.getMessage());
//            } finally {
//                resetAutoCommit(connection);
//            }
//        } catch (SQLException e) {
//            throw new DaoCrudException(e);
//        }
//        return row;
//    }
//    private void performSave(Row row, Connection connection) throws SQLException {
//        try (PreparedStatement statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
//            setStatement(row, statement);
//            statement.executeUpdate();
//
//            try (ResultSet keys = statement.getGeneratedKeys()) {
//                if (keys.next()) {
//                    row.setId(keys.getLong("id"));
//                }
//            }
//        }
//    }
//    @Override
//    public boolean update(Row row) {
//        boolean isSuccessful = false;
//        try (Connection connection = ConnectionManager.get()) {
//            connection.setAutoCommit(false);
//            try {
//                isSuccessful = performUpdate(row, connection);
//                if (isSuccessful) {
//                    connection.commit();
//                } else {
//                    connection.rollback();
//                }
//            } catch (SQLException e) {
//                handleRollback(connection);
//                throw new CreateUpdateEntityException(e.getMessage());
//            } finally {
//                resetAutoCommit(connection);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return isSuccessful;
//    }
//    private boolean performUpdate(Row row, Connection connection) throws SQLException {
//        try (PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {
//            setStatement(row, statement);
//            statement.setLong(4, row.getId());
//            updateSectorAfterRowUpdate(connection, row);
//            return statement.executeUpdate() > 0;
//        }
//    }
//    @Override
//    public boolean delete(Long id) {
//        boolean isSuccessful = false;
//        try (Connection connection = ConnectionManager.get()) {
//            connection.setAutoCommit(false);
//            try {
//                isSuccessful = performDelete(id, connection);
//                if (isSuccessful) {
//                    connection.commit();
//                } else {
//                    connection.rollback();
//                }
//            } catch (SQLException e) {
//                handleRollback(connection);
//                throw new DaoCrudException(e);
//            } finally {
//                resetAutoCommit(connection);
//            }
//        } catch (SQLException e) {
//            throw new DaoCrudException(e);
//        }
//        return isSuccessful;
//    }
//    private boolean performDelete(Long id, Connection connection) throws SQLException {
//        try (PreparedStatement statement = connection.prepareStatement(DELETE_SQL)) {
//            statement.setLong(1, id);
//            updateSectorAfterRowDelete(connection, id);
//            return statement.executeUpdate() > 0;
//        }
//    }
//    private void handleRollback(Connection connection) {
//        try {
//            if (connection != null) {
//                connection.rollback();
//            }
//        } catch (SQLException rollbackEx) {
//            rollbackEx.printStackTrace();
//        }
//    }
//    private void resetAutoCommit(Connection connection) {
//        try {
//            if (connection != null) {
//                connection.setAutoCommit(true);
//            }
//        } catch (SQLException ex) {
//            ex.printStackTrace();
//        }
//    }
//    private Row buildRow(ResultSet result) throws SQLException {
//        return new Row(
//                result.getLong("id"),
//                result.getInt("row_number"),
//                result.getInt("seats_numb"),
//                sectorService.findById((long) result.getInt("sector_id")).get()
//        );
//    }
//    private void setStatement(Row row, PreparedStatement statement) throws SQLException {
//        statement.setInt(1, row.getRowNumber());
//        statement.setInt(2, row.getSeatsNumb());
//        statement.setLong(3, row.getSector().getId());
//    }
//    private void updateSectorAfterRowSave(Connection connection, Row row) throws SQLException {
//        try (var updateStatement = connection.prepareStatement(UPDATE_SECTOR_AFTER_ROW_SAVE_SQL)) {
//            updateStatement.setInt(1, row.getSeatsNumb());
//            updateStatement.setLong(2, row.getSector().getId());
//            updateStatement.executeUpdate();
//        }
//    }
//    private void updateSectorAfterRowDelete(Connection connection, Long id) {
//        try (var updateStatement = connection.prepareStatement(UPDATE_SECTOR_AFTER_ROW_DELETE_SQL)) {
//
//            Optional<Row> rowBeforeDelete = findByIdBeforeUpdateOrDelete(connection, id);
//
//            if(rowBeforeDelete.isPresent()){
//                updateStatement.setInt(1, rowBeforeDelete.get().getSeatsNumb());
//                updateStatement.setLong(2, rowBeforeDelete.get().getSector().getId());
//                updateStatement.executeUpdate();
//            }
//        } catch (SQLException e) {
//            throw new DaoCrudException(e);
//        }
//    }
//    private void updateSectorAfterRowUpdate(Connection connection, Row row) throws SQLException {
//        try (var updateStatement = connection.prepareStatement(UPDATE_SECTOR_AFTER_ROW_UPDATE_SQL)) {
//
//            Optional<Row> rowBeforeUpdate= findByIdBeforeUpdateOrDelete(connection, row.getId());
//
//            if(rowBeforeUpdate.isPresent()){
//                updateStatement.setInt(1, rowBeforeUpdate.get().getSeatsNumb());
//                updateStatement.setInt(2, row.getSeatsNumb());
//                updateStatement.setLong(3, row.getSector().getId());
//                updateStatement.executeUpdate();
//            }else{
//                throw new SQLException("Row not found with id: " + row.getId());
//            }
//        }
//    }
//    private Optional<Row> findByIdBeforeUpdateOrDelete(Connection connection, Long id) {
//        try(var statement = connection.prepareStatement(FIND_BY_ID_SQL)){
//            statement.setLong(1, id);
//            var result = statement.executeQuery();
//            Row row = null;
//            if (result.next()){
//                row = buildRow(result);
//            }
//            return Optional.ofNullable(row);
//        } catch (SQLException e) {
//            throw new DaoCrudException(e);
//        }
//    }
//}

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
     * Returns the singleton instance of {@link SectorDao}.
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
        FiltrationSqlQueryParameters filtrationSqlQueryParameters = buildSqlQuery(rowFilter);
        String sql = filtrationSqlQueryParameters.sql();
        List<Object> parameters = filtrationSqlQueryParameters.parameters();

        return executeFilterQuery(sql, parameters, sectorId, rowFilter);
    }

    private FiltrationSqlQueryParameters buildSqlQuery(RowFilter rowFilter) {
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
            throw new DaoCrudException(e);
        }
    }

    @Override
    public void save(Row row) {
        Transaction transaction = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            transaction = session.beginTransaction();
            session.save(row);
            updateSectorAfterRowSave(session, row);
            transaction.commit();
            log.info("Row saved: {}", row);
        } catch (HibernateException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            log.error("Failed to save row: {}", row);
            throw new DaoCrudException(e);
        } finally {
            session.close();
        }
    }

    @Override
    public void update(Row row) {
        Optional<Row> rowBeforeUpdate = findById(row.getId());
        if (rowBeforeUpdate.isEmpty()) {
            log.error("Failed to find row: {}", row);
            throw new DaoResourceNotFoundException("Row not found");
        }
        Transaction transaction = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            transaction = session.beginTransaction();
            updateSectorBeforeRowUpdate(session, rowBeforeUpdate.get(), row);
            session.update(row);
            transaction.commit();
            log.info("row updated: {}", row);
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            log.error("Failed to update row: {}", row);
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
            Row row = session.load(Row.class, id);
            if (row != null) {
                updateSectorAfterRowDelete(session, row);
                session.delete(row);
                transaction.commit();
                log.info("Row {} deleted with given id: {}", row, id);
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
    }

    private void updateSectorBeforeRowUpdate(Session session, Row rowBeforeUpdate, Row row) throws HibernateException {
        Query<?> query = session.createQuery("UPDATE Sector SET availableSeatsNumb = "
                + "availableSeatsNumb - :oldSeatsNumb + :newSeatsNumb WHERE id = :sectorId");
        query.setParameter("oldSeatsNumb", rowBeforeUpdate.getSeatsNumb());
        query.setParameter("newSeatsNumb", row.getSeatsNumb());
        query.setParameter("sectorId", row.getSector().getId());
        query.executeUpdate();
    }

    private void updateSectorAfterRowDelete(Session session, Row row) throws HibernateException {
        Query<?> query = session.createQuery("UPDATE Sector SET availableRowsNumb = availableRowsNumb - 1, "
                + "availableSeatsNumb = availableSeatsNumb - :seatsNumb WHERE id = :sectorId");
        query.setParameter("seatsNumb", row.getSeatsNumb());
        query.setParameter("sectorId", row.getSector().getId());
        query.executeUpdate();
    }
}