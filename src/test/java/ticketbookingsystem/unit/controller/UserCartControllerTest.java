package ticketbookingsystem.unit.controller;

import com.example.ticketbookingsystem.dto.UserCartDto;
import com.example.ticketbookingsystem.service.TicketService;
import com.example.ticketbookingsystem.service.UserCartService;
import com.example.ticketbookingsystem.controller.UserCartController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserCartControllerTest {

    private static final Long USER_ID = 1L;

    private static final Long TICKET_ID = 1L;

    @Mock
    private UserCartService userCartService;

    @Mock
    private TicketService ticketService;

    @InjectMocks
    private UserCartController userCartController;

    @Test
    public void testAddItemToCart_success() {
        when(ticketService.getTicketStatus(anyLong())).thenReturn("AVAILABLE");
        doNothing().when(userCartService).addItemToCart(any(UserCartDto.class));

        ResponseEntity<String> response = userCartController.handleUserCartRequest(USER_ID, TICKET_ID, "add");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Item added to cart", response.getBody());
        verify(userCartService, times(1)).addItemToCart(any(UserCartDto.class));
    }

    @Test
    void testRemoveItemFromCart_success() {
        when(ticketService.getTicketStatus(anyLong())).thenReturn("RESERVED");
        doNothing().when(userCartService).removeItemFromCart(any(UserCartDto.class));

        ResponseEntity<String> response = userCartController.handleUserCartRequest(USER_ID, TICKET_ID, "remove");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Item removed from cart", response.getBody());
        verify(userCartService, times(1)).removeItemFromCart(any(UserCartDto.class));
    }

    @Test
    public void testClearCart() {
        doNothing().when(userCartService).clearUserCart(anyLong());

        ResponseEntity<String> response = userCartController.handleUserCartRequest(
                USER_ID, null, "clear");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Cart cleared", response.getBody());
        verify(userCartService, times(1)).clearUserCart(anyLong());
    }

    @Test
    public void testHandleUserCartRequest_ticketNotFound() {
        ResponseEntity<String> response = userCartController.handleUserCartRequest(USER_ID, null, "add");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Ticket ID is required for add action", response.getBody());
        verify(userCartService, never()).addItemToCart(any(UserCartDto.class));
    }

    @Test
    void testAddItemToCart_concurrencyError() {
        when(ticketService.getTicketStatus(anyLong())).thenReturn("RESERVED");

        ResponseEntity<String> response = userCartController.handleUserCartRequest(USER_ID, TICKET_ID, "add");

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Concurrency error", response.getBody());
        verify(userCartService, never()).addItemToCart(any(UserCartDto.class));
    }

    @Test
    void testInvalidAction() {
        ResponseEntity<String> response = userCartController.handleUserCartRequest(
                USER_ID, null, "invalid");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid action", response.getBody());
        verify(userCartService, never()).clearUserCart(anyLong());
        verify(userCartService, never()).addItemToCart(any(UserCartDto.class));
        verify(userCartService, never()).removeItemFromCart(any(UserCartDto.class));
    }
}