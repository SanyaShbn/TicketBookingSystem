package ticketbookingsystem.unit.controller;

import com.example.ticketbookingsystem.controller.SectorController;
import com.example.ticketbookingsystem.dto.sector_dto.SectorCreateEditDto;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.service.SectorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class SectorControllerTest {

    private static final Long SECTOR_ID = 1L;
    private static final Long ARENA_ID = 1L;

    MockMvc mockMvc;

    @Mock
    private SectorService sectorService;

    @InjectMocks
    private SectorController sectorController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(sectorController).build();
    }

    @Test
    public void testFindAllSectors() throws Exception {
        mockMvc.perform(get("/admin/sectors")
                        .param("arenaId", ARENA_ID.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("sectors-jsp/sectors"))
                .andExpect(model().attributeExists("sectors"));
    }

    @Test
    public void testShowCreateSectorForm() throws Exception {
        mockMvc.perform(get("/admin/sectors/create")
                        .param("arenaId", ARENA_ID.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("sectors-jsp/create-sector"))
                .andExpect(model().attributeExists("arenaId"));
    }

    @Test
    public void testCreateSector() throws Exception {
        SectorCreateEditDto sectorCreateEditDto = buildSectorCreateEditDto();

        doNothing().when(sectorService).createSector(any(SectorCreateEditDto.class), anyLong());

        mockMvc.perform(post("/admin/sectors/create")
                        .param("arenaId", ARENA_ID.toString())
                        .flashAttr("sectorCreateEditDto", sectorCreateEditDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/sectors?arenaId=" + ARENA_ID));
    }

    @Test
    public void testCreateSectorWithException() throws Exception {
        SectorCreateEditDto sectorCreateEditDto = buildSectorCreateEditDto();

        doThrow(new DaoCrudException(new Throwable())).when(sectorService).createSector(any(SectorCreateEditDto.class), anyLong());

        mockMvc.perform(post("/admin/sectors/create")
                        .param("arenaId", ARENA_ID.toString())
                        .flashAttr("sectorCreateEditDto", sectorCreateEditDto))
                .andExpect(status().isOk())
                .andExpect(view().name("sectors-jsp/create-sector"))
                .andExpect(model().attributeExists("errors"));
    }

    @Test
    public void testUpdateSector() throws Exception {
        SectorCreateEditDto sectorCreateEditDto = buildSectorCreateEditDto();

        doNothing().when(sectorService).updateSector(anyLong(), any(SectorCreateEditDto.class), anyLong());

        mockMvc.perform(post("/admin/sectors/{id}/update", SECTOR_ID)
                        .param("arenaId", ARENA_ID.toString())
                        .flashAttr("sectorCreateEditDto", sectorCreateEditDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/sectors?arenaId=" + ARENA_ID));
    }

    @Test
    public void testUpdateSectorWithException() throws Exception {
        SectorCreateEditDto sectorCreateEditDto = buildSectorCreateEditDto();

        doThrow(new DaoCrudException(new Throwable())).when(sectorService).updateSector(anyLong(), any(SectorCreateEditDto.class), anyLong());

        mockMvc.perform(post("/admin/sectors/{id}/update", SECTOR_ID)
                        .param("arenaId", ARENA_ID.toString())
                        .flashAttr("sectorCreateEditDto", sectorCreateEditDto))
                .andExpect(status().isOk())
                .andExpect(view().name("sectors-jsp/update-sector"))
                .andExpect(model().attributeExists("errors"));
    }

    @Test
    public void testDeleteSector() throws Exception {
        doNothing().when(sectorService).deleteSector(anyLong());

        mockMvc.perform(post("/admin/sectors/{id}/delete", SECTOR_ID)
                        .param("arenaId", ARENA_ID.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/sectors?arenaId=" + ARENA_ID));
    }

    @Test
    public void testDeleteSectorWithException() throws Exception {
        doThrow(new DaoCrudException(new Throwable())).when(sectorService).deleteSector(anyLong());

        mockMvc.perform(post("/admin/sectors/{id}/delete", SECTOR_ID)
                        .param("arenaId", ARENA_ID.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("error-jsp/error-page"))
                .andExpect(model().attributeExists("errors"));
    }

    private SectorCreateEditDto buildSectorCreateEditDto(){
        return SectorCreateEditDto.builder()
                .sectorName("Test Sector")
                .maxRowsNumb(10)
                .maxSeatsNumb(100)
                .build();
    }
}