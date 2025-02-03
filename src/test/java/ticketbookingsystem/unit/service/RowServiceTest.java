package ticketbookingsystem.unit.service;

import com.example.ticketbookingsystem.dto.row_dto.RowCreateEditDto;
import com.example.ticketbookingsystem.dto.row_dto.RowFilter;
import com.example.ticketbookingsystem.dto.row_dto.RowReadDto;
import com.example.ticketbookingsystem.entity.Row;
import com.example.ticketbookingsystem.entity.Sector;
import com.example.ticketbookingsystem.mapper.row_mapper.RowCreateEditMapper;
import com.example.ticketbookingsystem.mapper.row_mapper.RowReadMapper;
import com.example.ticketbookingsystem.repository.RowRepository;
import com.example.ticketbookingsystem.service.RowService;
import com.example.ticketbookingsystem.service.SectorService;
import com.example.ticketbookingsystem.utils.SortUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RowServiceTest {

    private static final Long ROW_ID = 1L;
    private static final Long SECTOR_ID = 1L;

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
        MockitoAnnotations.openMocks(this);
        row = new Row();
        row.setId(ROW_ID);
        sector = new Sector();
        sector.setId(SECTOR_ID);
        rowCreateEditDto = RowCreateEditDto.builder().build();
        rowReadDto = RowReadDto.builder().build();

        when(rowCreateEditMapper.toEntity(any(RowCreateEditDto.class))).thenReturn(row);
        when(rowReadMapper.toDto(any(Row.class))).thenReturn(rowReadDto);
        when(sectorService.findSectorById(any(Long.class))).thenReturn(sector);
    }

    @Test
    public void testFindAll() {
        when(rowRepository.findAll()).thenReturn(List.of(row));

        var result = rowService.findAll();

        assertEquals(1, result.size());
        assertEquals(rowReadDto, result.get(0));
        verify(rowRepository, times(1)).findAll();
    }

    @Test
    public void testFindAllRows() {
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
        when(rowRepository.findById(ROW_ID)).thenReturn(Optional.of(row));

        var result = rowService.findById(ROW_ID);

        assertTrue(result.isPresent());
        assertEquals(rowReadDto, result.get());
        verify(rowRepository, times(1)).findById(ROW_ID);
    }

    @Test
    void createRow() {
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
                row.getSeatsNumb(), row.getSeatsNumb());
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

}