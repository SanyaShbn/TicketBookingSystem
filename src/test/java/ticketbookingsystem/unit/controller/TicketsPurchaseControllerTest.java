package ticketbookingsystem.unit.controller;

import com.example.ticketbookingsystem.controller.TicketsPurchaseController;
import com.example.ticketbookingsystem.dto.PurchasedTicketDto;
import com.example.ticketbookingsystem.dto.UserDto;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.service.PurchasedTicketsService;
import com.example.ticketbookingsystem.service.UserCartService;
import com.example.ticketbookingsystem.utils.AuthenticationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TicketsPurchaseControllerTest {

    private static final Long USER_ID = 1L;

    MockMvc mockMvc;

    @Mock
    private UserCartService userCartService;

    @Mock
    private MessageSource messageSource;

    @Mock
    private PurchasedTicketsService purchasedTicketsService;

    @Mock
    private AuthenticationUtil authenticationUtil;

    @InjectMocks
    private TicketsPurchaseController ticketsPurchaseController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(ticketsPurchaseController).build();
    }

    @Test
    public void testShowPurchasePage() throws Exception {
        mockMvc.perform(get("/purchase"))
                .andExpect(status().isOk())
                .andExpect(view().name("tickets-purchases-jsp/purchase"));
    }

    @Test
    public void testCommitPurchaseSuccess() throws Exception {
        UserDto userDto = UserDto.builder().id(USER_ID).build();
        List<Long> ticketIds = List.of(1L, 2L);

        when(authenticationUtil.getAuthenticatedUser()).thenReturn(Optional.of(userDto));
        when(userCartService.getTicketIds(userDto.getId())).thenReturn(ticketIds);

        mockMvc.perform(post("/purchase"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/purchasedTickets"));
    }

    @Test
    public void testCommitPurchaseDaoException() throws Exception {
        UserDto userDto = UserDto.builder().id(USER_ID).build();
        List<Long> ticketIds = List.of(1L, 2L);

        when(authenticationUtil.getAuthenticatedUser()).thenReturn(Optional.of(userDto));
        when(userCartService.getTicketIds(userDto.getId())).thenReturn(ticketIds);
        doThrow(new DaoCrudException(new Throwable())).when(purchasedTicketsService).savePurchasedTickets(ticketIds, userDto.getId());

        mockMvc.perform(post("/purchase"))
                .andExpect(status().isOk())
                .andExpect(view().name("tickets-purchases-jsp/purchase"))
                .andExpect(model().attributeExists("errors"));
    }

    @Test
    public void testGetPurchasedTicketsSuccess() throws Exception {
        UserDto userDto = UserDto.builder().id(USER_ID).build();
        List<PurchasedTicketDto> purchasedTickets = List.of(PurchasedTicketDto.builder().build());

        when(authenticationUtil.getAuthenticatedUser()).thenReturn(Optional.of(userDto));
        when(purchasedTicketsService.findAllByUserId(userDto.getId())).thenReturn(purchasedTickets);

        mockMvc.perform(get("/purchasedTickets"))
                .andExpect(status().isOk())
                .andExpect(view().name("tickets-purchases-jsp/purchased-tickets"))
                .andExpect(model().attributeExists("purchasedTickets"))
                .andExpect(model().attribute("purchasedTickets", purchasedTickets));
    }

    @Test
    public void testGetPurchasedTicketsDaoException() throws Exception {
        UserDto userDto = UserDto.builder().id(1L).build();

        when(authenticationUtil.getAuthenticatedUser()).thenReturn(Optional.of(userDto));
        doThrow(new DaoCrudException(new Throwable())).when(purchasedTicketsService).findAllByUserId(userDto.getId());

        mockMvc.perform(get("/purchasedTickets"))
                .andExpect(status().isOk())
                .andExpect(view().name("tickets-purchases-jsp/purchased-tickets"))
                .andExpect(model().attributeExists("errors"));
    }

    @Test
    public void testGetPurchasedTicketsUserNotAuthenticated() throws Exception {
        when(authenticationUtil.getAuthenticatedUser()).thenReturn(Optional.empty());

        mockMvc.perform(get("/purchasedTickets"))
                .andExpect(status().isOk())
                .andExpect(view().name("tickets-purchases-jsp/purchased-tickets"))
                .andExpect(model().attributeExists("errors"));
    }
}