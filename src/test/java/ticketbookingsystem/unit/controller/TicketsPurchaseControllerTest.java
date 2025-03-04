package ticketbookingsystem.unit.controller;

import com.example.ticketbookingsystem.controller.TicketsPurchaseController;
import com.example.ticketbookingsystem.dto.PurchasedTicketDto;
import com.example.ticketbookingsystem.exception.DaoResourceNotFoundException;
import com.example.ticketbookingsystem.service.PurchasedTicketsService;
import com.example.ticketbookingsystem.service.UserCartService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TicketsPurchaseControllerTest {

    private static final Long USER_ID = 1L;

    @Mock
    private UserCartService userCartService;

    @Mock
    private MessageSource messageSource;

    @Mock
    private PurchasedTicketsService purchasedTicketsService;

    @InjectMocks
    private TicketsPurchaseController ticketsPurchaseController;

    @Test
    public void testCommitPurchaseSuccess() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        List<Long> ticketIds = Arrays.asList(1L, 2L, 3L);
        when(userCartService.getTicketIds(anyLong())).thenReturn(ticketIds);
        doNothing().when(purchasedTicketsService).savePurchasedTickets(ticketIds, USER_ID);

        ResponseEntity<String> response = ticketsPurchaseController.commitPurchase(USER_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Purchase successful", response.getBody());
        verify(userCartService, times(1)).getTicketIds(anyLong());
        verify(purchasedTicketsService, times(1)).savePurchasedTickets(ticketIds, USER_ID);
    }

    @Test
    public void testGetPurchasedTicketsSuccess() {
        List<PurchasedTicketDto> purchasedTickets = Arrays.asList(
                PurchasedTicketDto.builder().build(), PurchasedTicketDto.builder().build());
        when(purchasedTicketsService.findAllByUserId(anyLong())).thenReturn(purchasedTickets);

        ResponseEntity<?> response = ticketsPurchaseController.getPurchasedTickets(USER_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(purchasedTickets.toString(), response.getBody());
        verify(purchasedTicketsService, times(1)).findAllByUserId(anyLong());
    }

}