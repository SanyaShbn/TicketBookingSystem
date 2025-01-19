package ticketbookingsystem.dao;

import com.example.ticketbookingsystem.dao.ArenaDao;
import com.example.ticketbookingsystem.dao.SportEventDao;
import com.example.ticketbookingsystem.entity.Arena;
import com.example.ticketbookingsystem.entity.SportEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static ticketbookingsystem.utils.TestUtils.createTestArena;
import static ticketbookingsystem.utils.TestUtils.createTestSportEvent;

class SportEventDaoTest {

    private static final Long SPORT_EVENT_ID = 1L;

    private static final String SPORT_EVENT_NAME = "Event A";
    private static SportEventDao sportEventDao;

    private static ArenaDao arenaDao;

    @BeforeAll
    public static void setUp() {
        sportEventDao = SportEventDao.getInstance();
        arenaDao = ArenaDao.getInstance();
    }

    @Test
    void testFindAll() {
        Arena arena = createTestArena();
        arenaDao.save(arena);

        SportEvent sportEvent1 = createTestSportEvent("Event A1", LocalDateTime.now(), arena);
        SportEvent sportEvent2 = createTestSportEvent("Event A2", LocalDateTime.now(), arena);

        sportEventDao.save(sportEvent1);
        sportEventDao.save(sportEvent2);

        List<SportEvent> sportEvents = sportEventDao.findAll();

        assertEquals(2, sportEvents.size());
    }

    @Test
    void testFindById() {
        Arena arena = createTestArena();
        arenaDao.save(arena);

        SportEvent sportEvent = createTestSportEvent("Event A", LocalDateTime.now(), arena);

        sportEventDao.save(sportEvent);

        Optional<SportEvent> foundSportEvent = sportEventDao.findById(SPORT_EVENT_ID);

        assertTrue(foundSportEvent.isPresent());
        assertEquals(SPORT_EVENT_NAME, foundSportEvent.get().getEventName());
    }

    @Test
    void testSave() {
        Arena arena = createTestArena();
        arenaDao.save(arena);

        SportEvent sportEvent = createTestSportEvent("Event A", LocalDateTime.now(), arena);

        sportEventDao.save(sportEvent);

        Optional<SportEvent> savedSportEvent = sportEventDao.findById(SPORT_EVENT_ID);

        assertTrue(savedSportEvent.isPresent());
        assertNotNull(savedSportEvent.get().getId());
        assertEquals(SPORT_EVENT_NAME, savedSportEvent.get().getEventName());
    }

    @Test
    void testUpdate() {
        Arena arena = createTestArena();
        arenaDao.save(arena);

        SportEvent sportEvent = createTestSportEvent("Event A", LocalDateTime.now(), arena);
        sportEventDao.save(sportEvent);

        sportEvent.setEventName("Updated Event A");
        sportEventDao.update(sportEvent);

        Optional<SportEvent> updatedSportEvent = sportEventDao.findById(SPORT_EVENT_ID);

        assertTrue(updatedSportEvent.isPresent());
        assertEquals("Updated Event A", updatedSportEvent.get().getEventName());
    }

    @Test
    void testDelete() {
        Arena arena = createTestArena();
        arenaDao.save(arena);

        SportEvent sportEvent = createTestSportEvent("Event A", LocalDateTime.now(), arena);

        sportEventDao.save(sportEvent);
        sportEventDao.delete(sportEvent.getId());

        Optional<SportEvent> foundSportEvent = sportEventDao.findById(SPORT_EVENT_ID);

        assertFalse(foundSportEvent.isPresent());
    }
}

