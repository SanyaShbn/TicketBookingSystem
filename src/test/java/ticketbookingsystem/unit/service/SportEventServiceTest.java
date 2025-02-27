package ticketbookingsystem.unit.service;

import com.example.ticketbookingsystem.dto.QPredicates;
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
import com.example.ticketbookingsystem.utils.SortUtils;
import com.querydsl.core.types.Predicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.example.ticketbookingsystem.entity.QSportEvent.sportEvent;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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
        sportEventCreateEditDto = SportEventCreateEditDto.builder().build();
        sportEventReadDto = SportEventReadDto.builder().build();

//        when(sportEventCreateEditMapper.toEntity(any(SportEventCreateEditDto.class))).thenReturn(sportEventEntity);
//        when(sportEventReadMapper.toDto(any(SportEvent.class))).thenReturn(sportEventReadDto);
//        when(arenaService.findArenaById(any(Long.class))).thenReturn(arena);
    }

    @Test
    public void testFindAll() {
        when(sportEventReadMapper.toDto(any(SportEvent.class))).thenReturn(sportEventReadDto);
        when(sportEventRepository.findAll()).thenReturn(List.of(sportEventEntity));

        var result = sportEventService.findAll();

        assertEquals(1, result.size());
        assertEquals(sportEventReadDto, result.get(0));
        verify(sportEventRepository, times(1)).findAll();
    }

//    @Test
//    public void testFindAllSportEvents() {
//        when(sportEventReadMapper.toDto(any(SportEvent.class))).thenReturn(sportEventReadDto);
//
//        SportEventFilter sportEventFilter = mock(SportEventFilter.class);
//        Pageable pageable = PageRequest.of(0, 10);
//
//        when(sportEventFilter.startDate()).thenReturn(LocalDate.now().minusDays(1).atStartOfDay());
//        when(sportEventFilter.endDate()).thenReturn(LocalDate.now().plusDays(1).atStartOfDay());
//        when(sportEventFilter.arenaId()).thenReturn(ENTITY_ID);
//        when(sportEventFilter.sortOrder()).thenReturn("asc");
//
//        Predicate predicate = QPredicates.builder()
//                .add(sportEventFilter.startDate(), sportEvent.eventDateTime::after)
//                .add(sportEventFilter.endDate(), sportEvent.eventDateTime::before)
//                .add(sportEventFilter.arenaId(), sportEvent.arena.id::eq)
//                .build();
//
//        Map<String, String> sortOrders = new LinkedHashMap<>();
//        sortOrders.put("eventDateTime", "asc");
//
//        Sort sort = SortUtils.buildSort(sortOrders);
//        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
//
//        List<SportEvent> sportEvents = List.of(sportEventEntity);
//
//        when(sportEventRepository.findAllWithArena(eq(predicate), eq(sortedPageable))).thenReturn(sportEvents);
//
//        Page<SportEventReadDto> result = sportEventService.findAll(sportEventFilter, pageable);
//
//        assertNotNull(result);
//        assertEquals(1, result.getTotalElements());
//        assertEquals(sportEventReadDto, result.getContent().get(0));
//
//        verify(sportEventRepository, times(1)).findAllWithArena(predicate, sortedPageable);
//        verify(sportEventReadMapper, times(1)).toDto(sportEventEntity);
//    }

    @Test
    public void testFindById() {
        when(sportEventReadMapper.toDto(any(SportEvent.class))).thenReturn(sportEventReadDto);
        when(sportEventRepository.findById(ENTITY_ID)).thenReturn(Optional.of(sportEventEntity));

        var result = sportEventService.findById(ENTITY_ID);

        assertTrue(result.isPresent());
        assertEquals(sportEventReadDto, result.get());
        verify(sportEventRepository, times(1)).findById(ENTITY_ID);
    }

//    @Test
//    public void testCreateSportEvent() {
//        when(sportEventCreateEditMapper.toEntity(any(SportEventCreateEditDto.class))).thenReturn(sportEventEntity);
//        sportEventService.createSportEvent(sportEventCreateEditDto, arena.getId());
//
//        verify(sportEventRepository, times(1)).save(sportEventEntity);
//        verify(arenaService, times(1)).findArenaById(arena.getId());
//    }

//    @Test
//    public void testUpdateSportEvent() {
//        when(sportEventCreateEditMapper.toEntity(any(SportEventCreateEditDto.class))).thenReturn(sportEventEntity);
//
//        sportEventService.updateSportEvent(ENTITY_ID, sportEventCreateEditDto, arena.getId());
//
//        verify(sportEventRepository, times(1)).save(sportEventEntity);
//        verify(arenaService, times(1)).findArenaById(arena.getId());
//    }

    @Test
    public void testDeleteSportEvent() {
        when(sportEventRepository.existsById(ENTITY_ID)).thenReturn(true);

        sportEventService.deleteSportEvent(ENTITY_ID);

        verify(sportEventRepository, times(1)).deleteById(ENTITY_ID);
    }

    @Test
    void testDeleteSportEventNotFound() {
        when(sportEventRepository.existsById(ENTITY_ID)).thenReturn(false);

        assertThrows(DaoResourceNotFoundException.class, () -> sportEventService.deleteSportEvent(ENTITY_ID));
    }

//    @Test
//    void testCreateSportEventDataAccessException() {
//        when(sportEventCreateEditMapper.toEntity(any(SportEventCreateEditDto.class))).thenReturn(sportEventEntity);
//        when(sportEventRepository.save(any(SportEvent.class))).thenThrow(new DataAccessException("...") {});
//
//        assertThrows(DaoCrudException.class,
//                () -> sportEventService.createSportEvent(sportEventCreateEditDto, arena.getId()));
//    }

//    @Test
//    void testUpdateSportEventDataAccessException() {
//        when(sportEventCreateEditMapper.toEntity(any(SportEventCreateEditDto.class))).thenReturn(sportEventEntity);
//        when(sportEventRepository.save(any(SportEvent.class))).thenThrow(new DataAccessException("...") {});
//
//        assertThrows(DaoCrudException.class,
//                () -> sportEventService.updateSportEvent(ENTITY_ID, sportEventCreateEditDto, arena.getId()));
//    }

}