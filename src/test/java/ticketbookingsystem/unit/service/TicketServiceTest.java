package ticketbookingsystem.unit.service;

import com.example.ticketbookingsystem.dto.seat_dto.SeatReadDto;
import com.example.ticketbookingsystem.dto.sport_event_dto.SportEventReadDto;
import com.example.ticketbookingsystem.dto.ticket_dto.TicketCreateEditDto;
import com.example.ticketbookingsystem.dto.ticket_dto.TicketFilter;
import com.example.ticketbookingsystem.dto.ticket_dto.TicketReadDto;
import com.example.ticketbookingsystem.entity.Seat;
import com.example.ticketbookingsystem.entity.SportEvent;
import com.example.ticketbookingsystem.entity.Ticket;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.exception.DaoResourceNotFoundException;
import com.example.ticketbookingsystem.mapper.seat_mapper.SeatReadMapper;
import com.example.ticketbookingsystem.mapper.sport_event_mapper.SportEventReadMapper;
import com.example.ticketbookingsystem.mapper.ticket_mapper.TicketCreateEditMapper;
import com.example.ticketbookingsystem.mapper.ticket_mapper.TicketReadMapper;
import com.example.ticketbookingsystem.repository.TicketRepository;
import com.example.ticketbookingsystem.service.SeatService;
import com.example.ticketbookingsystem.service.SportEventService;
import com.example.ticketbookingsystem.service.TicketService;
import com.example.ticketbookingsystem.utils.SortUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {

    private static final Long ENTITY_ID = 1L;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private TicketReadMapper ticketReadMapper;

    @Mock
    private TicketCreateEditMapper ticketCreateEditMapper;

    @Mock
    private SportEventReadMapper sportEventReadMapper;

    @Mock
    private SeatReadMapper seatReadMapper;

    @Mock
    private SportEventService sportEventService;

    @Mock
    private SeatService seatService;

    @InjectMocks
    private TicketService ticketService;

    private Ticket ticket;

    private TicketCreateEditDto ticketCreateEditDto;

    private TicketReadDto ticketReadDto;

    @BeforeEach
    void setUp() {
        ticket = new Ticket();
        ticket.setId(ENTITY_ID);
        ticketCreateEditDto = TicketCreateEditDto.builder().build();
        ticketReadDto = TicketReadDto.builder().build();
    }

    @Test
    public void testFindAll() {
        when(ticketReadMapper.toDto(any(Ticket.class))).thenReturn(ticketReadDto);
        when(ticketRepository.findAll()).thenReturn(List.of(ticket));

        var result = ticketService.findAll();

        assertEquals(1, result.size());
        assertEquals(ticketReadDto, result.get(0));
        verify(ticketRepository, times(1)).findAll();
    }

    @Test
    public void testFindAllTickets() {
        when(ticketReadMapper.toDto(any(Ticket.class))).thenReturn(ticketReadDto);
        TicketFilter ticketFilter = mock(TicketFilter.class);
        Pageable pageable = PageRequest.of(0, 10);
        when(ticketFilter.priceSortOrder()).thenReturn("asc");

        Map<String, String> sortOrders = new LinkedHashMap<>();
        sortOrders.put("price", "asc");
        Sort sort = SortUtils.buildSort(sortOrders);
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        List<Ticket> tickets = List.of(ticket);
        when(ticketRepository.findAllBySportEventId(eq(ENTITY_ID), eq(sortedPageable))).thenReturn(new PageImpl<>(tickets));

        Page<TicketReadDto> result = ticketService.findAll(ENTITY_ID, ticketFilter, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(ticketReadDto, result.getContent().get(0));
        verify(ticketRepository, times(1)).findAllBySportEventId(ENTITY_ID, sortedPageable);
        verify(ticketReadMapper, times(1)).toDto(ticket);
    }

    @Test
    public void testFindById() {
        when(ticketReadMapper.toDto(any(Ticket.class))).thenReturn(ticketReadDto);
        when(ticketRepository.findById(ENTITY_ID)).thenReturn(Optional.of(ticket));

        var result = ticketService.findById(ENTITY_ID);

        assertTrue(result.isPresent());
        assertEquals(ticketReadDto, result.get());
        verify(ticketRepository, times(1)).findById(ENTITY_ID);
    }

    @Test
    void createTicket() {
        SportEventReadDto sportEventReadDto = SportEventReadDto.builder().build();
        SeatReadDto seatReadDto = SeatReadDto.builder().build();
        when(sportEventService.findById(any(Long.class))).thenReturn(Optional.of(sportEventReadDto));
        when(seatService.findById(any(Long.class))).thenReturn(Optional.of(seatReadDto));
        when(sportEventReadMapper.toEntity(any(SportEventReadDto.class))).thenReturn(new SportEvent());
        when(seatReadMapper.toEntity(any(SeatReadDto.class))).thenReturn(new Seat());
        when(ticketCreateEditMapper.toEntity(any(TicketCreateEditDto.class))).thenReturn(ticket);

        ticketService.createTicket(ticketCreateEditDto, ENTITY_ID, ENTITY_ID);

        verify(ticketRepository, times(1)).save(ticket);
    }

    @Test
    void updateTicket() {
        SportEventReadDto sportEventReadDto = SportEventReadDto.builder().build();
        SeatReadDto seatReadDto = SeatReadDto.builder().build();
        when(sportEventService.findById(any(Long.class))).thenReturn(Optional.of(sportEventReadDto));
        when(seatService.findById(any(Long.class))).thenReturn(Optional.of(seatReadDto));
        when(sportEventReadMapper.toEntity(any(SportEventReadDto.class))).thenReturn(new SportEvent());
        when(seatReadMapper.toEntity(any(SeatReadDto.class))).thenReturn(new Seat());
        when(ticketCreateEditMapper.toEntity(any(TicketCreateEditDto.class))).thenReturn(ticket);

        ticketService.updateTicket(ENTITY_ID, ticketCreateEditDto, ENTITY_ID, ENTITY_ID);

        verify(ticketRepository, times(1)).save(ticket);
    }

    @Test
    void deleteTicket() {
        when(ticketRepository.existsById(ENTITY_ID)).thenReturn(true);

        ticketService.deleteTicket(ENTITY_ID);

        verify(ticketRepository, times(1)).deleteById(ENTITY_ID);
    }

    @Test
    void testGetTicketStatus() {
        when(ticketRepository.findStatusById(ENTITY_ID)).thenReturn(Optional.of("AVAILABLE"));

        String status = ticketService.getTicketStatus(ENTITY_ID);

        assertEquals("AVAILABLE", status);
        verify(ticketRepository, times(1)).findStatusById(ENTITY_ID);
    }

    @Test
    void testDeleteTicketNotFound() {
        when(ticketRepository.existsById(ENTITY_ID)).thenReturn(false);

        assertThrows(DaoResourceNotFoundException.class, () -> ticketService.deleteTicket(ENTITY_ID));
    }

    @Test
    void testCreateTicketDataAccessException() {
        SportEventReadDto sportEventReadDto = SportEventReadDto.builder().build();
        SeatReadDto seatReadDto = SeatReadDto.builder().build();
        when(sportEventService.findById(any(Long.class))).thenReturn(Optional.of(sportEventReadDto));
        when(seatService.findById(any(Long.class))).thenReturn(Optional.of(seatReadDto));
        when(ticketCreateEditMapper.toEntity(any(TicketCreateEditDto.class))).thenReturn(ticket);
        when(ticketRepository.save(any(Ticket.class))).thenThrow(new DataAccessException("...") {});

        assertThrows(DaoCrudException.class,
                () -> ticketService.createTicket(ticketCreateEditDto, ENTITY_ID, ENTITY_ID));
    }

    @Test
    void testUpdateTicketDataAccessException() {
        SportEventReadDto sportEventReadDto = SportEventReadDto.builder().build();
        SeatReadDto seatReadDto = SeatReadDto.builder().build();
        when(sportEventService.findById(any(Long.class))).thenReturn(Optional.of(sportEventReadDto));
        when(seatService.findById(any(Long.class))).thenReturn(Optional.of(seatReadDto));
        when(ticketCreateEditMapper.toEntity(any(TicketCreateEditDto.class))).thenReturn(ticket);
        when(ticketRepository.save(any(Ticket.class))).thenThrow(new DataAccessException("...") {});

        assertThrows(DaoCrudException.class,
                () -> ticketService.updateTicket(ENTITY_ID, ticketCreateEditDto, ENTITY_ID, ENTITY_ID));
    }
}