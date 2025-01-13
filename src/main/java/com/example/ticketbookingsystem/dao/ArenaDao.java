package com.example.ticketbookingsystem.dao;

import com.example.ticketbookingsystem.dto.ArenaFilter;
import com.example.ticketbookingsystem.entity.Arena;
import com.example.ticketbookingsystem.exception.CreateUpdateEntityException;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.utils.ConnectionManager;
import com.example.ticketbookingsystem.utils.FiltrationSqlQueryParameters;;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ArenaDao implements DaoCrud<Long, Arena>{
    private static final ArenaDao INSTANCE = new ArenaDao();
    private static final String SAVE_SQL = """
            INSERT INTO arena (name, city, capacity)
            VALUES (?, ?, ?)
            """;
    private static final String DELETE_SQL = """
            DELETE FROM arena WHERE id=?
            """;
    private static final String UPDATE_SQL = """
            UPDATE arena
            SET name=?,
                city=?,
                capacity=?
            WHERE id=?
            """;
    private static final String FIND_ALL_SQL = """
            SELECT id, name, city, capacity, general_seats_numb FROM arena
            """;

    private static final String FIND_ALL_CITIES_SQL = """
            SELECT DISTINCT city FROM arena
            """;

    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE id=?
            """;
    public static ArenaDao getInstance(){
        return INSTANCE;
    }
    private ArenaDao(){
    }
    public List<Arena> findAll(ArenaFilter arenaFilter) {
        FiltrationSqlQueryParameters filtrationSqlQueryParameters = buildSqlQuery(arenaFilter);
        String sql = filtrationSqlQueryParameters.sql();
        List<Object> parameters = filtrationSqlQueryParameters.parameters();

        return executeFilterQuery(sql, parameters);
    }

    private FiltrationSqlQueryParameters buildSqlQuery(ArenaFilter arenaFilter) {
        List<Object> parameters = new ArrayList<>();
        List<String> whereSql = new ArrayList<>();
        List<String> sortSql = new ArrayList<>();

        if (arenaFilter.city() != null) {
            parameters.add("%" + arenaFilter.city() + "%");
            whereSql.add("city LIKE ?");
        }
        if (!arenaFilter.capacitySortOrder().isEmpty()) {
            sortSql.add("capacity " + arenaFilter.capacitySortOrder());
        }
        if (!arenaFilter.seatsNumbSortOrder().isEmpty()) {
            sortSql.add("general_seats_numb " + arenaFilter.seatsNumbSortOrder());
        }

        var orderBy = sortSql.stream().collect(Collectors.joining(
                " , ",
                !sortSql.isEmpty() ? " ORDER BY " : " ",
                ""
        ));

        parameters.add(arenaFilter.limit());
        parameters.add(arenaFilter.offset());

        var where = whereSql.stream().collect(Collectors.joining(
                " AND ",
                parameters.size() > 2 ? " WHERE " : " ",
                orderBy + " LIMIT ? OFFSET ? "
        ));

        String sql = FIND_ALL_SQL + where;

        return new FiltrationSqlQueryParameters(sql, parameters);
    }
    private List<Arena> executeFilterQuery(String sql, List<Object> parameters) {
        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(sql)) {

            for (int i = 0; i < parameters.size(); i++) {
                statement.setObject(i + 1, parameters.get(i));
            }

            try (var result = statement.executeQuery()) {
                List<Arena> arenaList = new ArrayList<>();
                while (result.next()) {
                    arenaList.add(buildArena(result));
                }
                return arenaList;
            }
        } catch (SQLException e) {
            throw new DaoCrudException(e);
        }
    }
    @Override
    public List<Arena> findAll() {
        try(var connection = ConnectionManager.get();
            var statement = connection.prepareStatement(FIND_ALL_SQL)){
            List<Arena> arenasList = new ArrayList<>();

            var result = statement.executeQuery();
            while (result.next()){
                arenasList.add(buildArena(result));
            }
            return arenasList;
        } catch (SQLException e) {
            throw new DaoCrudException(e);
        }
    }

    @Override
    public Optional<Arena> findById(Long id) {
        try(var connection = ConnectionManager.get();
            var statement = connection.prepareStatement(FIND_BY_ID_SQL)){
            statement.setLong(1, id);
            var result = statement.executeQuery();
            Arena arena = null;
            if (result.next()){
                arena = buildArena(result);
            }
            return Optional.ofNullable(arena);
        } catch (SQLException e) {
            throw new DaoCrudException(e);
        }
    }
    @Override
    public Arena save(Arena arena) {
        try(var connection = ConnectionManager.get();
            var statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)){
            setStatement(arena, statement);

            statement.executeUpdate();
            var keys = statement.getGeneratedKeys();
            if(keys.next()){
                arena.setId(keys.getLong("id"));
            }

            return arena;
        } catch (SQLException e) {
            throw new CreateUpdateEntityException(e.getMessage());
        }
    }
    @Override
    public boolean update(Arena arena) {
        try(var connection = ConnectionManager.get();
            var statement = connection.prepareStatement(UPDATE_SQL)){
            setStatement(arena, statement);
            statement.setLong(4, arena.getId());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new CreateUpdateEntityException(e.getMessage());
        }
    }
    @Override
    public boolean delete(Long id) {
        try(var connection = ConnectionManager.get();
            var statement = connection.prepareStatement(DELETE_SQL)){
            statement.setLong(1, id);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new CreateUpdateEntityException(e.getMessage());
        }
    }
    private Arena buildArena(ResultSet result) throws SQLException {
        return Arena.builder()
                .id(result.getLong("id"))
                .name(result.getString("name"))
                .city(result.getString("city"))
                .capacity(result.getInt("capacity"))
                .generalSeatsNumb(result.getInt("general_seats_numb"))
                .build();
    }
    private void setStatement(Arena arena, PreparedStatement statement) throws SQLException {
        statement.setString(1, arena.getName());
        statement.setString(2, arena.getCity());
        statement.setInt(3, arena.getCapacity());
    }

    public List<String> findAllArenasCities() {
        try(var connection = ConnectionManager.get();
            var statement = connection.prepareStatement(FIND_ALL_CITIES_SQL)){
            List<String> cities = new ArrayList<>();

            var result = statement.executeQuery();
            while (result.next()){
                cities.add(result.getString("city"));
            }
            return cities;
        } catch (SQLException e) {
            throw new DaoCrudException(e);
        }
    }
}
