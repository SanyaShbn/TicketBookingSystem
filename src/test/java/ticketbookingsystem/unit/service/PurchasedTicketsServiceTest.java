package ticketbookingsystem.unit.service;

import com.example.ticketbookingsystem.dto.PurchasedTicketDto;
import com.example.ticketbookingsystem.entity.*;
import com.example.ticketbookingsystem.repository.PurchasedTicketRepository;
import com.example.ticketbookingsystem.repository.TicketRepository;
import com.example.ticketbookingsystem.service.PurchasedTicketsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PurchasedTicketsServiceTest {

    private static final Long ENTITY_ID = 1L;

    @Mock
    private PurchasedTicketRepository purchasedTicketRepository;

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private PurchasedTicketsService purchasedTicketsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        SportEvent sportEvent = new SportEvent();
        sportEvent.setEventName("Event Name");
        sportEvent.setEventDateTime(LocalDateTime.now());

        Arena arena = new Arena();
        arena.setName("Arena Name");
        arena.setCity("Arena City");

        Sector sector = new Sector();
        sector.setSectorName("Sector Name");
        sector.setArena(arena);

        Row row = new Row();
        row.setRowNumber(1);
        row.setSector(sector);

        Seat seat = new Seat();
        seat.setSeatNumber(1);
        seat.setRow(row);

        Ticket ticket = new Ticket();
        ticket.setId(ENTITY_ID);
        ticket.setSportEvent(sportEvent);
        ticket.setSeat(seat);
        ticket.setPrice(BigDecimal.valueOf(100.0));

        PurchasedTicket purchasedTicket = PurchasedTicket.builder()
                .userId(ENTITY_ID)
                .purchaseDate(LocalDateTime.now())
                .ticket(ticket)
                .build();

        when(ticketRepository.findById(any(Long.class))).thenReturn(Optional.of(ticket));
        when(purchasedTicketRepository.findAllByUserId(any(Long.class)))
                .thenReturn(Collections.singletonList(purchasedTicket));
    }

    @Test
    public void testSavePurchasedTickets() {
        List<Long> ticketIds = List.of(ENTITY_ID);

        purchasedTicketsService.savePurchasedTickets(ticketIds, ENTITY_ID);

        verify(ticketRepository, times(1)).save(any(Ticket.class));
        verify(purchasedTicketRepository, times(1)).saveAll(anyList());
    }

    @Test
    public void testFindAllByUserId() {
        List<PurchasedTicketDto> result = purchasedTicketsService.findAllByUserId(ENTITY_ID);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(purchasedTicketRepository, times(1)).findAllByUserId(ENTITY_ID);
    }

}