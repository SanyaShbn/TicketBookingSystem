package ticketbookingsystem.dao;

import com.example.ticketbookingsystem.dao.UserDao;
import com.example.ticketbookingsystem.entity.User;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserDaoTest {

    private static final String USER_EMAIL = "test@example.com";

    private static final String USER_PASSWORD = "password123";

    private static UserDao userDao;

    @BeforeAll
    public static void setUp() {
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
        User user = createTestUser(USER_EMAIL, USER_PASSWORD);

        userDao.registerUser(user);

        assertNotNull(user.getId());
        assertEquals(USER_EMAIL, user.getEmail());
    }

    @Test
    void testFindByEmail() {
        User user = createTestUser(USER_EMAIL, USER_PASSWORD);

        userDao.registerUser(user);

        Optional<User> foundUser = userDao.findByEmail(USER_EMAIL);

        assertTrue(foundUser.isPresent());
        assertEquals(USER_EMAIL, foundUser.get().getEmail());
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
