package ticketbookingsystem.unit.controller;

import com.example.ticketbookingsystem.controller.SportEventController;
import com.example.ticketbookingsystem.dto.arena_dto.ArenaReadDto;
import com.example.ticketbookingsystem.dto.sport_event_dto.SportEventCreateEditDto;
import com.example.ticketbookingsystem.dto.sport_event_dto.SportEventReadDto;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.service.ArenaService;
import com.example.ticketbookingsystem.service.SportEventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class SportEventControllerTest {

    private static final Long SPORT_EVENT_ID = 1L;
    private static final Long ARENA_ID = 1L;

    MockMvc mockMvc;

    @Mock
    private SportEventService sportEventService;

    @Mock
    private ArenaService arenaService;

    @InjectMocks
    private SportEventController sportEventController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(sportEventController).build();
    }

    @Test
    public void testFindAllSportEvents() throws Exception {
        mockMvc.perform(get("/admin/sport_events"))
                .andExpect(status().isOk())
                .andExpect(view().name("sport-events-jsp/sport_events"))
                .andExpect(model().attributeExists("sport_events"));
    }

    @Test
    public void testShowCreateSportEventForm() throws Exception {
        when(arenaService.findAll()).thenReturn(List.of(ArenaReadDto.builder().build()));

        mockMvc.perform(get("/admin/sport_events/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("sport-events-jsp/create-sport-event"))
                .andExpect(model().attributeExists("arenas"));
    }

    @Test
    public void testCreateSportEvent() throws Exception {
        SportEventCreateEditDto sportEventCreateEditDto = buildSportEventCreateEditDto();

        doNothing().when(sportEventService).createSportEvent(any(SportEventCreateEditDto.class), anyLong());

        mockMvc.perform(post("/admin/sport_events/create")
                        .param("arenaId", ARENA_ID.toString())
                        .flashAttr("sportEventCreateEditDto", sportEventCreateEditDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/sport_events"));
    }

    @Test
    public void testCreateSportEventWithException() throws Exception {
        SportEventCreateEditDto sportEventCreateEditDto = buildSportEventCreateEditDto();

        doThrow(new DaoCrudException(new Throwable())).when(sportEventService).createSportEvent(any(SportEventCreateEditDto.class), anyLong());

        mockMvc.perform(post("/admin/sport_events/create")
                        .param("arenaId", ARENA_ID.toString())
                        .flashAttr("sportEventCreateEditDto", sportEventCreateEditDto))
                .andExpect(status().isOk())
                .andExpect(view().name("sport-events-jsp/create-sport-event"))
                .andExpect(model().attributeExists("errors"));
    }

    @Test
    public void testShowUpdateSportEventForm() throws Exception {
        when(sportEventService.findById(SPORT_EVENT_ID)).thenReturn(Optional.of(SportEventReadDto.builder().build()));
        when(arenaService.findAll()).thenReturn(List.of(ArenaReadDto.builder().build()));

        mockMvc.perform(get("/admin/sport_events/{id}/update", SPORT_EVENT_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("sport-events-jsp/update-sport-event"))
                .andExpect(model().attributeExists("sport_event"))
                .andExpect(model().attributeExists("arenas"));
    }

    @Test
    public void testUpdateSportEvent() throws Exception {
        SportEventCreateEditDto sportEventCreateEditDto = buildSportEventCreateEditDto();

        doNothing().when(sportEventService).updateSportEvent(anyLong(), any(SportEventCreateEditDto.class), anyLong());

        mockMvc.perform(post("/admin/sport_events/{id}/update", SPORT_EVENT_ID)
                        .param("arenaId", ARENA_ID.toString())
                        .flashAttr("sportEventCreateEditDto", sportEventCreateEditDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/sport_events"));
    }

    @Test
    public void testUpdateSportEventWithException() throws Exception {
        SportEventCreateEditDto sportEventCreateEditDto = buildSportEventCreateEditDto();

        doThrow(new DaoCrudException(new Throwable())).when(sportEventService).updateSportEvent(anyLong(), any(SportEventCreateEditDto.class), anyLong());

        mockMvc.perform(post("/admin/sport_events/{id}/update", SPORT_EVENT_ID)
                        .param("arenaId", ARENA_ID.toString())
                        .flashAttr("sportEventCreateEditDto", sportEventCreateEditDto))
                .andExpect(status().isOk())
                .andExpect(view().name("sport-events-jsp/update-sport-event"))
                .andExpect(model().attributeExists("errors"));
    }

    @Test
    public void testDeleteSportEvent() throws Exception {
        doNothing().when(sportEventService).deleteSportEvent(anyLong());

        mockMvc.perform(post("/admin/sport_events/{id}/delete", SPORT_EVENT_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/sport_events"));
    }

    @Test
    public void testDeleteSportEventWithException() throws Exception {
        doThrow(new DaoCrudException(new Throwable())).when(sportEventService).deleteSportEvent(anyLong());

        mockMvc.perform(post("/admin/sport_events/{id}/delete", SPORT_EVENT_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("error-jsp/error-page"))
                .andExpect(model().attributeExists("errors"));
    }

    private SportEventCreateEditDto buildSportEventCreateEditDto(){
        return SportEventCreateEditDto.builder()
                .eventName("Test Event")
                .eventDateTime(LocalDateTime.now())
                .build();
    }

}