package com.example.ticketbookingsystem.dao;

import com.example.ticketbookingsystem.dto.SectorFilter;
import com.example.ticketbookingsystem.entity.Sector;
import com.example.ticketbookingsystem.exception.CreateUpdateEntityException;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.service.ArenaService;
import com.example.ticketbookingsystem.utils.ConnectionManager;
import com.example.ticketbookingsystem.utils.FiltrationSqlQueryParameters;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SectorDao implements DaoCrud<Long, Sector>{
    private static final SectorDao INSTANCE = new SectorDao();
    private static final ArenaService arenaService = ArenaService.getInstance();
    private static final String SAVE_SQL = """
            INSERT INTO sector (sector_name, arena_id, max_rows_numb,
            max_seats_numb)
            VALUES (?, ?, ?, ?)
            """;
    private static final String DELETE_SQL = """
            DELETE FROM sector WHERE id=?
            """;
    private static final String UPDATE_SQL = """
            UPDATE sector
            SET sector_name=?,
                arena_id=?,
                max_rows_numb=?,
                max_seats_numb=?
            WHERE id=?
            """;
    private static final String FIND_ALL_SQL = """
            SELECT s.id, s.sector_name, s.arena_id, s.max_rows_numb, s.available_rows_numb,
            s.max_seats_numb, s.available_seats_numb
            FROM public.sector s
            JOIN public.arena a on a.id = s.arena_id
            """;

    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE s.id=?
            """;

    private static final String FIND_ALL_BY_ARENA_ID_SQL = FIND_ALL_SQL + """
            WHERE s.arena_id=?
            """;

    private static final String UPDATE_ARENA_AFTER_SECTOR_SAVE_SQL = """
            UPDATE arena
            SET general_seats_numb=general_seats_numb + ?
            WHERE id=?
            """;
    private static final String UPDATE_ARENA_AFTER_SECTOR_UPDATE_SQL = """
            UPDATE arena
            SET general_seats_numb=arena.general_seats_numb - ? + ?
            WHERE id=?
            """;
    private static final String UPDATE_ARENA_AFTER_SECTOR_DELETE_SQL = """
            UPDATE arena
            SET general_seats_numb=general_seats_numb - ?
            WHERE id=?
            """;
    public static SectorDao getInstance(){
        return INSTANCE;
    }
    private SectorDao(){
    }

    public List<Sector> findAll(SectorFilter sectorFilter, Long arenaId) {
        FiltrationSqlQueryParameters filtrationSqlQueryParameters = buildSqlQuery(sectorFilter);
        String sql = filtrationSqlQueryParameters.sql();
        List<Object> parameters = filtrationSqlQueryParameters.parameters();

        return executeFilterQuery(sql, parameters, arenaId);
    }

    private FiltrationSqlQueryParameters buildSqlQuery(SectorFilter sectorFilter) {
        List<Object> parameters = new ArrayList<>();
        List<String> sortSql = new ArrayList<>();

        if (!sectorFilter.nameSortOrder().isEmpty()) {
            sortSql.add("sector_name " + sectorFilter.nameSortOrder());
        }
        if (!sectorFilter.maxRowsNumbSortOrder().isEmpty()) {
            sortSql.add("max_rows_numb " + sectorFilter.maxRowsNumbSortOrder());
        }
        if (!sectorFilter.maxSeatsNumbSortOrder().isEmpty()) {
            sortSql.add("max_seats_numb " + sectorFilter.maxSeatsNumbSortOrder());
        }

        var orderBy = sortSql.stream().collect(Collectors.joining(
                " , ",
                !sortSql.isEmpty() ? " ORDER BY " : " ",
                " LIMIT ? OFFSET ? "
        ));

        parameters.add(sectorFilter.limit());
        parameters.add(sectorFilter.offset());

        String sql = FIND_ALL_BY_ARENA_ID_SQL + orderBy;

        return new FiltrationSqlQueryParameters(sql, parameters);
    }
    private List<Sector> executeFilterQuery(String sql, List<Object> parameters, Long arenaId) {
        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(sql)) {

            statement.setLong(1, arenaId);

            for (int i = 1; i <= parameters.size(); i++) {
                statement.setObject(i + 1, parameters.get(i - 1));
            }

            try (var result = statement.executeQuery()) {
                List<Sector> ticketList = new ArrayList<>();
                while (result.next()) {
                    ticketList.add(buildSector(result));
                }
                return ticketList;
            }
        } catch (SQLException e) {
            throw new DaoCrudException(e);
        }
    }

    @Override
    public List<Sector> findAll() {
        try(var connection = ConnectionManager.get();
            var statement = connection.prepareStatement(FIND_ALL_SQL)){
            List<Sector> sectorsList = new ArrayList<>();

            var result = statement.executeQuery();
            while (result.next()){
                sectorsList.add(buildSector(result));
            }
            return sectorsList;
        } catch (SQLException e) {
            throw new DaoCrudException(e);
        }
    }
    @Override
    public Optional<Sector> findById(Long id) {
        try(var connection = ConnectionManager.get();
            var statement = connection.prepareStatement(FIND_BY_ID_SQL)){
            statement.setLong(1, id);
            var result = statement.executeQuery();
            Sector sector = null;
            if (result.next()){
                sector = buildSector(result);
            }
            return Optional.ofNullable(sector);
        } catch (SQLException e) {
            throw new DaoCrudException(e);
        }
    }
    public List<Sector> findAllByArenaId(Long id){
        try(var connection = ConnectionManager.get();
            var statement = connection.prepareStatement(FIND_ALL_BY_ARENA_ID_SQL)){
            List<Sector> sectorList = new ArrayList<>();
            statement.setLong(1, id);
            var result = statement.executeQuery();
            while (result.next()){
                sectorList.add(buildSector(result));
            }
            return sectorList;
        } catch (SQLException e) {
            throw new DaoCrudException(e);
        }
    }
    @Override
    public Sector save(Sector sector) {
        try(var connection = ConnectionManager.get()){
            connection.setAutoCommit(false);
            try{
                performSave(sector, connection);
                updateArenaAfterSectorSave(connection, sector);
                connection.commit();
            }catch (SQLException e) {
                handleRollback(connection);
                throw new CreateUpdateEntityException(e.getMessage());
            } finally {
                resetAutoCommit(connection);
            }
            return sector;
        } catch (SQLException e) {
            throw new DaoCrudException(e);
        }
    }
    private void performSave(Sector sector, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            setStatement(sector, statement);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    sector.setId(keys.getLong("id"));
                }
            }
        }
    }
    @Override
    public boolean update(Sector sector) {
        boolean isSuccessful = false;
        try (Connection connection = ConnectionManager.get()) {
            connection.setAutoCommit(false);
            try {
                isSuccessful = performUpdate(sector, connection);
                if (isSuccessful) {
                    connection.commit();
                } else {
                    connection.rollback();
                }
            } catch (SQLException e) {
                handleRollback(connection);
                throw new CreateUpdateEntityException(e.getMessage());
            } finally {
                resetAutoCommit(connection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isSuccessful;
    }

    private boolean performUpdate(Sector sector, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {
            setStatement(sector, statement);
            statement.setLong(5, sector.getId());
            updateArenaAfterSectorUpdate(connection, sector);
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(Long id) {
        boolean isSuccessful = false;
        try (Connection connection = ConnectionManager.get()) {
            connection.setAutoCommit(false);
            try {
                isSuccessful = performDelete(id, connection);
                if (isSuccessful) {
                    connection.commit();
                } else {
                    connection.rollback();
                }
            } catch (SQLException e) {
                handleRollback(connection);
                throw new DaoCrudException(e);
            } finally {
                resetAutoCommit(connection);
            }
        } catch (SQLException e) {
            throw new DaoCrudException(e);
        }
        return isSuccessful;
    }

    private boolean performDelete(Long id, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_SQL)) {
            statement.setLong(1, id);
            updateArenaAfterSectorDelete(connection, id);
            return statement.executeUpdate() > 0;
        }
    }
    private void handleRollback(Connection connection) {
        try {
            if (connection != null) {
                connection.rollback();
            }
        } catch (SQLException rollbackEx) {
            rollbackEx.printStackTrace();
        }
    }
    private void resetAutoCommit(Connection connection) {
        try {
            if (connection != null) {
                connection.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    private Sector buildSector(ResultSet result) throws SQLException {
        return new Sector(
                result.getLong("id"),
                result.getString("sector_name"),
                arenaService.findById((long) result.getInt("arena_id")).get(),
                result.getInt("max_rows_numb"),
                result.getInt("available_rows_numb"),
                result.getInt("max_seats_numb"),
                result.getInt("available_seats_numb")
        );
    }
    private void setStatement(Sector sector, PreparedStatement statement) throws SQLException {
        statement.setString(1, sector.getSectorName());
        statement.setLong(2, sector.getArena().getId());
        statement.setInt(3, sector.getMaxRowsNumb());
        statement.setInt(4, sector.getMaxSeatsNumb());
    }
    private void updateArenaAfterSectorSave(Connection connection, Sector sector) throws SQLException {
        try (var updateStatement = connection.prepareStatement(UPDATE_ARENA_AFTER_SECTOR_SAVE_SQL)) {
            updateStatement.setInt(1, sector.getMaxSeatsNumb());
            updateStatement.setLong(2, sector.getArena().getId());
            updateStatement.executeUpdate();
        }
    }
    private void updateArenaAfterSectorUpdate(Connection connection, Sector sector) throws SQLException {
        try (var updateStatement = connection.prepareStatement(UPDATE_ARENA_AFTER_SECTOR_UPDATE_SQL)) {

            Optional<Sector> sectorBeforeUpdate= findByIdBeforeUpdateOrDelete(connection, sector.getId());

            if(sectorBeforeUpdate.isPresent()){
                updateStatement.setInt(1, sectorBeforeUpdate.get().getMaxSeatsNumb());
                updateStatement.setInt(2, sector.getMaxSeatsNumb());
                updateStatement.setLong(3, sector.getArena().getId());
                updateStatement.executeUpdate();
            }else{
                throw new SQLException("Row not found with id: " + sector.getId());
            }
        }
    }
    private void updateArenaAfterSectorDelete(Connection connection, Long id) {
        try (var updateStatement = connection.prepareStatement(UPDATE_ARENA_AFTER_SECTOR_DELETE_SQL)) {

            Optional<Sector> sectorBeforeDelete = findByIdBeforeUpdateOrDelete(connection, id);

            if(sectorBeforeDelete.isPresent()){
                updateStatement.setInt(1, sectorBeforeDelete.get().getMaxSeatsNumb());
                updateStatement.setLong(2, sectorBeforeDelete.get().getArena().getId());
                updateStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DaoCrudException(e);
        }
    }
    private Optional<Sector> findByIdBeforeUpdateOrDelete(Connection connection, Long id) {
        try(var statement = connection.prepareStatement(FIND_BY_ID_SQL)){
            statement.setLong(1, id);
            var result = statement.executeQuery();
            Sector sector = null;
            if (result.next()){
                sector = buildSector(result);
            }
            return Optional.ofNullable(sector);
        } catch (SQLException e) {
            throw new DaoCrudException(e);
        }
    }
}
