package ticketbookingsystem.dao;

import com.example.ticketbookingsystem.dao.ArenaDao;
import com.example.ticketbookingsystem.entity.Arena;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ArenaDaoTest {

    private ArenaDao arenaDao;

    @BeforeEach
    void setUp() {
        arenaDao = ArenaDao.getInstance();
    }

    @Test
    void testSave() {
        Arena arena = createTestArena("Test Arena", "Test City", 5000, 1000);

        arenaDao.save(arena);

        assertNotNull(arena.getId());
        assertEquals("Test Arena", arena.getName());
    }

    @Test
    void testFindById() {
        Arena arena = createTestArena("Test Arena", "Test City", 5000, 1000);

        arenaDao.save(arena);

        Optional<Arena> foundArena = arenaDao.findById(arena.getId());

        assertTrue(foundArena.isPresent());
        assertEquals("Test Arena", foundArena.get().getName());
    }

    @Test
    void testFindAll() {
        Arena arena1 = createTestArena("Test Arena 1", "Test City 1",
                5000, 1000);
        Arena arena2 = createTestArena("Test Arena 2", "Test City 2",
                7000, 2000);

        arenaDao.save(arena1);
        arenaDao.save(arena2);

        List<Arena> arenas = arenaDao.findAll();

        assertEquals(2, arenas.size());
    }

    @Test
    void testDelete() {
        Arena arena = createTestArena("Test Arena", "Test City", 5000, 1000);

        arenaDao.save(arena);
        arenaDao.delete(arena.getId());

        Optional<Arena> foundArena = arenaDao.findById(arena.getId());

        assertFalse(foundArena.isPresent());
    }

    private Arena createTestArena(String name, String city, int capacity, int generalSeatsNumb) {
        return Arena.builder()
                .name(name)
                .city(city)
                .capacity(capacity)
                .generalSeatsNumb(generalSeatsNumb)
                .build();
    }
}

