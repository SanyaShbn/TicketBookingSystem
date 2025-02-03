package ticketbookingsystem.unit.controller;

import com.example.ticketbookingsystem.dto.UserCartDto;
import com.example.ticketbookingsystem.dto.UserDto;
import com.example.ticketbookingsystem.service.TicketService;
import com.example.ticketbookingsystem.service.UserCartService;
import com.example.ticketbookingsystem.utils.AuthenticationUtil;
import com.example.ticketbookingsystem.controller.UserCartController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.Mockito.*;

public class UserCartControllerTest {

    private static final Long ENTITY_ID = 1L;

    private MockMvc mockMvc;

    @Mock
    private UserCartService userCartService;

    @Mock
    private TicketService ticketService;

    @Mock
    private AuthenticationUtil authenticationUtil;

    @InjectMocks
    private UserCartController userCartController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userCartController).build();
    }

    @Test
    public void testGetUserCartPage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user_cart"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view()
                        .name("tickets-purchases-jsp/view-available-tickets"));
    }

    @Test
    public void testHandleUserCartRequest_UserNotAuthenticated() throws Exception {
        when(authenticationUtil.getAuthenticatedUser()).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.post("/user_cart")
                        .param("ticketId", "1")
                        .param("action", "add")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .json("{\"success\": false, \"message\": \"User not authenticated\"}"));

        verify(authenticationUtil, times(1)).getAuthenticatedUser();
    }

    @Test
    public void testHandleUserCartRequest_AddAction_Success() throws Exception {
        UserDto userDto = UserDto.builder().id(ENTITY_ID).build();
        when(authenticationUtil.getAuthenticatedUser()).thenReturn(Optional.of(userDto));
        when(ticketService.getTicketStatus(ENTITY_ID)).thenReturn("AVAILABLE");

        mockMvc.perform(MockMvcRequestBuilders.post("/user_cart")
                        .param("ticketId", "1")
                        .param("action", "add")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("{\"success\": true}"));

        verify(userCartService, times(1)).addItemToCart(any(UserCartDto.class));
    }

    @Test
    public void testHandleUserCartRequest_ClearAction_Success() throws Exception {
        UserDto userDto = UserDto.builder().id(ENTITY_ID).build();
        when(authenticationUtil.getAuthenticatedUser()).thenReturn(Optional.of(userDto));

        mockMvc.perform(MockMvcRequestBuilders.post("/user_cart")
                        .param("action", "clear")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("{\"success\": true}"));

        verify(userCartService, times(1)).clearUserCart(userDto.getId());
    }

    @Test
    public void testHandleUserCartRequest_TicketNotFound() throws Exception {
        UserDto userDto = UserDto.builder().id(ENTITY_ID).build();
        when(authenticationUtil.getAuthenticatedUser()).thenReturn(Optional.of(userDto));
        when(ticketService.getTicketStatus(ENTITY_ID)).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/user_cart")
                        .param("ticketId", "1")
                        .param("action", "add")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .json("{\"success\": false, \"message\": \"Ticket not found\"}"));
    }
}