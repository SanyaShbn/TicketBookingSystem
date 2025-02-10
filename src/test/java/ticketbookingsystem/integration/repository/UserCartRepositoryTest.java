package ticketbookingsystem.integration.repository;

import com.example.ticketbookingsystem.entity.UserCart;
import com.example.ticketbookingsystem.entity.UserCartId;
import com.example.ticketbookingsystem.repository.UserCartRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserCartRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private UserCartRepository userCartRepository;

    private static final Long USER_ID = 1L;

    private static final Long TICKET_ID = 1L;

    @BeforeEach
    void setUp() {
        createUserCart();
    }

    @AfterEach
    void tearDown() {
        userCartRepository.deleteAll();
    }

    @Test
    void testFindByUserId() {
        List<UserCart> userCarts = userCartRepository.findByUserId(USER_ID);
        assertEquals(1, userCarts.size());
    }

    @Transactional
    @Test
    void testDeleteByUserId() {
        userCartRepository.deleteByUserId(USER_ID);
        List<UserCart> userCarts = userCartRepository.findByUserId(USER_ID);
        assertTrue(userCarts.isEmpty());
    }

    private void createUserCart() {
        UserCart userCart = UserCart.builder()
                .id(new UserCartId(UserCartRepositoryTest.USER_ID, UserCartRepositoryTest.TICKET_ID))
                .build();
        userCartRepository.save(userCart);
    }
}