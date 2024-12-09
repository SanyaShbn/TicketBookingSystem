package com.example.ticketbookingsystem.dao;

import com.example.ticketbookingsystem.entity.User;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.utils.ConnectionManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class UserDao implements DaoCrud<Long, User>{
    private static final UserDao INSTANCE = new UserDao();
    private static final String SAVE_SQL = "INSERT INTO users (email, password) VALUES (?, ?)";
    private static final String GET_BY_EMAIL_SQL = "SELECT * from users WHERE email=?";
    private static final String GET_USER_ROLE_SQL = """
        SELECT role_name FROM role
        JOIN user_roles ON role.id = user_roles.role_id
        WHERE user_roles.user_id = ?
        """;
    private static final String ASSIGN_ROLE_SQL = """ 
            INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)
            """;
    private static final String FIND_ROLE_ID_SQL = """ 
            SELECT id FROM role WHERE role_name=?
            """;

    public static UserDao getInstance(){
        return INSTANCE;
    }
    private UserDao() {}

    public Optional<User> findByEmail(String email){
        try(var connection = ConnectionManager.get();
            var statement = connection.prepareStatement(GET_BY_EMAIL_SQL)){

            statement.setString(1, email);

            var resultSet = statement.executeQuery();
            User user = null;
            if(resultSet.next()){
                user = buildUser(resultSet);
            }
            return Optional.ofNullable(user);
        } catch (SQLException e) {
            throw new DaoCrudException(e);
        }
    }

    public String getUserRole(Long userId) {
        String role = null;
        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(GET_USER_ROLE_SQL)) {

            statement.setLong(1, userId);
            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                role = resultSet.getString("role_name");
            }
        } catch (SQLException e) {
            throw new DaoCrudException(e);
        }
        return role;
    }

    @Override
    public User save(User user) {
        try(var connection = ConnectionManager.get();
            var statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS);
            var findRoleStatement = connection.prepareStatement(FIND_ROLE_ID_SQL)
            ){
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getPassword());
            statement.executeUpdate();
            var keys = statement.getGeneratedKeys();
            if(keys.next()){
                user.setId(keys.getLong("id"));
            }

            findRoleStatement.setString(1, "USER");
            var resultSet = findRoleStatement.executeQuery();
            if (resultSet.next()) {
                long roleId = resultSet.getLong("id");
                try (var assignUserRoleStatement = connection.prepareStatement(ASSIGN_ROLE_SQL)) {
                    assignUserRoleStatement.setLong(1, user.getId());
                    assignUserRoleStatement.setLong(2, roleId);
                    assignUserRoleStatement.executeUpdate();
                }
            }

            return user;
        } catch (SQLException e) {
            throw new DaoCrudException(e);
        }
    }

    private User buildUser(ResultSet resultSet) throws SQLException {
        return User.builder()
                .id(resultSet.getLong("id"))
                .email(resultSet.getString("email"))
                .password(resultSet.getString("password"))
                .build();
    }
}
