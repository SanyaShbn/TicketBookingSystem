package ticketbookingsystem.dao;

import com.example.ticketbookingsystem.dao.PurchasedTicketsDao;
import com.example.ticketbookingsystem.dto.PurchasedTicketDto;
import com.example.ticketbookingsystem.entity.*;
import com.example.ticketbookingsystem.utils.HibernateUtil;
import org.hibernate.Session;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ticketbookingsystem.utils.TestUtils.*;

class PurchasedTicketsDaoTest {

    private static PurchasedTicketsDao purchasedTicketsDao;

    @BeforeAll
    public static void setUp() {
        purchasedTicketsDao = PurchasedTicketsDao.getInstance();
        saveEntities();
    }

    public static void saveEntities() {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            Arena arena = createTestArena();
            session.save(arena);

            Sector sector = createTestSector(arena);
            session.save(sector);

            Row row = createTestRow(sector);
            session.save(row);

            Seat seat1 = createTestSeat(row);
            Seat seat2 = createTestSeat(row);

            session.save(seat1);
            session.save(seat2);

            SportEvent sportEvent = createTestSportEvent("Event A", LocalDateTime.now(), arena);
            session.save(sportEvent);

            Ticket ticket1 = createTestTicket(sportEvent, seat1);
            Ticket ticket2 = createTestTicket(sportEvent, seat2);

            session.save(ticket1);
            session.save(ticket2);

            session.getTransaction().commit();
        }
    }

    @Test
    void testSave() {
        List<Long> ticketIds = Arrays.asList(1L, 2L);
        Long userId = 1L;

        purchasedTicketsDao.save(ticketIds, userId);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<PurchasedTicket> savedTickets = session
                    .createQuery("FROM PurchasedTicket WHERE userId = :userId", PurchasedTicket.class)
                    .setParameter("userId", userId)
                    .getResultList(); assertNotNull(savedTickets);

            assertFalse(savedTickets.isEmpty());
            assertEquals(2, savedTickets.size());
            assertTrue(savedTickets.stream()
                    .map(PurchasedTicket::getId)
                    .toList()
                    .containsAll(ticketIds));
        }
    }

    @Test
    void testFindAllByUserId() {
        List<Long> ticketIds = Arrays.asList(1L, 2L);
        Long userId = 1L;

        purchasedTicketsDao.save(ticketIds, userId);

        List<PurchasedTicketDto> purchasedTickets = purchasedTicketsDao.findAllByUserId(userId);

        assertNotNull(purchasedTickets);
        assertEquals(2, purchasedTickets.size());
    }

}
