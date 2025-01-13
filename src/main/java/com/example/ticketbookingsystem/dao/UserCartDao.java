package com.example.ticketbookingsystem.dao;

import com.example.ticketbookingsystem.entity.TicketStatus;
import com.example.ticketbookingsystem.entity.UserCart;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.utils.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserCartDao {
    private static final UserCartDao INSTANCE = new UserCartDao();
    private static final String SAVE_SQL = """
            INSERT INTO user_cart (user_id, ticket_id) VALUES (?, ?)
            """;
    private static final String DELETE_SQL = """
            DELETE FROM user_cart WHERE user_id = ? AND ticket_id = ?
            """;
    private static final String UPDATE_TICKET_STATUS_SQL = """
            UPDATE ticket SET status = ? WHERE id = ?
            """;
    private static final String CLEAR_USER_CART_SQL = """
            DELETE FROM user_cart WHERE user_id = ?
            """;
    private static final String UPDATE_ALL_TICKET_STATUS_SQL = """
            UPDATE ticket SET status = 'AVAILABLE'
            WHERE id IN (SELECT ticket_id FROM user_cart WHERE user_id = ?) AND status = 'RESERVED'
            """;

    private static final String GET_TICKET_IDS = """
            SELECT ticket_id FROM user_cart WHERE user_id = ?
            """;
    public static UserCartDao getInstance(){
        return INSTANCE;
    }
    private UserCartDao(){
    }

    public void save(UserCart userCart) {
        try (var connection = ConnectionManager.get();
             PreparedStatement updateStatement = connection.prepareStatement(SAVE_SQL);
             PreparedStatement updateTicketStatus = connection.prepareStatement(UPDATE_TICKET_STATUS_SQL)) {
            updateStatement.setLong(1, userCart.getUser().getId());
            updateStatement.setLong(2, userCart.getTicket().getId());
            updateStatement.executeUpdate();

            updateTicketStatus.setString(1, String.valueOf(TicketStatus.RESERVED));
            updateTicketStatus.setLong(2, userCart.getTicket().getId());
            updateTicketStatus.executeUpdate();
        }catch (SQLException e) {
            throw new DaoCrudException(e);
        }
    }

    public void delete(UserCart userCart){
        try (var connection = ConnectionManager.get();
             PreparedStatement updateStatement = connection.prepareStatement(DELETE_SQL);
             PreparedStatement updateTicketStatus = connection.prepareStatement(UPDATE_TICKET_STATUS_SQL)) {
            updateStatement.setLong(1, userCart.getUser().getId());
            updateStatement.setLong(2, userCart.getTicket().getId());
            updateStatement.executeUpdate();

            updateTicketStatus.setString(1, String.valueOf(TicketStatus.AVAILABLE));
            updateTicketStatus.setLong(2, userCart.getTicket().getId());
            updateTicketStatus.executeUpdate();

        }catch (SQLException e) {
            throw new DaoCrudException(e);
        }
    }

    public void clearCart(Long userId) {
        try (var connection = ConnectionManager.get();
             PreparedStatement clearCartStatement = connection.prepareStatement(CLEAR_USER_CART_SQL);
             PreparedStatement updateTicketStatus = connection.prepareStatement(UPDATE_ALL_TICKET_STATUS_SQL)) {

            updateTicketStatus.setLong(1, userId);
            updateTicketStatus.executeUpdate();

            clearCartStatement.setLong(1, userId);
            clearCartStatement.executeUpdate();
        }catch (SQLException e) {
            throw new DaoCrudException(e);
        }
    }

    public List<Long> getTicketIdsFromUserCart(Long userId){
        List<Long> ticketIds = new ArrayList<>();
        try (var connection = ConnectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_TICKET_IDS)) {
            preparedStatement.setLong(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    ticketIds.add(resultSet.getLong("ticket_id"));
                }
            }
        }catch (SQLException e) {
            throw new DaoCrudException(e);
        }
        return ticketIds;
    }
}
