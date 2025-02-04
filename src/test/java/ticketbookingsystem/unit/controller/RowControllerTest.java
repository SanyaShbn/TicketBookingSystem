package ticketbookingsystem.unit.controller;

import com.example.ticketbookingsystem.controller.RowController;
import com.example.ticketbookingsystem.dto.row_dto.RowCreateEditDto;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.service.RowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class RowControllerTest {

    private static final Long ROW_ID = 1L;
    private static final Long SECTOR_ID = 1L;
    private static final Long ARENA_ID = 1L;

    MockMvc mockMvc;

    @Mock
    private RowService rowService;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private RowController rowController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(rowController).build();
    }

    @Test
    public void testShowCreateRowForm() throws Exception {
        mockMvc.perform(get("/admin/rows/create")
                        .param("arenaId", ARENA_ID.toString())
                        .param("sectorId", SECTOR_ID.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("rows-jsp/create-row"))
                .andExpect(model().attributeExists("arenaId"))
                .andExpect(model().attributeExists("sectorId"));
    }

    @Test
    public void testCreateRow() throws Exception {
        RowCreateEditDto rowCreateEditDto = buildRowCreateEditDto();

        doNothing().when(rowService).createRow(any(RowCreateEditDto.class), anyLong());

        mockMvc.perform(post("/admin/rows/create")
                        .param("arenaId", ARENA_ID.toString())
                        .param("sectorId", SECTOR_ID.toString())
                        .flashAttr("rowCreateEditDto", rowCreateEditDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/rows?arenaId=" + ARENA_ID + "&sectorId=" + SECTOR_ID));
    }

    @Test
    public void testUpdateRow() throws Exception {
        RowCreateEditDto rowCreateEditDto = buildRowCreateEditDto();

        doNothing().when(rowService).updateRow(anyLong(), any(RowCreateEditDto.class), anyLong());

        mockMvc.perform(post("/admin/rows/{id}/update", ROW_ID)
                        .param("arenaId", ARENA_ID.toString())
                        .param("sectorId", SECTOR_ID.toString())
                        .flashAttr("rowCreateEditDto", rowCreateEditDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/rows?arenaId=" + ARENA_ID + "&sectorId=" + SECTOR_ID));
    }

    @Test
    public void testDeleteRow() throws Exception {
        doNothing().when(rowService).deleteRow(anyLong());

        mockMvc.perform(post("/admin/rows/{id}/delete", ROW_ID)
                        .param("arenaId", ARENA_ID.toString())
                        .param("sectorId", SECTOR_ID.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/rows?arenaId=" + ARENA_ID + "&sectorId=" + SECTOR_ID));
    }

    @Test
    public void testDeleteRowWithException() throws Exception {
        doThrow(new DaoCrudException(new Throwable())).when(rowService).deleteRow(anyLong());

        mockMvc.perform(post("/admin/rows/{id}/delete", ROW_ID)
                        .param("arenaId", ARENA_ID.toString())
                        .param("sectorId", SECTOR_ID.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("error-jsp/error-page"))
                .andExpect(model().attributeExists("errors"));
    }

    private RowCreateEditDto buildRowCreateEditDto(){
        return RowCreateEditDto.builder()
                .rowNumber(1)
                .seatsNumb(5)
                .build();
    }
}