package ticketbookingsystem.unit.service;

import com.example.ticketbookingsystem.dto.PurchasedTicketDto;
import com.example.ticketbookingsystem.entity.*;
import com.example.ticketbookingsystem.repository.PurchasedTicketRepository;
import com.example.ticketbookingsystem.repository.TicketRepository;
import com.example.ticketbookingsystem.repository.UserCartRepository;
import com.example.ticketbookingsystem.repository.UserRepository;
import com.example.ticketbookingsystem.service.PurchasedTicketsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PurchasedTicketsServiceTest {

    private static final Long ENTITY_ID = 1L;

    @Mock
    private PurchasedTicketRepository purchasedTicketRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserCartRepository userCartRepository;

    @InjectMocks
    private PurchasedTicketsService purchasedTicketsService;

    private Ticket ticket;

    private PurchasedTicket purchasedTicket;

    @BeforeEach
    void setUp() {
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

        ticket = new Ticket();
        ticket.setId(ENTITY_ID);
        ticket.setSportEvent(sportEvent);
        ticket.setSeat(seat);
        ticket.setPrice(BigDecimal.valueOf(100.0));

        purchasedTicket = PurchasedTicket.builder()
                .userId(ENTITY_ID)
                .purchaseDate(LocalDateTime.now())
                .ticket(ticket)
                .build();

        when(userRepository.existsById(any(Long.class))).thenReturn(true);
    }

    @Test
    public void testSavePurchasedTickets() {
        when(userRepository.existsById(ENTITY_ID)).thenReturn(true);
        when(ticketRepository.findAllById(anyList())).thenReturn(List.of(ticket));
        doNothing().when(userCartRepository).deleteByUserId(any(Long.class));
        when(ticketRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(purchasedTicketRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        List<Long> ticketIds = List.of(ENTITY_ID);
        purchasedTicketsService.savePurchasedTickets(ticketIds, ENTITY_ID);

        verify(ticketRepository, times(1)).saveAll(anyList());
        verify(purchasedTicketRepository, times(1)).saveAll(anyList());
        verify(userCartRepository, times(1)).deleteByUserId(ENTITY_ID);
    }

    @Test
    public void testFindAllByUserId() {
        when(purchasedTicketRepository.findAllByUserId(any(Long.class)))
                .thenReturn(Collections.singletonList(purchasedTicket));

        List<PurchasedTicketDto> result = purchasedTicketsService.findAllByUserId(ENTITY_ID);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(purchasedTicketRepository, times(1)).findAllByUserId(ENTITY_ID);
    }

}