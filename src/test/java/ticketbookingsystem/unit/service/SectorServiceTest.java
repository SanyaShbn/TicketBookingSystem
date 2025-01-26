package ticketbookingsystem.unit.service;

import com.example.ticketbookingsystem.dto.SectorCreateEditDto;
import com.example.ticketbookingsystem.dto.SectorReadDto;
import com.example.ticketbookingsystem.entity.Arena;
import com.example.ticketbookingsystem.entity.Sector;
import com.example.ticketbookingsystem.mapper.SectorCreateEditMapper;
import com.example.ticketbookingsystem.mapper.SectorReadMapper;
import com.example.ticketbookingsystem.repository.SectorRepository;
import com.example.ticketbookingsystem.service.ArenaService;
import com.example.ticketbookingsystem.service.SectorService;
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

public class SectorServiceTest {

    private static final Long ENTITY_ID = 1L;

    @Mock
    private SectorRepository sectorRepository;

    @Mock
    private SectorCreateEditMapper sectorCreateEditMapper;

    @Mock
    private SectorReadMapper sectorReadMapper;

    @Mock
    private ArenaService arenaService;

    @InjectMocks
    private SectorService sectorService;

    private Sector sector;

    private Arena arena;

    private SectorCreateEditDto sectorCreateEditDto;

    private SectorReadDto sectorReadDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sector = new Sector();
        sector.setId(ENTITY_ID);
        arena = new Arena();
        arena.setId(ENTITY_ID);
        sectorCreateEditDto = SectorCreateEditDto.builder().build();
        sectorReadDto = SectorReadDto.builder().build();

        when(sectorCreateEditMapper.toEntity(any(SectorCreateEditDto.class))).thenReturn(sector);
        when(sectorReadMapper.toDto(any(Sector.class))).thenReturn(sectorReadDto);
        when(arenaService.findArenaById(any(Long.class))).thenReturn(arena);
    }

    @Test
    public void testFindAll() {
        when(sectorRepository.findAll()).thenReturn(List.of(sector));

        var result = sectorService.findAll();

        assertEquals(1, result.size());
        assertEquals(sectorReadDto, result.get(0));
        verify(sectorRepository, times(1)).findAll();
    }

    @Test
    public void testFindById() {
        when(sectorRepository.findById(ENTITY_ID)).thenReturn(Optional.of(sector));

        var result = sectorService.findById(ENTITY_ID);

        assertTrue(result.isPresent());
        assertEquals(sectorReadDto, result.get());
        verify(sectorRepository, times(1)).findById(ENTITY_ID);
    }

    @Test
    void createSector() {
        sectorService.createSector(sectorCreateEditDto, arena.getId());

        verify(sectorRepository, times(1)).save(sector);
        verify(sectorRepository, times(1))
                .updateArenaAfterSectorSave(sector.getArena().getId(), sector.getMaxSeatsNumb());
    }

    @Test
    void updateSector() {
        when(sectorRepository.findById(any(Long.class))).thenReturn(Optional.of(sector));

        sectorService.updateSector(ENTITY_ID, sectorCreateEditDto, arena.getId());

        verify(sectorRepository, times(1)).save(sector);
        verify(sectorRepository, times(1)).updateArenaBeforeSectorUpdate(sector.getArena().getId(),
                sector.getMaxSeatsNumb(), sector.getMaxSeatsNumb());
    }

    @Test
    void deleteSector() {
        Sector sectorWithID = new Sector();
        sectorWithID.setId(ENTITY_ID);
        Arena arenaWithID = new Arena();
        arenaWithID.setId(ENTITY_ID);
        sectorWithID.setArena(arenaWithID);

        when(sectorRepository.findById(ENTITY_ID)).thenReturn(Optional.of(sectorWithID));

        sectorService.deleteSector(ENTITY_ID);

        verify(sectorRepository, times(1)).delete(sectorWithID);
        verify(sectorRepository, times(1)).updateArenaAfterSectorDelete(arenaWithID.getId(),
                sectorWithID.getMaxSeatsNumb());
    }

}