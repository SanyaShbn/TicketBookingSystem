package ticketbookingsystem.unit.controller;

import com.example.ticketbookingsystem.controller.ArenaController;
import com.example.ticketbookingsystem.dto.arena_dto.ArenaCreateEditDto;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.service.ArenaService;
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

public class ArenaControllerTest {

    private static final Long ARENA_ID = 1L;

    MockMvc mockMvc;

    @Mock
    private ArenaService arenaService;

    @InjectMocks
    private ArenaController arenaController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(arenaController).build();
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

        doNothing().when(arenaService).createArena(any(ArenaCreateEditDto.class));

        mockMvc.perform(post("/admin/arenas/create")
                        .flashAttr("arenaCreateEditDto", arenaCreateEditDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/arenas"));
    }

    @Test
    public void testCreateArenaWithException() throws Exception {
        ArenaCreateEditDto arenaCreateEditDto = buildArenaCreateEditDto();

        doThrow(new DaoCrudException(new Throwable())).when(arenaService).createArena(any(ArenaCreateEditDto.class));

        mockMvc.perform(post("/admin/arenas/create")
                        .flashAttr("arenaCreateEditDto", arenaCreateEditDto))
                .andExpect(status().isOk())
                .andExpect(view().name("/arena-jsp/create-arena"))
                .andExpect(model().attributeExists("errors"));
    }

    @Test
    public void testUpdateArena() throws Exception {
        ArenaCreateEditDto arenaCreateEditDto = buildArenaCreateEditDto();

        doNothing().when(arenaService).updateArena(anyLong(), any(ArenaCreateEditDto.class));

        mockMvc.perform(post("/admin/arenas/{id}/update", ARENA_ID)
                        .flashAttr("arenaCreateEditDto", arenaCreateEditDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/arenas"));
    }

    @Test
    public void testUpdateArenaWithException() throws Exception {
        ArenaCreateEditDto arenaCreateEditDto = buildArenaCreateEditDto();

        doThrow(new DaoCrudException(new Throwable())).when(arenaService).updateArena(anyLong(),
                any(ArenaCreateEditDto.class));

        mockMvc.perform(post("/admin/arenas/{id}/update", ARENA_ID)
                        .flashAttr("arenaCreateEditDto", arenaCreateEditDto))
                .andExpect(status().isOk())
                .andExpect(view().name("/arena-jsp/update-arena"))
                .andExpect(model().attributeExists("errors"));
    }

    @Test
    public void testDeleteArena() throws Exception {
        doNothing().when(arenaService).deleteArena(anyLong());

        mockMvc.perform(post("/admin/arenas/{id}/delete", ARENA_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/arenas"));
    }

    @Test
    public void testDeleteArenaWithException() throws Exception {
        doThrow(new DaoCrudException(new Throwable())).when(arenaService).deleteArena(anyLong());

        mockMvc.perform(post("/admin/arenas/{id}/delete", ARENA_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("/error-jsp/error-page"))
                .andExpect(model().attributeExists("errors"));
    }

    private ArenaCreateEditDto buildArenaCreateEditDto(){
        return ArenaCreateEditDto.builder()
                .name("Test")
                .city("Test city")
                .capacity(1)
                .build();
    }
}