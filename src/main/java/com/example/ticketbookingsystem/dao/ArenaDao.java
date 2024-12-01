package com.example.ticketbookingsystem.dao;

import com.example.ticketbookingsystem.entity.Arena;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.utils.ConnectionManager;;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ArenaDao implements DaoCrud<Long, Arena>{
    private final static ArenaDao INSTANCE = new ArenaDao();
    private final static String SAVE_SQL = """
            INSERT INTO arena (name, city, capacity)
            VALUES (?, ?, ?)
            """;
    private final static String DELETE_SQL = """
            DELETE FROM arena WHERE id=?
            """;
    private final static String UPDATE_SQL = """
            UPDATE arena
            SET name=?,
                city=?,
                capacity=?
            WHERE id=?
            """;
    private final static String FIND_ALL_SQL = """
            SELECT id, name, city, capacity FROM arena
            """;

    private final static String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE id=?
            """;
    public static ArenaDao getInstance(){
        return INSTANCE;
    }
    private ArenaDao(){
    }
    private Arena buildArena(ResultSet result) throws SQLException {
        return new Arena(
                result.getLong("id"),
                result.getString("name"),
                result.getString("city"),
                result.getInt("capacity")
        );
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
    private void setStatement(Arena arena, PreparedStatement statement) throws SQLException {
        statement.setString(1, arena.getName());
        statement.setString(2, arena.getCity());
        statement.setInt(3, arena.getCapacity());
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
            throw new DaoCrudException(e);
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
            throw new DaoCrudException(e);
        }
    }
    @Override
    public boolean delete(Long id) {
        try(var connection = ConnectionManager.get();
            var statement = connection.prepareStatement(DELETE_SQL)){
            statement.setLong(1, id);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoCrudException(e);
        }
    }
}
