package ticketbookingsystem.unit.controller;

import com.example.ticketbookingsystem.controller.TicketController;
import com.example.ticketbookingsystem.dto.PageResponse;
import com.example.ticketbookingsystem.dto.ticket_dto.TicketCreateEditDto;
import com.example.ticketbookingsystem.dto.ticket_dto.TicketFilter;
import com.example.ticketbookingsystem.dto.ticket_dto.TicketReadDto;
import com.example.ticketbookingsystem.service.TicketService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TicketControllerTest {

    private static final Long TICKET_ID = 1L;

    private static final Long EVENT_ID = 1L;

    private static final Long SEAT_ID = 1L;

    @Mock
    private TicketService ticketService;

    @InjectMocks
    private TicketController ticketController;

    @Test
    public void testFindAllTickets() {
        TicketReadDto ticket1 = TicketReadDto.builder().price(BigDecimal.valueOf(100.0)).build();
        TicketReadDto ticket2 = TicketReadDto.builder().price(BigDecimal.valueOf(200.0)).build();

        List<TicketReadDto> tickets = Arrays.asList(ticket1, ticket2);
        Page<TicketReadDto> page = new PageImpl<>(tickets);
        when(ticketService.findAllByEventId(any(Long.class), any(TicketFilter.class), any(Pageable.class))).thenReturn(page);

        PageResponse<TicketReadDto> response = ticketController.findAllTicketsByEventId(
                TICKET_ID, new TicketFilter(""), Pageable.unpaged());

        assertEquals(2, response.getContent().size());
        verify(ticketService, times(1)).findAllByEventId(
                any(Long.class), any(TicketFilter.class), any(Pageable.class));
    }

    @Test
    void testGetTicketById() {
        TicketReadDto ticket = TicketReadDto.builder().price(BigDecimal.valueOf(100.0)).build();
        when(ticketService.findById(TICKET_ID)).thenReturn(Optional.of(ticket));

        ResponseEntity<TicketReadDto> response = ticketController.getTicketById(TICKET_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(BigDecimal.valueOf(100.0), response.getBody().getPrice());
        verify(ticketService, times(1)).findById(TICKET_ID);
    }

    @Test
    public void testCreateTicket() {
        TicketCreateEditDto createEditDto = TicketCreateEditDto.builder().price(BigDecimal.valueOf(100.0)).build();

        TicketReadDto readDto = TicketReadDto.builder().price(BigDecimal.valueOf(100.0)).build();
        when(ticketService.createTicket(
                any(TicketCreateEditDto.class), any(Long.class), any(Long.class))).thenReturn(readDto);

        ResponseEntity<TicketReadDto> response = ticketController.createTicket(EVENT_ID, SEAT_ID, createEditDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(BigDecimal.valueOf(100.0), response.getBody().getPrice());
        verify(ticketService, times(1)).createTicket(
                any(TicketCreateEditDto.class), any(Long.class), any(Long.class));
    }

    @Test
    public void testUpdateTicket() {
        TicketCreateEditDto createEditDto = TicketCreateEditDto.builder().price(BigDecimal.valueOf(100.0)).build();

        TicketReadDto readDto = TicketReadDto.builder().price(BigDecimal.valueOf(100.0)).build();
        when(ticketService.updateTicket(eq(TICKET_ID), any(
                TicketCreateEditDto.class), any(Long.class), any(Long.class))).thenReturn(readDto);

        ResponseEntity<TicketReadDto> response = ticketController.updateTicket(
                EVENT_ID, SEAT_ID, TICKET_ID, createEditDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(BigDecimal.valueOf(100.0), response.getBody().getPrice());
        verify(ticketService, times(1)).updateTicket(eq(TICKET_ID),
                any(TicketCreateEditDto.class), any(Long.class), any(Long.class));
    }

    @Test
    public void testDeleteTicket() {
        doNothing().when(ticketService).deleteTicket(TICKET_ID);

        ResponseEntity<Map<String, String>> response = ticketController.deleteTicket(TICKET_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Ticket deleted successfully", response.getBody().get("message"));
        verify(ticketService, times(1)).deleteTicket(TICKET_ID);
    }

}