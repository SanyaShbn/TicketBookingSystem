package ticketbookingsystem.integration;

import ticketbookingsystem.test_config.TestJpaConfig;
import com.example.ticketbookingsystem.config.WebMvcConfig;
import com.example.ticketbookingsystem.dto.SectorCreateEditDto;
import com.example.ticketbookingsystem.entity.Arena;
import com.example.ticketbookingsystem.entity.Sector;
import com.example.ticketbookingsystem.repository.ArenaRepository;
import com.example.ticketbookingsystem.repository.SectorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringJUnitConfig
@ContextConfiguration(classes = {TestJpaConfig.class, WebMvcConfig.class})
@ActiveProfiles("test")
@WebAppConfiguration
public class SectorControllerIT {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private SectorRepository sectorRepository;

    @Autowired
    private ArenaRepository arenaRepository;

    private Arena savedArena;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        sectorRepository.deleteAll();
        arenaRepository.deleteAll();

        Arena arena = Arena.builder()
                .name("Test")
                .city("Test city")
                .capacity(1)
                .build();
        savedArena = arenaRepository.save(arena);
    }

    @Test
    public void testCreateSector() throws Exception {
        SectorCreateEditDto sectorCreateEditDto = buildSectorCreateEditDto();

        mockMvc.perform(post("/admin/sectors/create")
                        .param("arenaId", "1")
                        .flashAttr("sectorCreateEditDto", sectorCreateEditDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/sectors?arenaId=1"));

        assertEquals(1, sectorRepository.findAll().size());
    }

    @Test
    public void testUpdateSector() throws Exception {
        Sector sector = buildSector();
        sector.setArena(savedArena);
        Sector savedSector = sectorRepository.save(sector);

        SectorCreateEditDto sectorCreateEditDto = SectorCreateEditDto.builder()
                .sectorName("Updated Test Sector")
                .maxRowsNumb(100)
                .maxSeatsNumb(100)
                .build();

        mockMvc.perform(post("/admin/sectors/{id}/update", savedSector.getId())
                        .param("arenaId", savedArena.getId().toString())
                        .flashAttr("sectorCreateEditDto", sectorCreateEditDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/sectors?arenaId=" + savedArena.getId()));

        Sector updatedSector = sectorRepository.findById(savedSector.getId()).orElse(null);
        assertEquals("Updated Test Sector", updatedSector.getSectorName());
        assertEquals(100, updatedSector.getMaxRowsNumb());
    }

    @Test
    public void testDeleteSector() throws Exception {
        Sector sector = buildSector();
        sector.setArena(savedArena);
        Sector savedSector = sectorRepository.save(sector);

        mockMvc.perform(post("/admin/sectors/{id}/delete", savedSector.getId())
                        .param("arenaId", savedArena.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/sectors?arenaId=" + savedArena.getId()));

        assertTrue(sectorRepository.findById(savedSector.getId()).isEmpty());
    }

    private SectorCreateEditDto buildSectorCreateEditDto(){
        return SectorCreateEditDto.builder()
                .sectorName("Test Sector")
                .maxRowsNumb(10)
                .maxSeatsNumb(100)
                .build();
    }

    private Sector buildSector(){
        return Sector.builder()
                .sectorName("Test Sector")
                .maxRowsNumb(10)
                .maxSeatsNumb(100)
                .build();
    }
}
