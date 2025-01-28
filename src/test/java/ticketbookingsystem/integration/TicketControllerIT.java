package ticketbookingsystem.integration;

import com.example.ticketbookingsystem.config.WebMvcConfig;
import com.example.ticketbookingsystem.dto.*;
import com.example.ticketbookingsystem.entity.*;
import com.example.ticketbookingsystem.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ticketbookingsystem.test_config.TestJpaConfig;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringJUnitConfig
@ContextConfiguration(classes = {TestJpaConfig.class, WebMvcConfig.class})
@ActiveProfiles("test")
@WebAppConfiguration
public class TicketControllerIT {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ArenaRepository arenaRepository;

    @Autowired
    private SportEventRepository sportEventRepository;

    @Autowired
    private SectorRepository sectorRepository;

    @Autowired
    private RowRepository rowRepository;

    private SportEvent savedSportEvent;

    private Seat savedSeat;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        ticketRepository.deleteAll();
        seatRepository.deleteAll();

        Arena arena = buildArena();
        Arena savedArena = arenaRepository.save(arena);

        SportEvent sportEvent = buildSportEvent(savedArena);
        savedSportEvent = sportEventRepository.save(sportEvent);

        Sector sector = buildSector(savedArena);
        sectorRepository.save(sector);

        Row row = buildRow(sector);
        rowRepository.save(row);

        Seat seat = buildSeat(row);
        savedSeat = seatRepository.save(seat);
    }

    @Test
    public void testCreateTicket() throws Exception {
        TicketCreateEditDto ticketCreateEditDto = buildTicketCreateEditDto();

        mockMvc.perform(post("/admin/tickets/create")
                        .param("eventId", savedSportEvent.getId().toString())
                        .param("seatId", "1")
                        .flashAttr("ticketCreateEditDto", ticketCreateEditDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/tickets?eventId=" + savedSportEvent.getId()));

        assertEquals(1, ticketRepository.findAll().size());
    }

    @Test
    public void testUpdateTicket() throws Exception {
        Ticket ticket = buildTicket();
        Ticket savedTicket = ticketRepository.save(ticket);

        TicketCreateEditDto ticketCreateEditDto = TicketCreateEditDto.builder()
                .price(BigDecimal.valueOf(20))
                .status(TicketStatus.SOLD)
                .build();

        mockMvc.perform(post("/admin/tickets/{id}/update", savedTicket.getId())
                        .param("eventId", savedSportEvent.getId().toString())
                        .param("seatId", "1")
                        .flashAttr("ticketCreateEditDto", ticketCreateEditDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/tickets?eventId=" + savedSportEvent.getId()));

        Ticket updatedTicket = ticketRepository.findById(savedTicket.getId()).orElse(null);
        assertEquals(TicketStatus.SOLD, updatedTicket.getStatus());
    }

    @Test
    public void testDeleteTicket() throws Exception {
        Ticket ticket = buildTicket();
        ticket.setSportEvent(savedSportEvent);
        Ticket savedTicket = ticketRepository.save(ticket);

        mockMvc.perform(post("/admin/tickets/{id}/delete", savedTicket.getId())
                        .param("eventId", savedSportEvent.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/tickets?eventId=" + savedSportEvent.getId()));

        assertTrue(ticketRepository.findById(savedTicket.getId()).isEmpty());
    }

    private TicketCreateEditDto buildTicketCreateEditDto(){
        return TicketCreateEditDto.builder()
                .status(TicketStatus.AVAILABLE)
                .price(BigDecimal.valueOf(10))
                .build();
    }

    private Ticket buildTicket(){
        return Ticket.builder()
                .status(TicketStatus.AVAILABLE)
                .sportEvent(savedSportEvent)
                .price(BigDecimal.valueOf(10))
                .seat(savedSeat)
                .build();
    }

    private Arena buildArena(){
        return Arena.builder()
                .name("Test Arena")
                .city("Test City")
                .capacity(1)
                .build();
    }

    private SportEvent buildSportEvent(Arena savedArena){
        return SportEvent.builder()
                .eventName("Test Event")
                .eventDateTime(LocalDateTime.now())
                .arena(savedArena)
                .build();
    }

    private Sector buildSector(Arena arena){
        return Sector.builder()
                .sectorName("Test Sector")
                .maxRowsNumb(10)
                .maxSeatsNumb(100)
                .arena(arena)
                .build();
    }

    private Row buildRow(Sector sector) {
        return Row.builder()
                .rowNumber(1)
                .seatsNumb(5)
                .sector(sector)
                .build();
    }

    private Seat buildSeat(Row row){
        return Seat.builder()
                .seatNumber(1)
                .row(row)
                .build();
    }
}