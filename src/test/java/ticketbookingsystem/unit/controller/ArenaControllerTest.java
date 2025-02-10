package ticketbookingsystem.unit.controller;

import com.example.ticketbookingsystem.controller.ArenaController;
import com.example.ticketbookingsystem.dto.PageResponse;
import com.example.ticketbookingsystem.dto.arena_dto.ArenaCreateEditDto;
import com.example.ticketbookingsystem.dto.arena_dto.ArenaFilter;
import com.example.ticketbookingsystem.dto.arena_dto.ArenaReadDto;
import com.example.ticketbookingsystem.service.ArenaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ArenaControllerTest {

    private static final Long ARENA_ID = 1L;

    @Mock
    private ArenaService arenaService;

    @InjectMocks
    private ArenaController arenaController;

    @Test
    void testFindAll() {
        ArenaReadDto arena1 = ArenaReadDto.builder().name("Arena1").build();
        ArenaReadDto arena2 = ArenaReadDto.builder().name("Arena2").build();

        List<ArenaReadDto> arenas = Arrays.asList(arena1, arena2);
        Page<ArenaReadDto> page = new PageImpl<>(arenas);
        when(arenaService.findAll(any(ArenaFilter.class), any(Pageable.class))).thenReturn(page);

        PageResponse<ArenaReadDto> response = arenaController.findAll(
                new ArenaFilter("", "",""), Pageable.unpaged());

        assertEquals(2, response.getContent().size());
        verify(arenaService, times(1)).findAll(any(ArenaFilter.class), any(Pageable.class));
    }

    @Test
    void testGetArenaById() {
        ArenaReadDto arena = ArenaReadDto.builder().name("Arena1").build();
        when(arenaService.findById(ARENA_ID)).thenReturn(Optional.of(arena));

        ResponseEntity<ArenaReadDto> response = arenaController.getArenaById(ARENA_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Arena1", response.getBody().getName());
        verify(arenaService, times(1)).findById(ARENA_ID);
    }

    @Test
    void testCreateArena() {
        ArenaCreateEditDto createEditDto = ArenaCreateEditDto.builder().name("New Arena").build();

        ArenaReadDto readDto = ArenaReadDto.builder().name("New Arena").build();
        when(arenaService.createArena(any(ArenaCreateEditDto.class))).thenReturn(readDto);

        ResponseEntity<ArenaReadDto> response = arenaController.createArena(createEditDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("New Arena", response.getBody().getName());
        verify(arenaService, times(1)).createArena(any(ArenaCreateEditDto.class));
    }

    @Test
    void testUpdateArena() {
        ArenaCreateEditDto createEditDto = ArenaCreateEditDto.builder().name("Updated Arena").build();

        ArenaReadDto readDto = ArenaReadDto.builder().name("Updated Arena").build();
        when(arenaService.updateArena(eq(ARENA_ID), any(ArenaCreateEditDto.class))).thenReturn(readDto);

        ResponseEntity<ArenaReadDto> response = arenaController.updateArena(ARENA_ID, createEditDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Updated Arena", response.getBody().getName());
        verify(arenaService, times(1)).updateArena(eq(ARENA_ID), any(ArenaCreateEditDto.class));
    }

    @Test
    void testDeleteArena() {
        doNothing().when(arenaService).deleteArena(ARENA_ID);

        ResponseEntity<String> response = arenaController.deleteArena(ARENA_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Arena deleted successfully", response.getBody());
        verify(arenaService, times(1)).deleteArena(ARENA_ID);
    }

}