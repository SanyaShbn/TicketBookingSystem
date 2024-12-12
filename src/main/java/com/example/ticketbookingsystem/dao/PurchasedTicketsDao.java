package com.example.ticketbookingsystem.dao;

import com.example.ticketbookingsystem.dto.PurchasedTicketDto;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.utils.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PurchasedTicketsDao {
    private final static PurchasedTicketsDao INSTANCE = new PurchasedTicketsDao();

    private final static String UPDATE_TICKET_TABLE_SQL = """
            UPDATE ticket SET status = 'SOLD' WHERE id = ?
            """;

    private final static String SAVE_SQL = """
            INSERT INTO purchased_tickets (user_id, ticket_id) VALUES (?, ?)
            """;
    private final static String FIND_ALL_BY_USER_ID_SQL = """
            SELECT t.id as ticket_id, e.event_name, e.event_date_time, a.name as arena_name, a.city as arena_city,
                            s.sector_name, r.row_number, se.seat_number, t.price
                            FROM purchased_tickets pt
                            JOIN ticket t ON pt.ticket_id = t.id
                            JOIN sport_event e ON t.event_id = e.id
                            JOIN seat se ON t.seat_id = se.id
                            JOIN row r ON se.row_id = r.id
                            JOIN sector s ON r.sector_id = s.id
                            JOIN arena a ON e.arena_id = a.id
                            WHERE pt.user_id = ?
            """;

    public static PurchasedTicketsDao getInstance(){
        return INSTANCE;
    }
    private PurchasedTicketsDao(){
    }

    public void save(List<Long> ticketIds, Long userId) {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement updateStatement = connection.prepareStatement(UPDATE_TICKET_TABLE_SQL)) {
            for (Long ticketId : ticketIds) {
                updateStatement.setLong(1, ticketId);
                updateStatement.executeUpdate();

                try (PreparedStatement insertPurchasedTicket = connection.prepareStatement(SAVE_SQL)) {
                    insertPurchasedTicket.setLong(1, userId);
                    insertPurchasedTicket.setLong(2, ticketId);
                    insertPurchasedTicket.executeUpdate();
                }
            }
        }catch (SQLException e){
            throw new DaoCrudException(e);
        }
    }

    public List<PurchasedTicketDto> findAllByUserId(Long userId) {
        List<PurchasedTicketDto> tickets = new ArrayList<>();
        try (Connection connection = ConnectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_BY_USER_ID_SQL)) {
            preparedStatement.setLong(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    PurchasedTicketDto ticket = PurchasedTicketDto.builder()
                            .ticketId(resultSet.getLong("ticket_id"))
                            .eventName(resultSet.getString("event_name"))
                            .eventDateTime(resultSet.getTimestamp("event_date_time").toLocalDateTime())
                            .arenaName(resultSet.getString("arena_name"))
                            .arenaCity(resultSet.getString("arena_city"))
                            .sectorName(resultSet.getString("sector_name"))
                            .rowNumber(resultSet.getInt("row_number"))
                            .seatNumber(resultSet.getInt("seat_number"))
                            .price(resultSet.getBigDecimal("price"))
                            .build();

                    tickets.add(ticket);
                }
            }
        }catch (SQLException e){
            throw new DaoCrudException(e);
        }
        return tickets;
    }
}
