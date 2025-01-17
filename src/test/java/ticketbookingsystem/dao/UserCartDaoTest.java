package ticketbookingsystem.dao;

import com.example.ticketbookingsystem.dao.UserCartDao;
import com.example.ticketbookingsystem.entity.*;
import com.example.ticketbookingsystem.utils.HibernateUtil;
import org.hibernate.Session;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ticketbookingsystem.utils.TestUtils.*;

public class UserCartDaoTest {

    private static final Long USER_CART_ID = 1L;
    private static UserCartDao userCartDao;

    @BeforeAll
    public static void setUp() {
        userCartDao = UserCartDao.getInstance();
    }

    private UserCart prepareData(Session session) {
        Arena arena = createTestArena();
        session.save(arena);

        Sector sector = createTestSector(arena);
        session.save(sector);

        Row row = createTestRow(sector);
        session.save(row);

        Seat seat = createTestSeat(row);
        session.save(seat);

        SportEvent sportEvent = createTestSportEvent("Event A", LocalDateTime.now(), arena);
        session.save(sportEvent);

        Ticket ticket = createTestTicket(sportEvent, seat);
        session.save(ticket);

        UserCart userCart = new UserCart();
        userCart.setId(new UserCartId(1L, ticket.getId()));
        userCartDao.save(userCart);

        return userCart;
    }

    @Test
    void testSave() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            UserCart userCart = prepareData(session);
            session.getTransaction().commit();

            UserCart foundCart = session.get(UserCart.class, userCart.getId());
            assertNotNull(foundCart);
        }
    }

    @Test
    void testDelete() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            UserCart userCart = prepareData(session);
            session.getTransaction().commit();

            session.beginTransaction();
            userCartDao.delete(userCart);
            session.getTransaction().commit();

            UserCart foundCart = session.get(UserCart.class, userCart.getId());
            assertNull(foundCart);
            Ticket ticket = session.get(Ticket.class, userCart.getId().getTicketId());
            assertEquals(TicketStatus.AVAILABLE, ticket.getStatus());
        }
    }

    @Test
    void testClearCart() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            prepareData(session);
            session.getTransaction().commit();

            session.beginTransaction();
            userCartDao.clearCart(USER_CART_ID);
            session.getTransaction().commit();

            List<UserCart> userCarts = session.createQuery("FROM UserCart WHERE id.userId = 1", UserCart.class)
                    .getResultList();
            assertTrue(userCarts.isEmpty());
            Ticket ticket = session.get(Ticket.class, 1L);
            assertEquals(TicketStatus.AVAILABLE, ticket.getStatus());
        }
    }

    @Test
    void testGetTicketIdsFromUserCart() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            UserCart userCart = prepareData(session);
            session.getTransaction().commit();

            List<Long> ticketIds = userCartDao.getTicketIdsFromUserCart(USER_CART_ID);

            assertNotNull(ticketIds);
            assertFalse(ticketIds.isEmpty());
            assertEquals(Collections.singletonList(userCart.getId().getTicketId()), ticketIds);
        }
    }
}