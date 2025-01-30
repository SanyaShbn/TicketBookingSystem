package ticketbookingsystem.integration;

import ticketbookingsystem.test_config.TestJpaConfig;
import com.example.ticketbookingsystem.config.WebMvcConfig;
import com.example.ticketbookingsystem.dto.row_dto.RowCreateEditDto;
import com.example.ticketbookingsystem.entity.Arena;
import com.example.ticketbookingsystem.entity.Row;
import com.example.ticketbookingsystem.entity.Sector;
import com.example.ticketbookingsystem.repository.ArenaRepository;
import com.example.ticketbookingsystem.repository.RowRepository;
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
public class RowControllerIT {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private RowRepository rowRepository;

    @Autowired
    private SectorRepository sectorRepository;

    @Autowired
    private ArenaRepository arenaRepository;

    private MockMvc mockMvc;

    private Arena savedArena;

    private Sector savedSector;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        rowRepository.deleteAll();
        sectorRepository.deleteAll();
        arenaRepository.deleteAll();

        Arena arena = Arena.builder()
                .name("Test Arena")
                .city("Test City")
                .capacity(10000)
                .build();
        savedArena = arenaRepository.save(arena);

        Sector sector = Sector.builder()
                .sectorName("Test Sector")
                .maxRowsNumb(10)
                .maxSeatsNumb(100)
                .arena(savedArena)
                .build();
        savedSector = sectorRepository.save(sector);
    }

    @Test
    public void testCreateRow() throws Exception {
        RowCreateEditDto rowCreateEditDto = buildRowCreateEditDto();

        mockMvc.perform(post("/admin/rows/create")
                        .param("arenaId", savedArena.getId().toString())
                        .param("sectorId", savedSector.getId().toString())
                        .flashAttr("rowCreateEditDto", rowCreateEditDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/rows?arenaId=" + savedArena.getId() + "&sectorId=" + savedSector.getId()));

        assertEquals(1, rowRepository.findAll().size());
    }

    @Test
    public void testUpdateRow() throws Exception {
        Row row = buildRow();
        row.setSector(savedSector);
        Row savedRow = rowRepository.save(row);

        RowCreateEditDto rowCreateEditDto = RowCreateEditDto.builder()
                .rowNumber(2)
                .seatsNumb(50)
                .build();

        mockMvc.perform(post("/admin/rows/{id}/update", savedRow.getId())
                        .param("arenaId", savedArena.getId().toString())
                        .param("sectorId", savedSector.getId().toString())
                        .flashAttr("rowCreateEditDto", rowCreateEditDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/rows?arenaId=" + savedArena.getId() + "&sectorId=" + savedSector.getId()));

        Row updatedRow = rowRepository.findById(savedRow.getId()).orElse(null);
        assertEquals(2, updatedRow.getRowNumber());
        assertEquals(50, updatedRow.getSeatsNumb());
    }

    @Test
    public void testDeleteRow() throws Exception {
        Row row = buildRow();
        row.setSector(savedSector);
        Row savedRow = rowRepository.save(row);

        mockMvc.perform(post("/admin/rows/{id}/delete", savedRow.getId())
                        .param("arenaId", savedArena.getId().toString())
                        .param("sectorId", savedSector.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/rows?arenaId=" + savedArena.getId() + "&sectorId=" + savedSector.getId()));

        assertTrue(rowRepository.findById(savedRow.getId()).isEmpty());
    }

    private RowCreateEditDto buildRowCreateEditDto() {
        return RowCreateEditDto.builder()
                .rowNumber(1)
                .seatsNumb(5)
                .build();
    }

    private Row buildRow() {
        return Row.builder()
                .rowNumber(1)
                .seatsNumb(5)
                .build();
    }
}