package ticketbookingsystem.dao;

import com.example.ticketbookingsystem.dao.SeatDao;
import com.example.ticketbookingsystem.entity.*;
import com.example.ticketbookingsystem.utils.HibernateUtil;
import org.hibernate.Session;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ticketbookingsystem.utils.TestUtils.*;

public class SeatDaoTest {

    private static SeatDao seatDao;

    @BeforeAll
    public static void setup() {
        seatDao = SeatDao.getInstance();
    }

    public void saveEntities(Session session) {
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
    }

    @Test
    public void testFindAllByEventId() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            saveEntities(session);
            session.getTransaction().commit();

            SportEvent sportEvent = session.createQuery("FROM SportEvent", SportEvent.class).getSingleResult();
            List<Seat> seats = seatDao.findAllByEventId(sportEvent.getId());

            assertEquals(2, seats.size());
        }
    }

    @Test
    public void testFindByEventIdWithNoTickets() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            saveEntities(session);

            SportEvent sportEvent = session.createQuery("FROM SportEvent", SportEvent.class).getSingleResult();
            Ticket ticket = createTestTicket(sportEvent, session.createQuery("FROM Seat", Seat.class)
                    .getResultList().get(0));
            session.save(ticket);

            session.getTransaction().commit();

            List<Seat> seats = seatDao.findByEventIdWithNoTickets(sportEvent.getId());

            assertEquals(1, seats.size());
        }
    }

    @Test
    public void testFindAllByEventIdWhenUpdate() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            saveEntities(session);
            session.getTransaction().commit();

            SportEvent sportEvent = session.createQuery("FROM SportEvent", SportEvent.class)
                    .getSingleResult();
            Seat seat1 = session.createQuery("FROM Seat", Seat.class).getResultList().get(0);
            List<Seat> seats = seatDao.findAllByEventIdWhenUpdate(sportEvent.getId(), seat1.getId());

            assertEquals(2, seats.size());
        }
    }

}