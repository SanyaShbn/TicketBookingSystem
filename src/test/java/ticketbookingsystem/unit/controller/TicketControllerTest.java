package ticketbookingsystem.unit.controller;

import com.example.ticketbookingsystem.controller.TicketController;
import com.example.ticketbookingsystem.dto.TicketCreateEditDto;
import com.example.ticketbookingsystem.dto.TicketReadDto;
import com.example.ticketbookingsystem.entity.TicketStatus;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.service.TicketService;
import com.example.ticketbookingsystem.utils.JspFilesResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TicketControllerTest {

    private static final Long TICKET_ID = 1L;
    private static final Long EVENT_ID = 1L;
    private static final Long SEAT_ID = 1L;

    MockMvc mockMvc;

    @Mock
    private TicketService ticketService;

    @InjectMocks
    private TicketController ticketController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(ticketController).build();
    }

    @Test
    public void testFindAllTickets() throws Exception {
        Page<TicketReadDto> ticketPage = new PageImpl<>(Collections.emptyList());

        when(ticketService.findAll(anyLong(), any(), any(Pageable.class))).thenReturn(ticketPage);

        mockMvc.perform(get("/admin/tickets")
                        .param("eventId", EVENT_ID.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name(JspFilesResolver.getPath("/tickets-jsp/tickets")))
                .andExpect(model().attributeExists("tickets"))
                .andExpect(model().attributeExists("filter"));
    }

    @Test
    public void testCreateTicket() throws Exception {
        TicketCreateEditDto ticketCreateEditDto = buildTicketCreateEditDto();

        doNothing().when(ticketService).createTicket(any(TicketCreateEditDto.class), anyLong(), anyLong());

        mockMvc.perform(post("/admin/tickets/create")
                        .param("eventId", EVENT_ID.toString())
                        .param("seatId", SEAT_ID.toString())
                        .flashAttr("ticketCreateEditDto", ticketCreateEditDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/tickets?eventId=" + EVENT_ID));
    }

    @Test
    public void testUpdateTicket() throws Exception {
        TicketCreateEditDto ticketCreateEditDto = buildTicketCreateEditDto();

        doNothing().when(ticketService).updateTicket(anyLong(), any(TicketCreateEditDto.class), anyLong(), anyLong());

        mockMvc.perform(post("/admin/tickets/{id}/update", TICKET_ID)
                        .param("eventId", EVENT_ID.toString())
                        .param("seatId", SEAT_ID.toString())
                        .flashAttr("ticketCreateEditDto", ticketCreateEditDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/tickets?eventId=" + EVENT_ID));
    }

    @Test
    public void testDeleteTicket() throws Exception {
        doNothing().when(ticketService).deleteTicket(anyLong());

        mockMvc.perform(post("/admin/tickets/{id}/delete", TICKET_ID)
                        .param("eventId", EVENT_ID.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/tickets?eventId=" + EVENT_ID));
    }

    @Test
    public void testDeleteTicketWithException() throws Exception {
        doThrow(new DaoCrudException(new Throwable())).when(ticketService).deleteTicket(anyLong());

        mockMvc.perform(post("/admin/tickets/{id}/delete", TICKET_ID)
                        .param("eventId", EVENT_ID.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name(JspFilesResolver.getPath("/error-jsp/error-page")))
                .andExpect(model().attributeExists("errors"));
    }

    private TicketCreateEditDto buildTicketCreateEditDto(){
        return TicketCreateEditDto.builder()
                .price(BigDecimal.valueOf(100))
                .status(TicketStatus.AVAILABLE)
                .build();
    }
}