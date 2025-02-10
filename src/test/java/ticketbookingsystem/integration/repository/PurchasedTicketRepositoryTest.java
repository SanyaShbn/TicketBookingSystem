package ticketbookingsystem.integration.repository;

import com.example.ticketbookingsystem.entity.*;
import com.example.ticketbookingsystem.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PurchasedTicketRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private PurchasedTicketRepository purchasedTicketRepository;

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

    private Ticket ticket;

    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        Arena arena = createArena("Test Arena", "Test City", 10000);
        Sector sector = createSector("Sector 1", 20, 15, 100, 80, arena);
        Row row = createRow(1, 50, sector);
        Seat seat = createSeat(1, row);
        SportEvent event = createSportEvent("Test Event", LocalDateTime.now(), arena);
        ticket = createTicket(event, seat, "RESERVED");

        createPurchasedTicket(userId, ticket);
    }

    @AfterEach
    void tearDown() {
        purchasedTicketRepository.deleteAll();
        ticketRepository.deleteAll();
        sportEventRepository.deleteAll();
        seatRepository.deleteAll();
        rowRepository.deleteAll();
        sectorRepository.deleteAll();
        arenaRepository.deleteAll();
    }

    @Test
    void testFindAllByUserId() {
        List<PurchasedTicket> purchasedTickets = purchasedTicketRepository.findAllByUserId(userId);
        assertEquals(1, purchasedTickets.size());
        assertEquals(ticket.getId(), purchasedTickets.get(0).getTicket().getId());
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

    private Ticket createTicket(SportEvent event, Seat seat, String status) {
        Ticket ticket = Ticket.builder()
                .sportEvent(event)
                .seat(seat)
                .price(BigDecimal.valueOf(100))
                .status(TicketStatus.valueOf(status))
                .build();
        return ticketRepository.save(ticket);
    }

    private void createPurchasedTicket(Long userId, Ticket ticket) {
        PurchasedTicket purchasedTicket = PurchasedTicket.builder()
                .userId(userId)
                .ticket(ticket)
                .purchaseDate(LocalDateTime.now())
                .build();
        purchasedTicketRepository.save(purchasedTicket);
    }
}

