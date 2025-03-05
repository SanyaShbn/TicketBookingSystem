package ticketbookingsystem.integration.repository;

import com.example.ticketbookingsystem.entity.*;
import com.example.ticketbookingsystem.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TicketRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private SportEventRepository sportEventRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private RowRepository rowRepository;

    @Autowired
    private SectorRepository sectorRepository;

    @Autowired
    private ArenaRepository arenaRepository;

    private Seat seat;

    private SportEvent event;

    @BeforeEach
    void setUp() {
        Arena arena = createArena("Test Arena", "Test City", 10000);
        Sector sector = createSector("Sector 1", 20, 15, 100, 80, arena);
        Row row = createRow(1, 50, sector);
        seat = createSeat(1, row);
        event = createSportEvent("Test Event", LocalDateTime.now(), arena);
    }

    @AfterEach
    void tearDown() {
        ticketRepository.deleteAll();
        sportEventRepository.deleteAll();
        seatRepository.deleteAll();
        rowRepository.deleteAll();
        sectorRepository.deleteAll();
        arenaRepository.deleteAll();
    }

    @Test
    void testFindById() {
        Ticket ticket = createTicket(event, seat, TicketStatus.AVAILABLE);

        Optional<Ticket> foundTicket = ticketRepository.findById(ticket.getId());
        assertTrue(foundTicket.isPresent());
        assertEquals(TicketStatus.AVAILABLE, foundTicket.get().getStatus());
    }

    @Test
    void testFindStatusById() {
        Ticket ticket = createTicket(event, seat, TicketStatus.RESERVED);

        Optional<String> status = ticketRepository.findStatusById(ticket.getId());
        assertTrue(status.isPresent());
        assertEquals("RESERVED", status.get());
    }

    @Test
    void testFindAllBySportEventIdPageable() {
        createTicket(event, seat, TicketStatus.AVAILABLE);

        Pageable pageable = PageRequest.of(0, 10);
        var tickets = ticketRepository.findAllBySportEventId(event.getId(), pageable);
        assertEquals(1, tickets.getTotalElements());
    }

    private Arena createArena(String name, String city, int capacity) {
        Arena arena = Arena.builder()
                .name(name)
                .city(city)
                .capacity(capacity)
                .build();
        return arenaRepository.save(arena);
    }

    private Sector createSector(String name, int maxRows, int availableRows, int maxSeats, int availableSeats, Arena arena) {
        Sector sector = Sector.builder()
                .sectorName(name)
                .maxRowsNumb(maxRows)
                .availableRowsNumb(availableRows)
                .maxSeatsNumb(maxSeats)
                .availableSeatsNumb(availableSeats)
                .arena(arena)
                .build();
        return sectorRepository.save(sector);
    }

    private Row createRow(int number, int seats, Sector sector) {
        Row row = Row.builder()
                .rowNumber(number)
                .seatsNumb(seats)
                .sector(sector)
                .build();
        return rowRepository.save(row);
    }

    private Seat createSeat(int number, Row row) {
        Seat seat = Seat.builder()
                .seatNumber(number)
                .row(row)
                .build();
        return seatRepository.save(seat);
    }

    private SportEvent createSportEvent(String name, LocalDateTime date, Arena arena) {
        SportEvent event = SportEvent.builder()
                .eventName(name)
                .eventDateTime(date)
                .arena(arena)
                .build();
        return sportEventRepository.save(event);
    }

    private Ticket createTicket(SportEvent event, Seat seat, TicketStatus status) {
        Ticket ticket = Ticket.builder()
                .sportEvent(event)
                .seat(seat)
                .price(BigDecimal.valueOf(100))
                .status(status)
                .build();
        return ticketRepository.save(ticket);
    }
}