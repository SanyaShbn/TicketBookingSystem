package ticketbookingsystem.integration;

import com.example.ticketbookingsystem.config.WebMvcConfig;
import com.example.ticketbookingsystem.entity.*;
import com.example.ticketbookingsystem.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ticketbookingsystem.utils.TestUtils.*;

@SpringJUnitConfig
@ContextConfiguration(classes = {TestJpaConfig.class, WebMvcConfig.class})
@ActiveProfiles("test")
@WebAppConfiguration
public class TicketsPurchaseControllerIT {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private PurchasedTicketRepository purchasedTicketsRepository;

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

    private MockMvc mockMvc;

    private Seat savedSeat;

    private SportEvent savedSportEvent;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

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

    @AfterEach
    void tearDown() {
        purchasedTicketsRepository.deleteAll();
        userRepository.deleteAll();
        ticketRepository.deleteAll();
        seatRepository.deleteAll();
        rowRepository.deleteAll();
        sectorRepository.deleteAll();
        sportEventRepository.deleteAll();
        arenaRepository.deleteAll();
    }

    @Test
    public void testShowPurchasePage() throws Exception {
        mockMvc.perform(get("/purchase"))
                .andExpect(status().isOk())
                .andExpect(view().name("tickets-purchases-jsp/purchase"));
    }

    @Test
    @WithMockUser(username = "testUser@gmail.com")
    public void testCommitPurchaseWithInsufficientFunds() throws Exception {
        User user = User.builder()
                .email("test@example.com")
                .password("password")
                .build();
        userRepository.save(user);

        mockMvc.perform(post("/purchase")
                        .with(csrf())
                        .sessionAttr("user", user))
                .andExpect(status().isOk())
                .andExpect(view().name("tickets-purchases-jsp/purchase"))
                .andExpect(model().attributeExists("errors"));
    }

    @Test
    @WithMockUser(username = "testUser@gmail.com")
    public void testGetPurchasedTickets() throws Exception {
        User user = User.builder()
                .email("test@example.com")
                .password("password")
                .build();
        userRepository.save(user);

        Ticket ticket = Ticket.builder()
                .price(BigDecimal.valueOf(10))
                .status(TicketStatus.AVAILABLE)
                .seat(savedSeat)
                .sportEvent(savedSportEvent)
                .build();
        ticketRepository.save(ticket);

        PurchasedTicket purchasedTicket = PurchasedTicket.builder()
                .purchaseDate(LocalDateTime.now())
                .ticket(ticket)
                .userId(user.getId())
                .build();
        purchasedTicketsRepository.save(purchasedTicket);

        mockMvc.perform(get("/purchasedTickets")
                        .sessionAttr("user", user))
                .andExpect(status().isOk())
                .andExpect(view().name("tickets-purchases-jsp/purchased-tickets"));
    }
}