package ticketbookingsystem.integration.repository;

import com.example.ticketbookingsystem.entity.Arena;
import com.example.ticketbookingsystem.entity.Sector;
import com.example.ticketbookingsystem.repository.ArenaRepository;
import com.example.ticketbookingsystem.repository.SectorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SectorRepositoryTest extends BaseRepositoryTest{

    @Autowired
    private SectorRepository sectorRepository;

    @Autowired
    private ArenaRepository arenaRepository;

    @Test
    void testSaveAndFindById() {
        Arena arena = Arena.builder()
                .name("Test")
                .city("Test")
                .capacity(10000)
                .build();

        arena = arenaRepository.save(arena);

        Sector sector = Sector.builder()
                .sectorName("Sector 1")
                .maxRowsNumb(20)
                .availableRowsNumb(15)
                .maxSeatsNumb(100)
                .availableSeatsNumb(80)
                .arena(arena)
                .build();

        sector = sectorRepository.save(sector);

        Optional<Sector> foundSector = sectorRepository.findById(sector.getId());
        assertTrue(foundSector.isPresent());
        assertEquals("Sector 1", foundSector.get().getSectorName());
    }

    @Test
    void testFindAllByArenaId() {
        Arena arena = Arena.builder()
                .name("Test Arena")
                .city("Test City")
                .capacity(10000)
                .build();

        arena = arenaRepository.save(arena);

        Sector sector1 = Sector.builder()
                .sectorName("Sector 1")
                .maxRowsNumb(20)
                .availableRowsNumb(15)
                .maxSeatsNumb(100)
                .availableSeatsNumb(80)
                .arena(arena)
                .build();
        Sector sector2 = Sector.builder()
                .sectorName("Sector 2")
                .maxRowsNumb(25)
                .availableRowsNumb(20)
                .maxSeatsNumb(120)
                .availableSeatsNumb(90)
                .arena(arena)
                .build();

        sectorRepository.save(sector1);
        sectorRepository.save(sector2);

        Pageable pageable = PageRequest.of(0, 10);
        var sectors = sectorRepository.findAllByArenaId(arena.getId(), pageable);
        assertEquals(2, sectors.getTotalElements());
    }

    @Test
    void testUpdateSector() {
        Arena arena = Arena.builder()
                .name("Test Arena")
                .city("Test City")
                .capacity(10000)
                .build();

        arena = arenaRepository.save(arena);

        Sector sector = Sector.builder()
                .sectorName("Sector 1")
                .maxRowsNumb(20)
                .availableRowsNumb(15)
                .maxSeatsNumb(100)
                .availableSeatsNumb(80)
                .arena(arena)
                .build();

        sector = sectorRepository.save(sector);

        sector.setSectorName("Updated Sector");
        sector.setAvailableRowsNumb(18);
        sector = sectorRepository.save(sector);

        Optional<Sector> updatedSector = sectorRepository.findById(sector.getId());
        assertTrue(updatedSector.isPresent());
        assertEquals("Updated Sector", updatedSector.get().getSectorName());
        assertEquals(18, updatedSector.get().getAvailableRowsNumb());
    }

    @Test
    void testDeleteById() {
        Arena arena = Arena.builder()
                .name("Test Arena")
                .city("Test City")
                .capacity(10000)
                .build();

        arena = arenaRepository.save(arena);

        Sector sector = Sector.builder()
                .sectorName("Sector 1")
                .maxRowsNumb(20)
                .availableRowsNumb(15)
                .maxSeatsNumb(100)
                .availableSeatsNumb(80)
                .arena(arena)
                .build();

        sector = sectorRepository.save(sector);
        sectorRepository.deleteById(sector.getId());

        Optional<Sector> deletedSector = sectorRepository.findById(sector.getId());
        assertTrue(deletedSector.isEmpty());
    }
}