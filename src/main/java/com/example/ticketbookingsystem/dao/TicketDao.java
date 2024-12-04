package com.example.ticketbookingsystem.dao;

import com.example.ticketbookingsystem.entity.Ticket;
import com.example.ticketbookingsystem.entity.TicketStatus;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.service.SeatService;
import com.example.ticketbookingsystem.service.SportEventService;
import com.example.ticketbookingsystem.utils.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TicketDao implements DaoCrud<Long, Ticket>{
    private final static TicketDao INSTANCE = new TicketDao();
    private final static SportEventService sportEventService = SportEventService.getInstance();

    private final static SeatService seatService = SeatService.getInstance();
    private final static String SAVE_SQL = """ 
            INSERT INTO ticket (status, price, event_id, seat_id)
            VALUES (?, ?, ?, ?);
    """;

    private final static String UPDATE_SEAT_SQL = """
            UPDATE seat
            SET has_ticket = true
            WHERE id = ?;
    """;

    private final static String DELETE_SQL = """
            UPDATE seat
            set has_ticket=false
            WHERE id=( SELECT seat_id
                       FROM ticket
                       WHERE id=?
                       );
                       
            UPDATE ticket
            SET event_id=null,
                seat_id=null
            WHERE id=?;
            
            DELETE FROM ticket WHERE id=?
            """;
    private final static String UPDATE_SQL = """
            
            UPDATE seat
            set has_ticket=false
            WHERE id=( SELECT seat_id
                       FROM ticket
                       WHERE id=?
                       );
            
            UPDATE seat
            set has_ticket=true
            WHERE id=?;
            
            UPDATE ticket
            SET status=?,
                price=?,
                event_id=?,
                seat_id=?
            WHERE id=?
            """;
    private final static String FIND_ALL_SQL = """
            SELECT id, status, price, event_id, seat_id FROM ticket
            """;

    private final static String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE id=?
            """;

    private final static String FIND_ALL_BY_EVENT_ID_SQL = FIND_ALL_SQL + """
            WHERE event_id=?
            """;

    public static TicketDao getInstance(){
        return INSTANCE;
    }
    private TicketDao(){
    }

    @Override
    public List<Ticket> findAll() {
        try(var connection = ConnectionManager.get();
            var statement = connection.prepareStatement(FIND_ALL_SQL)){
            List<Ticket> ticketList = new ArrayList<>();

            var result = statement.executeQuery();
            while (result.next()){
                ticketList.add(buildTicket(result));
            }
            return ticketList;
        } catch (SQLException e) {
            throw new DaoCrudException(e);
        }
    }

    @Override
    public Optional<Ticket> findById(Long id) {
        try(var connection = ConnectionManager.get();
            var statement = connection.prepareStatement(FIND_BY_ID_SQL)){
            statement.setLong(1, id);
            var result = statement.executeQuery();
            Ticket ticket = null;
            if (result.next()){
                ticket = buildTicket(result);
            }
            return Optional.ofNullable(ticket);
        } catch (SQLException e) {
            throw new DaoCrudException(e);
        }
    }

    public List<Ticket> findAllByEventId(Long id){
        try(var connection = ConnectionManager.get();
            var statement = connection.prepareStatement(FIND_ALL_BY_EVENT_ID_SQL)){
            List<Ticket> ticketList = new ArrayList<>();
            statement.setLong(1, id);
            var result = statement.executeQuery();
            while (result.next()){
                ticketList.add(buildTicket(result));
            }
            return ticketList;
        } catch (SQLException e) {
            throw new DaoCrudException(e);
        }
    }

    @Override
    public Ticket save(Ticket ticket) {
        try(var connection = ConnectionManager.get();
            var statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS);
            var updateSeatTable = connection.prepareStatement(UPDATE_SEAT_SQL)){
            setSaveStatement(ticket, statement);
            statement.executeUpdate();
            var keys = statement.getGeneratedKeys();
            if(keys.next()){
                ticket.setId(keys.getLong("id"));
            }

            updateSeatTable.setLong(1, ticket.getSeat().getId());
            updateSeatTable.executeUpdate();

            return ticket;
        } catch (SQLException e) {
            throw new DaoCrudException(e);
        }
    }

    @Override
    public boolean update(Ticket ticket) {
        try(var connection = ConnectionManager.get();
            var statement = connection.prepareStatement(UPDATE_SQL)){
            setUpdateStatement(ticket, statement);
            statement.setLong(7, ticket.getId());
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
            statement.setLong(2, id);
            statement.setLong(3, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoCrudException(e);
        }
    }

    private Ticket buildTicket(ResultSet result) throws SQLException {
        return Ticket.builder()
                .id(result.getLong("id"))
                .status(TicketStatus.valueOf(result.getString("status")))
                .price(result.getBigDecimal("price"))
                .sportEvent(sportEventService.findById((long) result.getInt("event_id")).get())
                .seat(seatService.findById((long) result.getInt("seat_id")).get())
                .build();
    }
    private void setSaveStatement(Ticket ticket, PreparedStatement statement) throws SQLException {
        statement.setString(1, String.valueOf(ticket.getStatus()));
        statement.setBigDecimal(2, ticket.getPrice());
        statement.setLong(3, ticket.getSportEvent().getId());
        statement.setLong(4, ticket.getSeat().getId());
    }

    private void setUpdateStatement(Ticket ticket, PreparedStatement statement) throws SQLException {

        statement.setLong(1, ticket.getId());
        statement.setLong(2, ticket.getSeat().getId());

        statement.setString(3, String.valueOf(ticket.getStatus()));
        statement.setBigDecimal(4, ticket.getPrice());
        statement.setLong(5, ticket.getSportEvent().getId());
        statement.setLong(6, ticket.getSeat().getId());
    }
}
