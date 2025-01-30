package ticketbookingsystem.integration;

import ticketbookingsystem.test_config.TestJpaConfig;
import com.example.ticketbookingsystem.config.WebMvcConfig;
import com.example.ticketbookingsystem.dto.arena_dto.ArenaCreateEditDto;
import com.example.ticketbookingsystem.entity.Arena;
import com.example.ticketbookingsystem.repository.ArenaRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringJUnitConfig
@ContextConfiguration(classes = {TestJpaConfig.class, WebMvcConfig.class})
@ActiveProfiles("test")
@WebAppConfiguration
public class ArenaControllerIT {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ArenaRepository arenaRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        arenaRepository.deleteAll();

        Arena arena = Arena.builder()
                .name("Test Arena№1")
                .city("Test city")
                .capacity(1)
                .build();
        arenaRepository.save(arena);
    }

    @Test
    public void testFindAllArenas() throws Exception {
        mockMvc.perform(get("/admin/arenas"))
                .andExpect(status().isOk())
                .andExpect(view().name("/arena-jsp/arenas"))
                .andExpect(model().attributeExists("arenas"))
                .andExpect(model().attributeExists("cities"))
                .andExpect(model().attribute("limit", 8));
    }

    @Test
    public void testCreateArena() throws Exception {
        ArenaCreateEditDto arenaCreateEditDto = buildArenaCreateEditDto();

        mockMvc.perform(post("/admin/arenas/create")
                        .flashAttr("arenaCreateEditDto", arenaCreateEditDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/arenas"));

        assertEquals(2, arenaRepository.findAll().size());
    }

    @Test
    public void testUpdateArena() throws Exception {
        Arena arena = arenaRepository.findAll().get(0);

        ArenaCreateEditDto arenaCreateEditDto = ArenaCreateEditDto.builder()
                .name("Updated Arena")
                .city("Updated City")
                .capacity(6000)
                .build();

        mockMvc.perform(post("/admin/arenas/{id}/update", arena.getId())
                        .flashAttr("arenaCreateEditDto", arenaCreateEditDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/arenas"));

        Arena updatedArena = arenaRepository.findById(arena.getId()).orElse(null);
        assertEquals("Updated Arena", updatedArena.getName());
        assertEquals("Updated City", updatedArena.getCity());
        assertEquals(6000, updatedArena.getCapacity());
    }

    @Test
    public void testDeleteArena() throws Exception {
        Arena arena = arenaRepository.findAll().get(0);

        mockMvc.perform(post("/admin/arenas/{id}/delete", arena.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/arenas"));

        assertTrue(arenaRepository.findById(arena.getId()).isEmpty());
    }

    private ArenaCreateEditDto buildArenaCreateEditDto() {
        return ArenaCreateEditDto.builder()
                .name("Test Arena№2")
                .city("Test City")
                .capacity(5000)
                .build();
    }
}