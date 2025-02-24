package ticketbookingsystem.unit.controller;

import com.example.ticketbookingsystem.controller.RowController;
import com.example.ticketbookingsystem.dto.PageResponse;
import com.example.ticketbookingsystem.dto.row_dto.RowCreateEditDto;
import com.example.ticketbookingsystem.dto.row_dto.RowFilter;
import com.example.ticketbookingsystem.dto.row_dto.RowReadDto;
import com.example.ticketbookingsystem.service.RowService;
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
public class RowControllerTest {

    private static final Long ROW_ID = 1L;

    @Mock
    private RowService rowService;

    @InjectMocks
    private RowController rowController;

    @Test
    public void testFindAllRows() {
        RowReadDto row1 = RowReadDto.builder().rowNumber(1).build();
        RowReadDto row2 = RowReadDto.builder().rowNumber(2).build();

        List<RowReadDto> rows = Arrays.asList(row1, row2);
        Page<RowReadDto> page = new PageImpl<>(rows);
        when(rowService.findAll(any(Long.class), any(RowFilter.class), any(Pageable.class))).thenReturn(page);

        PageResponse<RowReadDto> response = rowController.findAllRows(
                ROW_ID, new RowFilter("",""), Pageable.unpaged());

        assertEquals(2, response.getContent().size());
        verify(rowService, times(1)).findAll(
                any(Long.class), any(RowFilter.class), any(Pageable.class));
    }

    @Test
    void testGetRowById() {
        RowReadDto row = RowReadDto.builder().rowNumber(1).build();
        when(rowService.findById(ROW_ID)).thenReturn(Optional.of(row));

        ResponseEntity<RowReadDto> response = rowController.getRowById(ROW_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getRowNumber());
        verify(rowService, times(1)).findById(ROW_ID);
    }

    @Test
    public void testCreateRow() {
        RowCreateEditDto createEditDto = RowCreateEditDto.builder().rowNumber(1).build();

        RowReadDto readDto = RowReadDto.builder().rowNumber(1).build();
        when(rowService.createRow(any(RowCreateEditDto.class), any(Long.class))).thenReturn(readDto);

        ResponseEntity<RowReadDto> response = rowController.createRow(1L, createEditDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(1, response.getBody().getRowNumber());
        verify(rowService, times(1)).createRow(any(RowCreateEditDto.class), any(Long.class));
    }

    @Test
    public void testUpdateRow() {
        RowCreateEditDto createEditDto = RowCreateEditDto.builder().rowNumber(1).build();

        RowReadDto readDto = RowReadDto.builder().rowNumber(1).build();
        when(rowService.updateRow(eq(ROW_ID), any(RowCreateEditDto.class), any(Long.class))).thenReturn(readDto);

        ResponseEntity<RowReadDto> response = rowController.updateRow(ROW_ID, ROW_ID, createEditDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getRowNumber());
        verify(rowService, times(1)).updateRow(
                eq(ROW_ID), any(RowCreateEditDto.class), any(Long.class));
    }

    @Test
    public void testDeleteRow() {
        doNothing().when(rowService).deleteRow(ROW_ID);

        ResponseEntity<Map<String, String>> response = rowController.deleteRow(ROW_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Row deleted successfully", response.getBody().get("message"));
        verify(rowService, times(1)).deleteRow(ROW_ID);
    }

}