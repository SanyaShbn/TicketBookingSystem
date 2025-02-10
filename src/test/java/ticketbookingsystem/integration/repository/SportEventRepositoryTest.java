package ticketbookingsystem.integration.repository;

import com.example.ticketbookingsystem.entity.Arena;
import com.example.ticketbookingsystem.entity.SportEvent;
import com.example.ticketbookingsystem.repository.ArenaRepository;
import com.example.ticketbookingsystem.repository.SportEventRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SportEventRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private SportEventRepository sportEventRepository;

    @Autowired
    private ArenaRepository arenaRepository;

    @Test
    void testSaveAndFindById() {
        Arena arena = Arena.builder()
                .name("Test Arena")
                .city("Test City")
                .capacity(10000)
                .build();

        arena = arenaRepository.save(arena);

        SportEvent event = SportEvent.builder()
                .eventName("Test Event")
                .eventDateTime(LocalDateTime.now())
                .arena(arena)
                .build();

        event = sportEventRepository.save(event);

        Optional<SportEvent> foundEvent = sportEventRepository.findById(event.getId());
        assertTrue(foundEvent.isPresent());
        assertEquals("Test Event", foundEvent.get().getEventName());
    }

    @Test
    void testUpdateSportEvent() {
        Arena arena = Arena.builder()
                .name("Test Arena")
                .city("Test City")
                .capacity(10000)
                .build();

        arena = arenaRepository.save(arena);

        SportEvent event = SportEvent.builder()
                .eventName("Original Event")
                .eventDateTime(LocalDateTime.now())
                .arena(arena)
                .build();

        event = sportEventRepository.save(event);

        event.setEventName("Updated Event");
        event = sportEventRepository.save(event);

        Optional<SportEvent> updatedEvent = sportEventRepository.findById(event.getId());
        assertTrue(updatedEvent.isPresent());
        assertEquals("Updated Event", updatedEvent.get().getEventName());
    }

    @Test
    void testDeleteById() {
        Arena arena = Arena.builder()
                .name("Test Arena")
                .city("Test City")
                .capacity(10000)
                .build();

        arena = arenaRepository.save(arena);

        SportEvent event = SportEvent.builder()
                .eventName("Test Event")
                .eventDateTime(LocalDateTime.now())
                .arena(arena)
                .build();

        event = sportEventRepository.save(event);
        sportEventRepository.deleteById(event.getId());

        Optional<SportEvent> deletedEvent = sportEventRepository.findById(event.getId());
        assertTrue(deletedEvent.isEmpty());
    }
}