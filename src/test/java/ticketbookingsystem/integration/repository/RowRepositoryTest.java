package ticketbookingsystem.integration.repository;

import com.example.ticketbookingsystem.entity.Arena;
import com.example.ticketbookingsystem.entity.Row;
import com.example.ticketbookingsystem.entity.Sector;
import com.example.ticketbookingsystem.repository.ArenaRepository;
import com.example.ticketbookingsystem.repository.RowRepository;
import com.example.ticketbookingsystem.repository.SectorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RowRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private RowRepository rowRepository;

    @Autowired
    private SectorRepository sectorRepository;

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

        Sector sector = Sector.builder()
                .sectorName("Sector 1")
                .maxRowsNumb(20)
                .availableRowsNumb(15)
                .maxSeatsNumb(100)
                .availableSeatsNumb(80)
                .arena(arena)
                .build();

        sector = sectorRepository.save(sector);

        Row row = Row.builder()
                .rowNumber(1)
                .seatsNumb(50)
                .sector(sector)
                .build();

        row = rowRepository.save(row);

        Optional<Row> foundRow = rowRepository.findById(row.getId());
        assertTrue(foundRow.isPresent());
        assertEquals(1, foundRow.get().getRowNumber());
    }

    @Test
    void testFindAllBySectorId() {
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

        Row row1 = Row.builder()
                .rowNumber(1)
                .seatsNumb(50)
                .sector(sector)
                .build();
        Row row2 = Row.builder()
                .rowNumber(2)
                .seatsNumb(50)
                .sector(sector)
                .build();

        rowRepository.save(row1);
        rowRepository.save(row2);

        Pageable pageable = PageRequest.of(0, 10);
        var rows = rowRepository.findAllBySectorId(sector.getId(), pageable);
        assertEquals(2, rows.getTotalElements());
    }

    @Test
    void testUpdateRow() {
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

        Row row = Row.builder()
                .rowNumber(1)
                .seatsNumb(50)
                .sector(sector)
                .build();

        row = rowRepository.save(row);

        row.setRowNumber(2);
        row.setSeatsNumb(60);
        row = rowRepository.save(row);

        Optional<Row> updatedRow = rowRepository.findById(row.getId());
        assertTrue(updatedRow.isPresent());
        assertEquals(2, updatedRow.get().getRowNumber());
        assertEquals(60, updatedRow.get().getSeatsNumb());
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

        Row row = Row.builder()
                .rowNumber(1)
                .seatsNumb(50)
                .sector(sector)
                .build();

        row = rowRepository.save(row);
        rowRepository.deleteById(row.getId());

        Optional<Row> deletedRow = rowRepository.findById(row.getId());
        assertTrue(deletedRow.isEmpty());
    }

}