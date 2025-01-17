package ticketbookingsystem.dao;

import com.example.ticketbookingsystem.dao.ArenaDao;
import com.example.ticketbookingsystem.entity.Arena;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static ticketbookingsystem.utils.TestUtils.createTestArena;

class ArenaDaoTest {

    private static ArenaDao arenaDao;

    @BeforeAll
    public static void setUp() {
        arenaDao = ArenaDao.getInstance();
    }

    @Test
    void testFindById() {
        Arena arena = createTestArena();

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
    void testSave() {
        Arena arena = createTestArena();

        arenaDao.save(arena);

        Optional<Arena> savedArena = arenaDao.findById(arena.getId());

        assertTrue(savedArena.isPresent());
        assertNotNull(savedArena.get().getId());
        assertEquals(savedArena.get().getName(), arena.getName());
    }

    @Test
    void testUpdate() {
        Arena arena = createTestArena();
        arenaDao.save(arena);

        arena.setName("Updated Test Arena");
        arenaDao.update(arena);

        Optional<Arena> updatedArena = arenaDao.findById(arena.getId());

        assertTrue(updatedArena.isPresent());
        assertEquals(updatedArena.get().getName(), arena.getName());
    }

    @Test
    void testDelete() {
        Arena arena = createTestArena();

        arenaDao.save(arena);
        arenaDao.delete(arena.getId());

        Optional<Arena> foundArena = arenaDao.findById(arena.getId());

        assertFalse(foundArena.isPresent());
    }
}

