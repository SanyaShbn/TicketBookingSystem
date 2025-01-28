package ticketbookingsystem.integration;

import com.example.ticketbookingsystem.config.WebMvcConfig;
import com.example.ticketbookingsystem.dto.*;
import com.example.ticketbookingsystem.entity.*;
import com.example.ticketbookingsystem.repository.*;
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
import ticketbookingsystem.test_config.TestJpaConfig;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringJUnitConfig
@ContextConfiguration(classes = {TestJpaConfig.class, WebMvcConfig.class})
@ActiveProfiles("test")
@WebAppConfiguration
public class SportEventControllerIT {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private SportEventRepository sportEventRepository;

    @Autowired
    private ArenaRepository arenaRepository;

    private MockMvc mockMvc;

    private Arena savedArena;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        sportEventRepository.deleteAll();
        arenaRepository.deleteAll();

        Arena arena = Arena.builder()
                .name("Test")
                .city("Test city")
                .capacity(1)
                .build();
        savedArena = arenaRepository.save(arena);
    }

    @Test
    public void testCreateSportEvent() throws Exception {
        SportEventCreateEditDto sportEventCreateEditDto = buildSportEventCreateEditDto();

        mockMvc.perform(post("/admin/sport_events/create")
                        .param("arenaId", savedArena.getId().toString())
                        .flashAttr("sportEventCreateEditDto", sportEventCreateEditDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/sport_events"));

        assertEquals(1, sportEventRepository.findAll().size());
    }

    @Test
    public void testUpdateSportEvent() throws Exception {
        SportEvent sportEvent = buildSportEvent();
        sportEvent.setArena(savedArena);
        SportEvent savedSportEvent = sportEventRepository.save(sportEvent);

        SportEventCreateEditDto sportEventCreateEditDto = SportEventCreateEditDto.builder()
                .eventName("Updated Test Event")
                .eventDateTime(LocalDateTime.now())
                .build();

        mockMvc.perform(post("/admin/sport_events/{id}/update", savedSportEvent.getId())
                        .param("arenaId", savedArena.getId().toString())
                        .flashAttr("sportEventCreateEditDto", sportEventCreateEditDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/sport_events"));

        SportEvent updatedSportEvent = sportEventRepository.findById(savedSportEvent.getId()).orElse(null);
        assertEquals("Updated Test Event", updatedSportEvent.getEventName());
    }

    @Test
    public void testDeleteSportEvent() throws Exception {
        SportEvent sportEvent = buildSportEvent();
        sportEvent.setArena(savedArena);
        SportEvent savedSportEvent = sportEventRepository.save(sportEvent);

        mockMvc.perform(post("/admin/sport_events/{id}/delete", savedSportEvent.getId())
                        .param("arenaId", savedArena.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/sport_events"));

        assertTrue(sportEventRepository.findById(savedSportEvent.getId()).isEmpty());
    }

    private SportEventCreateEditDto buildSportEventCreateEditDto(){
        return SportEventCreateEditDto.builder()
                .eventName("Test Event")
                .eventDateTime(LocalDateTime.now())
                .build();
    }

    private SportEvent buildSportEvent(){
        return SportEvent.builder()
                .eventName("Test Event")
                .eventDateTime(LocalDateTime.now())
                .build();
    }
}
