package ticketbookingsystem.dao;

import com.example.ticketbookingsystem.dao.ArenaDao;
import com.example.ticketbookingsystem.dao.SportEventDao;
import com.example.ticketbookingsystem.entity.Arena;
import com.example.ticketbookingsystem.entity.SportEvent;
import com.example.ticketbookingsystem.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SportEventDaoTest {

    private SportEventDao sportEventDao;

    private ArenaDao arenaDao;

    private Session session;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        sportEventDao = SportEventDao.getInstance();
        arenaDao = ArenaDao.getInstance();

        session = mock(Session.class);
        transaction = mock(Transaction.class);
    }

    @Test
    void testSave() {
        Arena arena = createTestArena();
        arenaDao.save(arena);

        SportEvent sportEvent = createTestSportEvent("Event A", LocalDateTime.now(), arena);
        when(session.beginTransaction()).thenReturn(transaction);

        sportEventDao.save(sportEvent);

        verify(session).save(sportEvent);
        verify(transaction).commit();
    }

    @Test
    void testFindById() {
        Arena arena = createTestArena();
        arenaDao.save(arena);

        SportEvent sportEvent = createTestSportEvent("Event A", LocalDateTime.now(), arena);
        when(session.get(SportEvent.class, sportEvent.getId())).thenReturn(sportEvent);

        Optional<SportEvent> foundSportEvent = sportEventDao.findById(sportEvent.getId());

        assertTrue(foundSportEvent.isPresent());
        assertEquals("Event A", foundSportEvent.get().getEventName());
    }

    @Test
    void testFindAll() {
        Arena arena = createTestArena();
        arenaDao.save(arena);

        SportEvent sportEvent1 = createTestSportEvent("Event A1", LocalDateTime.now(), arena);
        SportEvent sportEvent2 = createTestSportEvent("Event A2", LocalDateTime.now(), arena);

        when(session.createQuery("from SportEvent", SportEvent.class)).thenReturn(mock(Query.class));
        Query<SportEvent> query = session.createQuery("from SportEvent", SportEvent.class);
        when(query.list()).thenReturn(List.of(sportEvent1, sportEvent2));

        List<SportEvent> sportEvents = sportEventDao.findAll();

        assertEquals(2, sportEvents.size());
    }

    @Test
    void testDelete() {
        Arena arena = createTestArena();
        arenaDao.save(arena);

        SportEvent sportEvent = createTestSportEvent("Event A", LocalDateTime.now(), arena);
        when(session.beginTransaction()).thenReturn(transaction);
        when(session.load(SportEvent.class, sportEvent.getId())).thenReturn(sportEvent);

        sportEventDao.delete(sportEvent.getId());

        verify(session).delete(sportEvent);
        verify(transaction).commit();
    }

    private SportEvent createTestSportEvent(String eventName, LocalDateTime eventDateTime, Arena arena) {
        return SportEvent.builder()
                .eventName(eventName)
                .eventDateTime(eventDateTime)
                .arena(arena)
                .build();
    }

    private Arena createTestArena() {
        return Arena.builder()
                .name("Test Arena")
                .city("Test City")
                .capacity(5000)
                .generalSeatsNumb(1000)
                .build();
    }
}

