package ticketbookingsystem.unit.controller;

import com.example.ticketbookingsystem.controller.SectorController;
import com.example.ticketbookingsystem.dto.PageResponse;
import com.example.ticketbookingsystem.dto.sector_dto.SectorCreateEditDto;
import com.example.ticketbookingsystem.dto.sector_dto.SectorFilter;
import com.example.ticketbookingsystem.dto.sector_dto.SectorReadDto;
import com.example.ticketbookingsystem.service.SectorService;
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
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SectorControllerTest {

    private static final Long SECTOR_ID = 1L;

    @Mock
    private SectorService sectorService;

    @InjectMocks
    private SectorController sectorController;

    @Test
    public void testFindAllSectors(){
        SectorReadDto sector1 = SectorReadDto.builder().sectorName("Sector1").build();
        SectorReadDto sector2 = SectorReadDto.builder().sectorName("Sector2").build();

        List<SectorReadDto> sectors = Arrays.asList(sector1, sector2);
        Page<SectorReadDto> page = new PageImpl<>(sectors);
        when(sectorService.findAll(any(Long.class), any(SectorFilter.class), any(Pageable.class))).thenReturn(page);

        PageResponse<SectorReadDto> response = sectorController.findAllSectors(
                SECTOR_ID,
                new SectorFilter("", "", ""),
                Pageable.unpaged());

        assertEquals(2, response.getContent().size());
        verify(sectorService, times(1)).findAll(
                any(Long.class), any(SectorFilter.class), any(Pageable.class));
    }

    @Test
    void testGetSectorById() {
        SectorReadDto sector = SectorReadDto.builder().sectorName("Sector1").build();
        when(sectorService.findById(SECTOR_ID)).thenReturn(Optional.of(sector));

        ResponseEntity<SectorReadDto> response = sectorController.getSectorById(SECTOR_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Sector1", response.getBody().getSectorName());
        verify(sectorService, times(1)).findById(SECTOR_ID);
    }

    @Test
    public void testCreateSector() {
        SectorCreateEditDto createEditDto = SectorCreateEditDto.builder().sectorName("New Sector").build();

        SectorReadDto readDto = SectorReadDto.builder().sectorName("New Sector").build();
        when(sectorService.createSector(any(SectorCreateEditDto.class), any(Long.class))).thenReturn(readDto);

        ResponseEntity<SectorReadDto> response = sectorController.createSector(SECTOR_ID, createEditDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("New Sector", response.getBody().getSectorName());
        verify(sectorService, times(1)).createSector(
                any(SectorCreateEditDto.class), any(Long.class));
    }

    @Test
    public void testUpdateSector() {
        SectorCreateEditDto createEditDto = SectorCreateEditDto.builder().sectorName("Updated Sector").build();

        SectorReadDto readDto = SectorReadDto.builder().sectorName("Updated Sector").build();
        when(sectorService.updateSector(eq(SECTOR_ID),
                any(SectorCreateEditDto.class), any(Long.class))).thenReturn(readDto);

        ResponseEntity<SectorReadDto> response = sectorController.updateSector(SECTOR_ID, SECTOR_ID, createEditDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Updated Sector", response.getBody().getSectorName());
        verify(sectorService, times(1)).updateSector(eq(SECTOR_ID),
                any(SectorCreateEditDto.class), any(Long.class));
    }

    @Test
    public void testDeleteSector() {
        doNothing().when(sectorService).deleteSector(SECTOR_ID);

        ResponseEntity<Map<String, String>> response = sectorController.deleteSector(SECTOR_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Sector deleted successfully", response.getBody().get("message"));
        verify(sectorService, times(1)).deleteSector(SECTOR_ID);
    }
}