package ticketbookingsystem.dao;

import com.example.ticketbookingsystem.dao.UserDao;
import com.example.ticketbookingsystem.entity.User;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserDaoTest {

    private UserDao userDao;

    @BeforeEach
    void setUp() {
        userDao = UserDao.getInstance();
    }

    private User createTestUser(String email, String password) {
        return User.builder()
                .email(email)
                .password(password)
                .build();
    }

    @Test
    void testRegisterUser() {
        User user = createTestUser("test@example.com", "password123");

        userDao.registerUser(user);

        assertNotNull(user.getId());
        assertEquals("test@example.com", user.getEmail());
    }

    @Test
    void testFindByEmail() {
        User user = createTestUser("test@example.com", "password123");

        userDao.registerUser(user);

        Optional<User> foundUser = userDao.findByEmail("test@example.com");

        assertTrue(foundUser.isPresent());
        assertEquals("test@example.com", foundUser.get().getEmail());
    }

    @Test
    void testFindByEmailNotFound() {
        Optional<User> foundUser = userDao.findByEmail("nonexistent@example.com");

        assertFalse(foundUser.isPresent());
    }

    @Test
    void testRegisterUserThrowsException() {
        User user = createTestUser(null, null);

        assertThrows(DaoCrudException.class, () -> {
            userDao.registerUser(user);
        });
    }
}
