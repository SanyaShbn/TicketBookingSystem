package com.example.ticketbookingsystem.dao;

import com.example.ticketbookingsystem.entity.SportEvent;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.service.ArenaService;
import com.example.ticketbookingsystem.utils.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SportEventDao implements DaoCrud<Long, SportEvent>{
    private final static SportEventDao INSTANCE = new SportEventDao();
    private final static ArenaService arenaService = ArenaService.getInstance();
    private final static String SAVE_SQL = """
            INSERT INTO sport_event (event_name, event_date_time, arena_id)
            VALUES (?, ?, ?)
            """;
    private final static String DELETE_SQL = """
            DELETE FROM sport_event WHERE id=?
            """;
    private final static String UPDATE_SQL = """
            UPDATE sport_event
            SET event_name=?,
                event_date_time=?,
                arena_id=?
            WHERE id=?
            """;
    private final static String FIND_ALL_SQL = """
            SELECT id, event_name, event_date_time, arena_id FROM sport_event
            """;

    private final static String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE id=?
            """;
    public static SportEventDao getInstance(){
        return INSTANCE;
    }
    private SportEventDao(){
    }
    @Override
    public List<SportEvent> findAll() {
        try(var connection = ConnectionManager.get();
            var statement = connection.prepareStatement(FIND_ALL_SQL)){
            List<SportEvent> sportEventList = new ArrayList<>();

            var result = statement.executeQuery();
            while (result.next()){
                sportEventList.add(buildSportEvent(result));
            }
            return sportEventList;
        } catch (SQLException e) {
            throw new DaoCrudException(e);
        }
    }

    @Override
    public Optional<SportEvent> findById(Long id) {
        try(var connection = ConnectionManager.get();
            var statement = connection.prepareStatement(FIND_BY_ID_SQL)){
            statement.setLong(1, id);
            var result = statement.executeQuery();
            SportEvent sportEvent = null;
            if (result.next()){
                sportEvent = buildSportEvent(result);
            }
            return Optional.ofNullable(sportEvent);
        } catch (SQLException e) {
            throw new DaoCrudException(e);
        }
    }

    @Override
    public SportEvent save(SportEvent sportEvent) {
        try(var connection = ConnectionManager.get();
            var statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)){
            setStatement(sportEvent, statement);
            statement.executeUpdate();
            var keys = statement.getGeneratedKeys();
            if(keys.next()){
                sportEvent.setId(keys.getLong("id"));
            }
            return sportEvent;
        } catch (SQLException e) {
            throw new DaoCrudException(e);
        }
    }

    @Override
    public boolean update(SportEvent sportEvent) {
        try(var connection = ConnectionManager.get();
            var statement = connection.prepareStatement(UPDATE_SQL)){
            setStatement(sportEvent, statement);
            statement.setLong(4, sportEvent.getId());
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

    private SportEvent buildSportEvent(ResultSet result) throws SQLException {
        return SportEvent.builder()
                .id(result.getLong("id"))
                .eventName(result.getString("event_name"))
                .eventDateTime(result.getTimestamp("event_date_time").toLocalDateTime())
                .arena(arenaService.findById((long) result.getInt("arena_id")).get())
                .build();
    }
    private void setStatement(SportEvent sportEvent, PreparedStatement statement) throws SQLException {
        statement.setString(1, sportEvent.getEventName());
        statement.setTimestamp(2, Timestamp.valueOf(sportEvent.getEventDateTime()));
        statement.setLong(3, sportEvent.getArena().getId());
    }
}
