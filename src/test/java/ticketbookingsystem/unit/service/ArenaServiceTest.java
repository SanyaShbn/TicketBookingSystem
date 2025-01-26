package ticketbookingsystem.unit.service;

import com.example.ticketbookingsystem.dto.ArenaCreateEditDto;
import com.example.ticketbookingsystem.dto.ArenaReadDto;
import com.example.ticketbookingsystem.entity.Arena;
import com.example.ticketbookingsystem.mapper.ArenaCreateEditMapper;
import com.example.ticketbookingsystem.mapper.ArenaReadMapper;
import com.example.ticketbookingsystem.repository.ArenaRepository;
import com.example.ticketbookingsystem.service.ArenaService;
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

public class ArenaServiceTest {

    private static final Long ENTITY_ID = 1L;

    @Mock
    private ArenaRepository arenaRepository;

    @Mock
    private ArenaCreateEditMapper arenaCreateEditMapper;

    @Mock
    private ArenaReadMapper arenaReadMapper;

    @InjectMocks
    private ArenaService arenaService;

    private Arena arena;

    private ArenaCreateEditDto arenaCreateEditDto;

    private ArenaReadDto arenaReadDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        arena = new Arena();
        arena.setId(ENTITY_ID);
        arenaCreateEditDto = ArenaCreateEditDto.builder().build();
        arenaReadDto = ArenaReadDto.builder().build();

        when(arenaCreateEditMapper.toEntity(any(ArenaCreateEditDto.class))).thenReturn(arena);
        when(arenaReadMapper.toDto(any(Arena.class))).thenReturn(arenaReadDto);
    }

    @Test
    public void testFindAll() {
        when(arenaRepository.findAll()).thenReturn(List.of(arena));

        var result = arenaService.findAll();

        assertEquals(1, result.size());
        assertEquals(arenaReadDto, result.get(0));
        verify(arenaRepository, times(1)).findAll();
    }

    @Test
    public void testFindById() {
        when(arenaRepository.findById(ENTITY_ID)).thenReturn(Optional.of(arena));

        var result = arenaService.findById(ENTITY_ID);

        assertTrue(result.isPresent());
        assertEquals(arenaReadDto, result.get());
        verify(arenaRepository, times(1)).findById(ENTITY_ID);
    }

    @Test
    void createArena() {
        arenaService.createArena(arenaCreateEditDto);

        verify(arenaRepository, times(1)).save(arena);
    }

    @Test
    void updateArena() {
        arenaService.updateArena(ENTITY_ID, arenaCreateEditDto);

        verify(arenaRepository, times(1)).save(arena);
    }

    @Test
    void deleteArena() {
        arenaService.deleteArena(ENTITY_ID);

        verify(arenaRepository, times(1)).deleteById(ENTITY_ID);
    }

}