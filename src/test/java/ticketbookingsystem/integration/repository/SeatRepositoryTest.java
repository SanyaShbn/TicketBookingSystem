package ticketbookingsystem.integration.repository;

import com.example.ticketbookingsystem.entity.Arena;
import com.example.ticketbookingsystem.entity.Row;
import com.example.ticketbookingsystem.entity.Seat;
import com.example.ticketbookingsystem.entity.Sector;
import com.example.ticketbookingsystem.repository.ArenaRepository;
import com.example.ticketbookingsystem.repository.RowRepository;
import com.example.ticketbookingsystem.repository.SeatRepository;
import com.example.ticketbookingsystem.repository.SectorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SeatRepositoryTest extends BaseRepositoryTest {

    private static final Long EVENT_ID = 1L;

    @Autowired
    private SeatRepository seatRepository;

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

        Seat seat = Seat.builder()
                .seatNumber(1)
                .row(row)
                .build();

        seat = seatRepository.save(seat);

        Optional<Seat> foundSeat = seatRepository.findById(seat.getId());
        assertTrue(foundSeat.isPresent());
        assertEquals(1, foundSeat.get().getSeatNumber());
    }

    @Test
    void testFindByEventId() {
        List<Seat> seats = seatRepository.findByEventId(EVENT_ID);
        assertEquals(0, seats.size());
    }

    @Test
    void testUpdateSeat() {
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

        Seat seat = Seat.builder()
                .seatNumber(1)
                .row(row)
                .build();

        seat = seatRepository.save(seat);

        seat.setSeatNumber(2);
        seat = seatRepository.save(seat);

        Optional<Seat> updatedSeat = seatRepository.findById(seat.getId());
        assertTrue(updatedSeat.isPresent());
        assertEquals(2, updatedSeat.get().getSeatNumber());
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

        Seat seat = Seat.builder()
                .seatNumber(1)
                .row(row)
                .build();

        seat = seatRepository.save(seat);
        seatRepository.deleteById(seat.getId());

        Optional<Seat> deletedSeat = seatRepository.findById(seat.getId());
        assertTrue(deletedSeat.isEmpty());
    }
}