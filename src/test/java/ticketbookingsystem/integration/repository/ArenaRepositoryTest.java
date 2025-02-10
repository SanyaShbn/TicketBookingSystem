package ticketbookingsystem.integration.repository;

import com.example.ticketbookingsystem.entity.Arena;
import com.example.ticketbookingsystem.repository.ArenaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArenaRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private ArenaRepository arenaRepository;

    @Test
    void testFindAll() {
        Arena arena1 = Arena.builder()
                .name("Arena1")
                .city("City1")
                .capacity(5000)
                .build();
        Arena arena2 = Arena.builder()
                .name("Arena2")
                .city("City2")
                .capacity(7000)
                .build();

        arenaRepository.save(arena1);
        arenaRepository.save(arena2);

        List<Arena> arenas = arenaRepository.findAll();
        assertEquals(2, arenas.size());
    }

    @Test
    void testSaveAndFindById() {
        Arena arena = Arena.builder()
                .name("Test Arena")
                .city("Test City")
                .capacity(10000)
                .build();

        arena = arenaRepository.save(arena);

        Optional<Arena> foundArena = arenaRepository.findById(arena.getId());
        assertTrue(foundArena.isPresent());
        assertEquals("Test Arena", foundArena.get().getName());
    }

    @Test
    void testUpdateArena() {
        Arena arena = Arena.builder()
                .name("Original Arena")
                .city("Original City")
                .capacity(8000)
                .build();

        arena = arenaRepository.save(arena);

        arena.setName("Updated Arena");
        arena.setCity("Updated City");
        arena = arenaRepository.save(arena);

        Optional<Arena> updatedArena = arenaRepository.findById(arena.getId());
        assertTrue(updatedArena.isPresent());
        assertEquals("Updated Arena", updatedArena.get().getName());
        assertEquals("Updated City", updatedArena.get().getCity());
    }


    @Test
    void testDeleteById() {
        Arena arena = Arena.builder()
                .name("Test Arena")
                .city("Test City")
                .capacity(10000)
                .build();

        arena = arenaRepository.save(arena);
        arenaRepository.deleteById(arena.getId());

        Optional<Arena> deletedArena = arenaRepository.findById(arena.getId());
        assertTrue(deletedArena.isEmpty());
    }
}