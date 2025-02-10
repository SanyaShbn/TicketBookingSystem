package ticketbookingsystem.unit.service;

import com.example.ticketbookingsystem.dto.arena_dto.ArenaCreateEditDto;
import com.example.ticketbookingsystem.dto.arena_dto.ArenaFilter;
import com.example.ticketbookingsystem.dto.arena_dto.ArenaReadDto;
import com.example.ticketbookingsystem.entity.Arena;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.exception.DaoResourceNotFoundException;
import com.example.ticketbookingsystem.mapper.arena_mapper.ArenaCreateEditMapper;
import com.example.ticketbookingsystem.mapper.arena_mapper.ArenaReadMapper;
import com.example.ticketbookingsystem.repository.ArenaRepository;
import com.example.ticketbookingsystem.service.ArenaService;
import com.querydsl.core.types.Predicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ArenaServiceTest {

    private static final Long ENTITY_ID = 1L;

    @Mock
    private ArenaRepository arenaRepository;

    @Mock
    private ArenaReadMapper arenaReadMapper;

    @Mock
    private ArenaCreateEditMapper arenaCreateEditMapper;

    @Captor
    private ArgumentCaptor<Predicate> predicateCaptor;

    @InjectMocks
    private ArenaService arenaService;

    private Arena arena;

    private ArenaCreateEditDto arenaCreateEditDto;

    private ArenaReadDto arenaReadDto;

    @BeforeEach
    void setUp() {
        arena = new Arena();
        arena.setId(ENTITY_ID);
        arenaCreateEditDto = ArenaCreateEditDto.builder().build();
        arenaReadDto = ArenaReadDto.builder().build();
    }

    @Test
    public void testFindAll() {
        when(arenaReadMapper.toDto(any(Arena.class))).thenReturn(arenaReadDto);

        when(arenaRepository.findAll()).thenReturn(List.of(arena));

        var result = arenaService.findAll();

        assertEquals(1, result.size());
        assertEquals(arenaReadDto, result.get(0));
        verify(arenaRepository, times(1)).findAll();
    }

    @Test
    public void testFindAllWithFilterAndPageable() {
        when(arenaReadMapper.toDto(any(Arena.class))).thenReturn(arenaReadDto);

        ArenaFilter arenaFilter = mock(ArenaFilter.class);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Arena> arenaPage = new PageImpl<>(List.of(arena), pageable, 1);

        when(arenaRepository.findAll(any(Predicate.class), any(Pageable.class))).thenReturn(arenaPage);

        Page<ArenaReadDto> result = arenaService.findAll(arenaFilter, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(arenaReadDto, result.getContent().get(0));

        verify(arenaRepository, times(1)).findAll(predicateCaptor.capture(), eq(pageable));

        verify(arenaReadMapper, times(1)).toDto(arena);
    }

    @Test
    public void testFindById() {
        when(arenaReadMapper.toDto(any(Arena.class))).thenReturn(arenaReadDto);
        when(arenaRepository.findById(ENTITY_ID)).thenReturn(Optional.of(arena));

        var result = arenaService.findById(ENTITY_ID);

        assertTrue(result.isPresent());
        assertEquals(arenaReadDto, result.get());
        verify(arenaRepository, times(1)).findById(ENTITY_ID);
    }

    @Test
    void createArena() {
        when(arenaCreateEditMapper.toEntity(any(ArenaCreateEditDto.class))).thenReturn(arena);

        arenaService.createArena(arenaCreateEditDto);

        verify(arenaRepository, times(1)).save(arena);
    }

    @Test
    void updateArena() {
        when(arenaCreateEditMapper.toEntity(any(ArenaCreateEditDto.class))).thenReturn(arena);

        arenaService.updateArena(ENTITY_ID, arenaCreateEditDto);

        verify(arenaRepository, times(1)).save(arena);
    }

    @Test
    void deleteArena() {
        when(arenaRepository.existsById(ENTITY_ID)).thenReturn(true);

        arenaService.deleteArena(ENTITY_ID);

        verify(arenaRepository, times(1)).deleteById(ENTITY_ID);
    }

    @Test
    void testDeleteArenaNotFound() {
        when(arenaRepository.existsById(ENTITY_ID)).thenReturn(false);

        assertThrows(DaoResourceNotFoundException.class, () -> arenaService.deleteArena(ENTITY_ID));
    }

    @Test
    void testCreateArenaDataAccessException() {
        when(arenaCreateEditMapper.toEntity(any(ArenaCreateEditDto.class))).thenReturn(arena);
        when(arenaRepository.save(any(Arena.class))).thenThrow(new DataAccessException("...") {});

        assertThrows(DaoCrudException.class, () -> arenaService.createArena(arenaCreateEditDto));
    }

    @Test
    void testUpdateArenaDataAccessException() {
        when(arenaCreateEditMapper.toEntity(any(ArenaCreateEditDto.class))).thenReturn(arena);
        when(arenaRepository.save(any(Arena.class))).thenThrow(new DataAccessException("...") {});

        assertThrows(DaoCrudException.class, () -> arenaService.updateArena(ENTITY_ID, arenaCreateEditDto));
    }

}