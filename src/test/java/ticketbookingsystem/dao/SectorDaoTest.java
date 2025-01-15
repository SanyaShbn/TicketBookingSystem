package ticketbookingsystem.dao;

import com.example.ticketbookingsystem.dao.ArenaDao;
import com.example.ticketbookingsystem.dao.SectorDao;
import com.example.ticketbookingsystem.entity.Arena;
import com.example.ticketbookingsystem.entity.Sector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SectorDaoTest {

    private SectorDao sectorDao;

    private ArenaDao arenaDao;

    @BeforeEach
    void setUp() {
        sectorDao = SectorDao.getInstance();
        arenaDao = ArenaDao.getInstance();
    }

    @Test
    void testSave() {
        Arena arena = createTestArena();
        arenaDao.save(arena);

        Sector sector = createTestSector("Sector A", arena, 10, 10,
                100, 100);
        sectorDao.save(sector);

        assertNotNull(sector.getId());
        assertEquals("Sector A", sector.getSectorName());
    }

    @Test
    void testFindById() {
        Arena arena = createTestArena();
        arenaDao.save(arena);

        Sector sector = createTestSector("Sector A", arena, 10, 10,
                100, 100);
        sectorDao.save(sector);

        Optional<Sector> foundSector = sectorDao.findById(sector.getId());

        assertTrue(foundSector.isPresent());
        assertEquals("Sector A", foundSector.get().getSectorName());
    }

    @Test
    void testFindAll() {
        Arena arena = createTestArena();
        arenaDao.save(arena);

        Sector sector1 = createTestSector("Sector A1", arena, 10, 10,
                100, 100);
        Sector sector2 = createTestSector("Sector A2", arena, 20, 20,
                200, 200);

        sectorDao.save(sector1);
        sectorDao.save(sector2);

        List<Sector> sectors = sectorDao.findAll();

        assertEquals(2, sectors.size());
    }

    @Test
    void testDelete() {
        Arena arena = createTestArena();
        arenaDao.save(arena);

        Sector sector = createTestSector("Sector A", arena, 10, 10,
                100, 100);
        sectorDao.save(sector);
        sectorDao.delete(sector.getId());

        Optional<Sector> foundSector = sectorDao.findById(sector.getId());

        assertFalse(foundSector.isPresent());
    }

    private Sector createTestSector(String sectorName, Arena arena, int maxRowsNumb, int availableRowsNumb,
                                    int maxSeatsNumb, int availableSeatsNumb) {
        return Sector.builder()
                .sectorName(sectorName)
                .arena(arena)
                .maxRowsNumb(maxRowsNumb)
                .availableRowsNumb(availableRowsNumb)
                .maxSeatsNumb(maxSeatsNumb)
                .availableSeatsNumb(availableSeatsNumb)
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
