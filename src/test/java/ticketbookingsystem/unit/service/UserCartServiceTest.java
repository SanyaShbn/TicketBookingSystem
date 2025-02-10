package ticketbookingsystem.unit.service;

import com.example.ticketbookingsystem.dto.UserCartDto;
import com.example.ticketbookingsystem.entity.Ticket;
import com.example.ticketbookingsystem.entity.UserCart;
import com.example.ticketbookingsystem.entity.UserCartId;
import com.example.ticketbookingsystem.mapper.UserCartMapper;
import com.example.ticketbookingsystem.repository.TicketRepository;
import com.example.ticketbookingsystem.repository.UserCartRepository;
import com.example.ticketbookingsystem.service.UserCartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserCartServiceTest {

    private static final Long ENTITY_ID = 1L;

    @Mock
    private UserCartRepository userCartRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private UserCartMapper userCartMapper;

    @InjectMocks
    private UserCartService userCartService;

    private UserCart userCart;

    private UserCartDto userCartDto;

    private Ticket ticket;

    @BeforeEach
    void setUp() {
        userCart = new UserCart();
        ticket = new Ticket();
        ticket.setId(ENTITY_ID);

        userCartDto = UserCartDto.builder()
                .userId(ENTITY_ID)
                .ticketId(ENTITY_ID)
                .build();
        userCart.setId(new UserCartId(userCartDto.getUserId(), userCartDto.getTicketId()));
    }

    @Test
    public void testClearUserCart() {
        when(userCartRepository.findByUserId(any(Long.class))).thenReturn(Arrays.asList(userCart));
        when(ticketRepository.findById(any(Long.class))).thenReturn(Optional.of(ticket));

        userCartService.clearUserCart(ENTITY_ID);

        verify(ticketRepository, times(1)).saveAll(anyList());
        verify(userCartRepository, times(1)).deleteByUserId(any(Long.class));
    }

    @Test
    public void testAddItemToCart() {
        when(userCartMapper.toEntity(any(UserCartDto.class))).thenReturn(userCart);
        when(ticketRepository.findById(any(Long.class))).thenReturn(Optional.of(ticket));

        userCartService.addItemToCart(userCartDto);

        verify(userCartRepository, times(1)).save(any(UserCart.class));
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    public void testRemoveItemFromCart() {
        when(userCartMapper.toEntity(any(UserCartDto.class))).thenReturn(userCart);
        when(ticketRepository.findById(any(Long.class))).thenReturn(Optional.of(ticket));

        userCartService.removeItemFromCart(userCartDto);

        verify(ticketRepository, times(1)).saveAll(anyList());
        verify(userCartRepository, times(1)).delete(any(UserCart.class));
    }

    @Test
    public void testGetTicketIds() {
        when(userCartRepository.findByUserId(any(Long.class))).thenReturn(Collections.singletonList(userCart));

        List<Long> result = userCartService.getTicketIds(ENTITY_ID);

        assertNotNull(result);
        verify(userCartRepository, times(1)).findByUserId(any(Long.class));
    }

}