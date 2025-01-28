//package ticketbookingsystem.dao;
//
//import com.example.ticketbookingsystem.dao.TicketDao;
//import com.example.ticketbookingsystem.entity.*;
//import com.example.ticketbookingsystem.utils.HibernateUtil;
//import org.hibernate.Session;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static ticketbookingsystem.utils.TestUtils.*;
//
//public class TicketDaoTest {
//
//    private static final Long TICKET_ID = 1L;
//
//    private static TicketDao ticketDao;
//
//    @BeforeAll
//    public static void setup() {
//        ticketDao = TicketDao.getInstance();
//    }
//
//    public void saveEntities(Session session) {
//        Arena arena = createTestArena();
//        session.save(arena);
//
//        Sector sector = createTestSector(arena);
//        session.save(sector);
//
//        Row row = createTestRow(sector);
//        session.save(row);
//
//        Seat seat1 = createTestSeat(row);
//        Seat seat2 = createTestSeat(row);
//
//        session.save(seat1);
//        session.save(seat2);
//
//        SportEvent sportEvent = createTestSportEvent("Event A", LocalDateTime.now(), arena);
//        session.save(sportEvent);
//
//        Ticket ticket = createTestTicket(sportEvent, seat1);
//        session.save(ticket);
//    }
//
//    @Test
//    void testFindById() {
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            session.beginTransaction();
//            saveEntities(session);
//            session.getTransaction().commit();
//
//            Optional<Ticket> foundTicket = ticketDao.findById(TICKET_ID);
//
//            assertTrue(foundTicket.isPresent());
//        }
//    }
//
//    @Test
//    void testFindAll() {
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            session.beginTransaction();
//            saveEntities(session);
//            session.getTransaction().commit();
//
//            List<Ticket> sectors = ticketDao.findAll();
//
//            assertEquals(1, sectors.size());
//        }
//    }
//
//    @Test
//    void testSave() {
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            session.beginTransaction();
//            saveEntities(session);
//            session.getTransaction().commit();
//
//            Optional<Ticket> savedTicket = ticketDao.findById(TICKET_ID);
//
//            assertTrue(savedTicket.isPresent());
//            assertNotNull(savedTicket.get().getId());
//        }
//    }
//
//    @Test
//    void testUpdate() {
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            session.beginTransaction();
//            saveEntities(session);
//            session.getTransaction().commit();
//
//            Optional<Ticket> savedTicket = ticketDao.findById(TICKET_ID);
//
//            assertTrue(savedTicket.isPresent());
//
//            savedTicket.get().setPrice(BigDecimal.valueOf(15));
//            ticketDao.update(savedTicket.get());
//
//            Optional<Ticket> updatedTicket = ticketDao.findById(TICKET_ID);
//
//            assertTrue(updatedTicket.isPresent());
//            assertEquals(0, BigDecimal.valueOf(15).compareTo(updatedTicket.get().getPrice()));
//        }
//    }
//
//    @Test
//    void testDelete() {
//
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            session.beginTransaction();
//            saveEntities(session);
//            session.getTransaction().commit();
//
//            ticketDao.delete(TICKET_ID);
//
//            Optional<Ticket> foundTicket = ticketDao.findById(TICKET_ID);
//
//            assertFalse(foundTicket.isPresent());
//        }
//
//    }
//
//    @Test
//    void testFindAllByEventId() {
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            session.beginTransaction();
//            saveEntities(session);
//            session.getTransaction().commit();
//
//            SportEvent sportEvent = session.createQuery("FROM SportEvent", SportEvent.class)
//                    .getSingleResult();
//            List<Ticket> tickets = ticketDao.findAllByEventId(sportEvent.getId());
//
//            assertNotNull(tickets);
//            assertFalse(tickets.isEmpty());
//            assertEquals(1, tickets.size());
//        }
//    }
//
//    @Test
//    void testGetTicketStatus() {
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            session.beginTransaction();
//            saveEntities(session);
//            session.getTransaction().commit();
//
//            String status = ticketDao.getTicketStatus(TICKET_ID);
//
//            assertNotNull(status);
//            assertEquals(TicketStatus.AVAILABLE.name(), status);
//        }
//    }
//
//}
