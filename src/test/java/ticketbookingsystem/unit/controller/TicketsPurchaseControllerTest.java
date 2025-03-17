package ticketbookingsystem.unit.controller;

import com.example.ticketbookingsystem.controller.TicketsPurchaseController;
import com.example.ticketbookingsystem.dto.PurchasedTicketDto;
import com.example.ticketbookingsystem.service.PurchasedTicketsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TicketsPurchaseControllerTest {

    private static final Long USER_ID = 1L;

    @Mock
    private PurchasedTicketsService purchasedTicketsService;

    @InjectMocks
    private TicketsPurchaseController ticketsPurchaseController;

    @Test
    public void testGetPurchasedTicketsSuccess() {
        List<PurchasedTicketDto> purchasedTickets = Arrays.asList(
                PurchasedTicketDto.builder().build(), PurchasedTicketDto.builder().build());
        when(purchasedTicketsService.findAllByUserId(anyLong())).thenReturn(purchasedTickets);

        ResponseEntity<?> response = ticketsPurchaseController.getPurchasedTickets(USER_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(purchasedTickets, response.getBody());
        verify(purchasedTicketsService, times(1)).findAllByUserId(anyLong());
    }

}