package ticketbookingsystem.unit.service;

import com.example.ticketbookingsystem.dto.SportEventCreateEditDto;
import com.example.ticketbookingsystem.dto.SportEventReadDto;
import com.example.ticketbookingsystem.entity.Arena;
import com.example.ticketbookingsystem.entity.SportEvent;
import com.example.ticketbookingsystem.mapper.SportEventCreateEditMapper;
import com.example.ticketbookingsystem.mapper.SportEventReadMapper;
import com.example.ticketbookingsystem.repository.SportEventRepository;
import com.example.ticketbookingsystem.service.ArenaService;
import com.example.ticketbookingsystem.service.SportEventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SportEventServiceTest {

    private static final Long ENTITY_ID = 1L;

    @Mock
    private SportEventRepository sportEventRepository;

    @Mock
    private SportEventCreateEditMapper sportEventCreateEditMapper;

    @Mock
    private SportEventReadMapper sportEventReadMapper;

    @Mock
    private ArenaService arenaService;

    @InjectMocks
    private SportEventService sportEventService;

    private SportEvent sportEvent;

    private Arena arena;

    private SportEventCreateEditDto sportEventCreateEditDto;

    private SportEventReadDto sportEventReadDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sportEvent = new SportEvent();
        sportEvent.setId(ENTITY_ID);
        arena = new Arena();
        arena.setId(ENTITY_ID);
        sportEventCreateEditDto = SportEventCreateEditDto.builder().build();
        sportEventReadDto = SportEventReadDto.builder().build();

        when(sportEventCreateEditMapper.toEntity(any(SportEventCreateEditDto.class))).thenReturn(sportEvent);
        when(sportEventReadMapper.toDto(any(SportEvent.class))).thenReturn(sportEventReadDto);
        when(arenaService.findArenaById(any(Long.class))).thenReturn(arena);
    }

    @Test
    public void testFindAll() {
        when(sportEventRepository.findAll()).thenReturn(List.of(sportEvent));

        var result = sportEventService.findAll();

        assertEquals(1, result.size());
        assertEquals(sportEventReadDto, result.get(0));
        verify(sportEventRepository, times(1)).findAll();
    }

    @Test
    public void testFindById() {
        when(sportEventRepository.findById(ENTITY_ID)).thenReturn(Optional.of(sportEvent));

        var result = sportEventService.findById(ENTITY_ID);

        assertTrue(result.isPresent());
        assertEquals(sportEventReadDto, result.get());
        verify(sportEventRepository, times(1)).findById(ENTITY_ID);
    }

    @Test
    public void testCreateSportEvent() {
        sportEventService.createSportEvent(sportEventCreateEditDto, arena.getId());

        verify(sportEventRepository, times(1)).save(sportEvent);
        verify(arenaService, times(1)).findArenaById(arena.getId());
    }

    @Test
    public void testUpdateSportEvent() {
        when(sportEventRepository.findById(any(Long.class))).thenReturn(Optional.of(sportEvent));

        sportEventService.updateSportEvent(ENTITY_ID, sportEventCreateEditDto, arena.getId());

        verify(sportEventRepository, times(1)).save(sportEvent);
        verify(arenaService, times(1)).findArenaById(arena.getId());
    }

    @Test
    public void testDeleteSportEvent() {
        when(sportEventRepository.findById(ENTITY_ID)).thenReturn(Optional.of(sportEvent));

        sportEventService.deleteSportEvent(ENTITY_ID);

        verify(sportEventRepository, times(1)).deleteById(ENTITY_ID);
    }

}