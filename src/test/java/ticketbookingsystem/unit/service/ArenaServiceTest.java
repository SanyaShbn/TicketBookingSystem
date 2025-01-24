package ticketbookingsystem.unit.service;

import com.example.ticketbookingsystem.dto.ArenaCreateEditDto;
import com.example.ticketbookingsystem.dto.ArenaReadDto;
import com.example.ticketbookingsystem.entity.Arena;
import com.example.ticketbookingsystem.mapper.ArenaCreateEditMapper;
import com.example.ticketbookingsystem.mapper.ArenaReadMapper;
import com.example.ticketbookingsystem.repository.ArenaRepository;
import com.example.ticketbookingsystem.service.ArenaService;
import com.example.ticketbookingsystem.validator.CreateOrUpdateArenaValidator;
import com.example.ticketbookingsystem.validator.ValidationResult;
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
        arena.setId(1L);
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
        when(arenaRepository.findById(1L)).thenReturn(Optional.of(arena));

        var result = arenaService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(arenaReadDto, result.get());
        verify(arenaRepository, times(1)).findById(1L);
    }

    @Test
    void createArena() {
        arenaService.createArena(arenaCreateEditDto);

        verify(arenaRepository, times(1)).save(arena);
    }

    @Test
    void updateArena() {
        arenaService.updateArena(1L, arenaCreateEditDto);

        verify(arenaRepository, times(1)).save(arena);
    }

    @Test
    void deleteArena() {
        arenaService.deleteArena(1L);

        verify(arenaRepository, times(1)).deleteById(1L);
    }

}