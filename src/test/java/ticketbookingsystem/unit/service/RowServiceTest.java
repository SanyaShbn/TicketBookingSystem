package ticketbookingsystem.unit.service;

import com.example.ticketbookingsystem.dto.row_dto.RowCreateEditDto;
import com.example.ticketbookingsystem.dto.row_dto.RowFilter;
import com.example.ticketbookingsystem.dto.row_dto.RowReadDto;
import com.example.ticketbookingsystem.entity.Row;
import com.example.ticketbookingsystem.entity.Sector;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.exception.DaoResourceNotFoundException;
import com.example.ticketbookingsystem.mapper.row_mapper.RowCreateEditMapper;
import com.example.ticketbookingsystem.mapper.row_mapper.RowReadMapper;
import com.example.ticketbookingsystem.repository.RowRepository;
import com.example.ticketbookingsystem.service.RowService;
import com.example.ticketbookingsystem.service.SectorService;
import com.example.ticketbookingsystem.utils.SortUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RowServiceTest {

    private static final Long ROW_ID = 1L;

    private static final Long SECTOR_ID = 1L;

    private static final Integer ROW_NUMBER = 1;

    private static final Integer SEATS_NUMB = 5;

    @Mock
    private RowRepository rowRepository;

    @Mock
    private RowCreateEditMapper rowCreateEditMapper;

    @Mock
    private RowReadMapper rowReadMapper;

    @Mock
    private SectorService sectorService;

    @InjectMocks
    private RowService rowService;

    private Row row;

    private Sector sector;

    private RowCreateEditDto rowCreateEditDto;

    private RowReadDto rowReadDto;

    @BeforeEach
    void setUp() {
        row = new Row();
        row.setId(ROW_ID);
        sector = new Sector();
        sector.setId(SECTOR_ID);
        rowCreateEditDto = RowCreateEditDto.builder()
                .rowNumber(ROW_NUMBER)
                .seatsNumb(SEATS_NUMB)
                .build();
        rowReadDto = RowReadDto.builder().build();
    }

    @Test
    public void testFindAll() {
        when(rowReadMapper.toDto(any(Row.class))).thenReturn(rowReadDto);
        when(rowRepository.findAll()).thenReturn(List.of(row));

        var result = rowService.findAll();

        assertEquals(1, result.size());
        assertEquals(rowReadDto, result.get(0));
        verify(rowRepository, times(1)).findAll();
    }

    @Test
    public void testFindAllRows() {
        when(rowReadMapper.toDto(any(Row.class))).thenReturn(rowReadDto);

        RowFilter rowFilter = mock(RowFilter.class);
        Pageable pageable = PageRequest.of(0, 10);

        when(rowFilter.rowNumberOrder()).thenReturn("asc");
        when(rowFilter.seatsNumbOrder()).thenReturn("desc");

        Map<String, String> sortOrders = new LinkedHashMap<>();
        sortOrders.put("rowNumber", "asc");
        sortOrders.put("seatsNumb", "desc");

        Sort sort = SortUtils.buildSort(sortOrders);
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<Row> rowPage = new PageImpl<>(List.of(row), sortedPageable, 1);

        when(rowRepository.findAllBySectorId(eq(ROW_ID), eq(sortedPageable))).thenReturn(rowPage);

        Page<RowReadDto> result = rowService.findAll(ROW_ID, rowFilter, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(rowReadDto, result.getContent().get(0));

        verify(rowRepository, times(1)).findAllBySectorId(ROW_ID, sortedPageable);
        verify(rowReadMapper, times(1)).toDto(row);
    }

    @Test
    public void testFindById() {
        when(rowReadMapper.toDto(any(Row.class))).thenReturn(rowReadDto);
        when(rowRepository.findById(ROW_ID)).thenReturn(Optional.of(row));

        var result = rowService.findById(ROW_ID);

        assertTrue(result.isPresent());
        assertEquals(rowReadDto, result.get());
        verify(rowRepository, times(1)).findById(ROW_ID);
    }

    @Test
    void createRow() {
        when(rowCreateEditMapper.toEntity(any(RowCreateEditDto.class))).thenReturn(row);
        rowService.createRow(rowCreateEditDto, sector.getId());

        verify(rowRepository, times(1)).save(row);
        verify(rowRepository, times(1))
                .updateSectorAfterRowSave(sector.getId(), row.getSeatsNumb());
    }

    @Test
    void updateRow() {
        when(rowRepository.findById(any(Long.class))).thenReturn(Optional.of(row));

        rowService.updateRow(ROW_ID, rowCreateEditDto, sector.getId());

        verify(rowRepository, times(1)).save(row);
        verify(rowRepository, times(1)).updateSectorBeforeRowUpdate(sector.getId(),
                row.getSeatsNumb(), rowCreateEditDto.getSeatsNumb());
    }

    @Test
    void deleteRow() {
        Row rowWithID = new Row();
        rowWithID.setId(ROW_ID);
        Sector sectorWithID = new Sector();
        sectorWithID.setId(SECTOR_ID);
        rowWithID.setSector(sectorWithID);

        when(rowRepository.findById(ROW_ID)).thenReturn(Optional.of(rowWithID));

        rowService.deleteRow(ROW_ID);

        verify(rowRepository, times(1)).delete(rowWithID);
        verify(rowRepository, times(1)).updateSectorAfterRowDelete(sectorWithID.getId(),
                rowWithID.getSeatsNumb());
    }

    @Test
    void testDeleteRowNotFound() {
        when(rowRepository.findById(ROW_ID)).thenReturn(Optional.empty());

        assertThrows(DaoResourceNotFoundException.class, () -> rowService.deleteRow(ROW_ID));
    }

    @Test
    void testCreateRowDataAccessException() {
        when(rowCreateEditMapper.toEntity(any(RowCreateEditDto.class))).thenReturn(row);
        when(rowRepository.save(any(Row.class))).thenThrow(new DataAccessException("...") {});

        assertThrows(DaoCrudException.class,
                () -> rowService.createRow(rowCreateEditDto, row.getId()));
    }

    @Test
    void testUpdateRowDataAccessException() {
        when(rowRepository.findById(ROW_ID)).thenReturn(Optional.of(row));

        when(rowRepository.save(any(Row.class))).thenThrow(new DataAccessException("...") {});

        assertThrows(DaoCrudException.class,
                () -> rowService.updateRow(ROW_ID, rowCreateEditDto, row.getId()));
    }

}