package ticketbookingsystem.integration.repository;

import com.example.ticketbookingsystem.entity.Role;
import com.example.ticketbookingsystem.entity.User;
import com.example.ticketbookingsystem.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        createUser("test@example.com", "password123", Role.USER);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void testFindByEmail() {
        Optional<User> foundUser = userRepository.findByEmail("test@example.com");
        assertTrue(foundUser.isPresent());
        assertEquals("test@example.com", foundUser.get().getEmail());
    }

    private User createUser(String email, String password, Role role) {
        User user = User.builder()
                .email(email)
                .password(password)
                .role(role)
                .build();
        return userRepository.save(user);
    }
}