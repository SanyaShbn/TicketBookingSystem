package ticketbookingsystem.unit.service;

import com.example.ticketbookingsystem.dto.sport_event_dto.SportEventCreateEditDto;
import com.example.ticketbookingsystem.dto.sport_event_dto.SportEventFilter;
import com.example.ticketbookingsystem.dto.sport_event_dto.SportEventReadDto;
import com.example.ticketbookingsystem.entity.Arena;
import com.example.ticketbookingsystem.entity.SportEvent;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.exception.DaoResourceNotFoundException;
import com.example.ticketbookingsystem.mapper.sport_event_mapper.SportEventCreateEditMapper;
import com.example.ticketbookingsystem.mapper.sport_event_mapper.SportEventReadMapper;
import com.example.ticketbookingsystem.repository.SportEventRepository;
import com.example.ticketbookingsystem.service.ArenaService;
import com.example.ticketbookingsystem.service.SportEventService;
import com.querydsl.core.types.Predicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SportEventServiceTest {

    private static final Long ENTITY_ID = 1L;

    private static final String SORT_ORDER = "ASC";

    private static final String ARENA_NAME = "Test_Arena";

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

    private SportEvent sportEventEntity;

    private Arena arena;

    private SportEventCreateEditDto sportEventCreateEditDto;

    private SportEventReadDto sportEventReadDto;

    @BeforeEach
    void setUp() {
        sportEventEntity = new SportEvent();
        sportEventEntity.setId(ENTITY_ID);
        arena = new Arena();
        arena.setId(ENTITY_ID);
        arena.setName(ARENA_NAME);
        sportEventCreateEditDto = SportEventCreateEditDto.builder().build();
        sportEventReadDto = SportEventReadDto.builder().build();
    }

    @Test
    public void testFindAllSportEvents() {
        SportEventFilter sportEventFilter = new SportEventFilter(
                LocalDateTime.now(),
                LocalDateTime.now(),
                ARENA_NAME,
                SORT_ORDER);
        Pageable pageable = PageRequest.of(0, 10);
        Page<SportEvent> sportEventPage = new PageImpl<>(List.of(sportEventEntity), pageable, 1);
        List<SportEventReadDto> sportEventReadDtos = List.of(sportEventReadDto);

        when(sportEventRepository.findAllWithArena(any(Predicate.class), any(Pageable.class)))
                .thenReturn(sportEventPage);
        when(sportEventReadMapper.toDto(any(SportEvent.class))).thenReturn(sportEventReadDto);

        Page<SportEventReadDto> result = sportEventService.findAll(sportEventFilter, pageable);

        assertEquals(sportEventReadDtos, result.getContent());
        verify(sportEventRepository, times(1))
                .findAllWithArena(any(Predicate.class), any(Pageable.class));
        verify(sportEventReadMapper, times(1)).toDto(any(SportEvent.class));
    }

    @Test
    public void testFindById() {
        when(sportEventReadMapper.toDto(any(SportEvent.class))).thenReturn(sportEventReadDto);
        when(sportEventRepository.findById(ENTITY_ID)).thenReturn(Optional.of(sportEventEntity));

        var result = sportEventService.findById(ENTITY_ID);

        assertTrue(result.isPresent());
        assertEquals(sportEventReadDto, result.get());
        verify(sportEventRepository, times(1)).findById(ENTITY_ID);
    }

    @Test
    public void testCreateSportEvent() {
        when(sportEventCreateEditMapper.toEntity(any(SportEventCreateEditDto.class))).thenReturn(sportEventEntity);
        sportEventService.createSportEvent(sportEventCreateEditDto, arena.getId());

        verify(sportEventRepository, times(1)).save(sportEventEntity);
        verify(arenaService, times(1)).findArenaById(arena.getId());
    }

    @Test
    public void testUpdateSportEvent() {
        when(sportEventCreateEditMapper.toEntity(any(SportEventCreateEditDto.class))).thenReturn(sportEventEntity);
        when(sportEventRepository.findById(ENTITY_ID)).thenReturn(Optional.of(new SportEvent()));
        when(arenaService.findArenaById(arena.getId())).thenReturn(arena);
        when(sportEventRepository.save(any(SportEvent.class))).thenReturn(sportEventEntity);

        sportEventService.updateSportEvent(ENTITY_ID, sportEventCreateEditDto, arena.getId());

        verify(sportEventRepository, times(1)).findById(ENTITY_ID);
        verify(sportEventCreateEditMapper, times(1)).toEntity(
                any(SportEventCreateEditDto.class)
        );
        verify(sportEventRepository, times(1)).save(sportEventEntity);
        verify(arenaService, times(1)).findArenaById(arena.getId());
    }

    @Test
    public void testDeleteSportEvent() {
        when(sportEventRepository.findById(ENTITY_ID)).thenReturn(Optional.of(sportEventEntity));

        sportEventService.deleteSportEvent(ENTITY_ID);

        verify(sportEventRepository, times(1)).deleteById(ENTITY_ID);

        verify(sportEventRepository, times(1)).findById(ENTITY_ID);
        verify(sportEventRepository, times(1)).deleteById(ENTITY_ID);
    }

    @Test
    void testDeleteSportEventNotFound() {
        when(sportEventRepository.findById(ENTITY_ID)).thenReturn(Optional.empty());

        assertThrows(DaoResourceNotFoundException.class, () -> sportEventService.deleteSportEvent(ENTITY_ID));

        verify(sportEventRepository, times(1)).findById(ENTITY_ID);
        verify(sportEventRepository, never()).deleteById(anyLong());
    }

    @Test
    void testCreateSportEventDataAccessException() {
        when(sportEventCreateEditMapper.toEntity(any(SportEventCreateEditDto.class))).thenReturn(sportEventEntity);
        when(sportEventRepository.save(any(SportEvent.class))).thenThrow(new DataAccessException("...") {});

        assertThrows(DaoCrudException.class,
                () -> sportEventService.createSportEvent(sportEventCreateEditDto, arena.getId()));
    }

}