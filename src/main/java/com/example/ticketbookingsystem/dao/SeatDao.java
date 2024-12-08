package com.example.ticketbookingsystem.dao;

import com.example.ticketbookingsystem.entity.Seat;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.service.RowService;
import com.example.ticketbookingsystem.utils.ConnectionManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SeatDao implements DaoCrud<Long, Seat>{
    private final static SeatDao INSTANCE = new SeatDao();
    private final static RowService rowService = RowService.getInstance();
    private final static String FIND_ALL_SQL = """
            SELECT id, row_id, seat_number FROM seat s
            """;

    private final static String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE s.id=?
            """;
    private final static String SELECT_SEATS = """
            SELECT s.*
            FROM seat s
            JOIN row r ON s.row_id = r.id
            JOIN sector sec ON r.sector_id = sec.id
            JOIN arena a ON sec.arena_id = a.id
            JOIN sport_event e ON a.id = e.arena_id
            """;
    private final static String FIND_ALL_BY_EVENT_ID_SQL = SELECT_SEATS + """
            WHERE e.id = ?
            ORDER BY sec.sector_name, r.id, s.id;
            """;
    private final static String FIND_BY_EVENT_ID_WITH_NO_TICKETS_SQL = SELECT_SEATS + """
            WHERE e.id = ? AND has_ticket!=true
            ORDER BY sec.sector_name, r.id, s.id;
            """;
    private final static String FIND_BY_EVENT_ID_WHEN_UPDATE_SQL = SELECT_SEATS + """
            WHERE e.id = ? AND (has_ticket!=true OR s.id = ?)
            ORDER BY sec.sector_name, r.id, s.id;
            """;

    public static SeatDao getInstance(){
        return INSTANCE;
    }

    private SeatDao(){
    }

    @Override
    public List<Seat> findAll() {
        try(var connection = ConnectionManager.get();
            var statement = connection.prepareStatement(FIND_ALL_SQL)){
            List<Seat> seatList = new ArrayList<>();

            var result = statement.executeQuery();
            while (result.next()){
                seatList.add(buildSeat(result));
            }
            return seatList;
        } catch (SQLException e) {
            throw new DaoCrudException(e);
        }
    }

    public List<Seat> findSeatsByQuery(String sql, Long eventId) {
        try(var connection = ConnectionManager.get();
            var statement = connection.prepareStatement(sql)){
            List<Seat> seatList = new ArrayList<>();

            statement.setLong(1, eventId);

            var result = statement.executeQuery();
            while (result.next()){
                seatList.add(buildSeat(result));
            }
            return seatList;
        } catch (SQLException e) {
            throw new DaoCrudException(e);
        }
    }

    public List<Seat> findAllByEventId(Long eventId) {
        return findSeatsByQuery(FIND_ALL_BY_EVENT_ID_SQL, eventId);
    }

    public List<Seat> findByEventIdWithNoTickets(Long eventId) {
        return findSeatsByQuery(FIND_BY_EVENT_ID_WITH_NO_TICKETS_SQL, eventId);
    }

    public List<Seat> findAllByEventIdWhenUpdate(Long eventId, Long seatId) {
        try(var connection = ConnectionManager.get();
            var statement = connection.prepareStatement(FIND_BY_EVENT_ID_WHEN_UPDATE_SQL)){
            List<Seat> seatList = new ArrayList<>();

            statement.setLong(1, eventId);
            statement.setLong(2, seatId);

            var result = statement.executeQuery();
            while (result.next()){
                seatList.add(buildSeat(result));
            }
            return seatList;
        } catch (SQLException e) {
            throw new DaoCrudException(e);
        }
    }

    @Override
    public Optional<Seat> findById(Long id) {
        try(var connection = ConnectionManager.get();
            var statement = connection.prepareStatement(FIND_BY_ID_SQL)){
            statement.setLong(1, id);
            var result = statement.executeQuery();
            Seat seat = null;
            if (result.next()){
                seat = buildSeat(result);
            }
            return Optional.ofNullable(seat);
        } catch (SQLException e) {
            throw new DaoCrudException(e);
        }
    }

    private Seat buildSeat(ResultSet result) throws SQLException {
        return Seat.builder()
                .id(result.getLong("id"))
                .seatNumber(result.getInt("seat_number"))
                .row(rowService.findById((long) result.getInt("row_id")).get())
                .build();
    }
}
