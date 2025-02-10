package ticketbookingsystem.integration.repository;

import com.example.ticketbookingsystem.security.RefreshToken;
import com.example.ticketbookingsystem.repository.RefreshTokenRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RefreshTokenRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @BeforeEach
    void setUp() {
        createRefreshToken(Instant.now().plusSeconds(3600));
    }

    @AfterEach
    void tearDown() {
        refreshTokenRepository.deleteAll();
    }

    @Test
    void testFindByToken() {
        Optional<RefreshToken> foundToken = refreshTokenRepository.findByToken("sample-token");
        assertTrue(foundToken.isPresent());
        assertEquals("sample-token", foundToken.get().getToken());
    }

    private void createRefreshToken(Instant expiryDate) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("sample-token");
        refreshToken.setUsername("username");
        refreshToken.setExpiryDate(expiryDate);
        refreshTokenRepository.save(refreshToken);
    }
}