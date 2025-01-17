package ticketbookingsystem.dao;

import com.example.ticketbookingsystem.dao.ArenaDao;
import com.example.ticketbookingsystem.dao.SectorDao;
import com.example.ticketbookingsystem.entity.Arena;
import com.example.ticketbookingsystem.entity.Sector;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static ticketbookingsystem.utils.TestUtils.*;

class SectorDaoTest {

    private static final Long SECTOR_ID = 1L;

    private static final String SECTOR_NAME = "Test Sector";

    private static SectorDao sectorDao;

    private static ArenaDao arenaDao;

    @BeforeAll
    public static void setUp() {
        sectorDao = SectorDao.getInstance();
        arenaDao = ArenaDao.getInstance();
    }

    public void saveEntities() {
        Arena arena = createTestArena();
        arenaDao.save(arena);

        Sector sector1 = createTestSector(arena);
        Sector sector2 = createTestSector(arena);
        sectorDao.save(sector1);
        sectorDao.save(sector2);

    }

    @Test
    void testFindById() {
        saveEntities();

        Optional<Sector> foundSector = sectorDao.findById(SECTOR_ID);

        assertTrue(foundSector.isPresent());
        assertEquals(SECTOR_NAME, foundSector.get().getSectorName());
    }

    @Test
    void testFindAll() {
        saveEntities();

        List<Sector> sectors = sectorDao.findAll();

        assertEquals(2, sectors.size());
    }

    @Test
    void testSave() {
        saveEntities();

        Optional<Sector> savedSector = sectorDao.findById(SECTOR_ID);

        assertTrue(savedSector.isPresent());
        assertNotNull(savedSector.get().getId());
        assertEquals(SECTOR_NAME, savedSector.get().getSectorName());
    }

    @Test
    void testUpdate() {
        Arena arena = createTestArena();
        arenaDao.save(arena);

        Sector sector = createTestSector(arena);
        sectorDao.save(sector);

        sector.setSectorName("Sector B");
        sectorDao.update(sector);

        Optional<Sector> updatedSector = sectorDao.findById(SECTOR_ID);

        assertTrue(updatedSector.isPresent());
        assertEquals("Sector B", updatedSector.get().getSectorName());
    }

    @Test
    void testDelete() {
        saveEntities();

        sectorDao.delete(SECTOR_ID);

        Optional<Sector> foundSector = sectorDao.findById(SECTOR_ID);

        assertFalse(foundSector.isPresent());
    }
}
