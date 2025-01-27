//package ticketbookingsystem.dao;
//
//import com.example.ticketbookingsystem.dao.ArenaDao;
//import com.example.ticketbookingsystem.dao.RowDao;
//import com.example.ticketbookingsystem.dao.SectorDao;
//import com.example.ticketbookingsystem.entity.*;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static ticketbookingsystem.utils.TestUtils.*;
//
//public class RowDaoTest {
//
//    private static final Long ROW_ID = 1L;
//
//    private static final int ROW_NUMBER = 1;
//
//    private static RowDao rowDao;
//
//    private static SectorDao sectorDao;
//
//    private static ArenaDao arenaDao;
//
//    @BeforeAll
//    public static void setUp() {
//        rowDao = RowDao.getInstance();
//        sectorDao = SectorDao.getInstance();
//        arenaDao = ArenaDao.getInstance();
//    }
//
//    public void saveEntities() {
//        Arena arena = createTestArena();
//        arenaDao.save(arena);
//
//        Sector sector = createTestSector(arena);
//        sectorDao.save(sector);
//
//        Row row1 = createTestRow(sector);
//        Row row2 = createTestRow(sector);
//        rowDao.save(row1);
//        rowDao.save(row2);
//    }
//
//    @Test
//    void testFindById() {
//        saveEntities();
//
//        Optional<Row> foundRow = rowDao.findById(ROW_ID);
//
//        assertTrue(foundRow.isPresent());
//        assertEquals(ROW_NUMBER, foundRow.get().getRowNumber());
//    }
//
//    @Test
//    void testFindAll() {
//        saveEntities();
//
//        List<Row> rows = rowDao.findAll();
//
//        assertEquals(2, rows.size());
//    }
//
//    @Test
//    void testSave() {
//        saveEntities();
//
//        Optional<Row> savedRow = rowDao.findById(ROW_ID);
//
//        assertTrue(savedRow.isPresent());
//        assertNotNull(savedRow.get().getId());
//        assertEquals(savedRow.get().getRowNumber(), ROW_NUMBER);
//    }
//
//    @Test
//    void testUpdate() {
//        Arena arena = createTestArena();
//        arenaDao.save(arena);
//
//        Sector sector = createTestSector(arena);
//        sectorDao.save(sector);
//
//        Row row = createTestRow(sector);
//        rowDao.save(row);
//
//        row.setRowNumber(2);
//        rowDao.update(row);
//
//        Optional<Row> updatedRow = rowDao.findById(row.getId());
//
//        assertTrue(updatedRow.isPresent());
//        assertEquals(updatedRow.get().getRowNumber(), row.getRowNumber());
//    }
//
//    @Test
//    void testDelete() {
//        saveEntities();
//
//        rowDao.delete(ROW_ID);
//
//        Optional<Row> foundRow = rowDao.findById(ROW_ID);
//
//        assertFalse(foundRow.isPresent());
//    }
//}
