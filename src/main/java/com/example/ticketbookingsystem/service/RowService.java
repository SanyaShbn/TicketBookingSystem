package com.example.ticketbookingsystem.service;

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
import com.example.ticketbookingsystem.utils.SortUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing sector rows.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RowService {

    private static final String SORT_BY_ROW_NUMBER = "rowNumber";

    private static final String SORT_BY_SEATS_NUMB = "seatsNumb";

    private final RowRepository rowRepository;

    private final RowReadMapper rowReadMapper;

    private final RowCreateEditMapper rowCreateEditMapper;

    private final SectorService sectorService;

    /**
     * Finds all rows.
     *
     * @return a list of all rows
     */
    public List<RowReadDto> findAll(){
        return rowRepository.findAll().stream()
                .map(rowReadMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Finds all rows matching the given filter.
     *
     * @param sectorId the ID of the sector which rows of seats is needed to get
     * @param rowFilter the filter to apply
     * @param pageable object of Pageable interface to apply pagination correctly
     * @return a list of rows matching the filter
     */
    public Page<RowReadDto> findAll(Long sectorId, RowFilter rowFilter, Pageable pageable){
        Map<String, String> sortOrders = new LinkedHashMap<>();
        if (rowFilter.rowNumberOrder() != null && !rowFilter.rowNumberOrder().isEmpty()) {
            sortOrders.put(SORT_BY_ROW_NUMBER, rowFilter.rowNumberOrder());
        }
        if (rowFilter.seatsNumbOrder() != null && !rowFilter.seatsNumbOrder().isEmpty()) {
            sortOrders.put(SORT_BY_SEATS_NUMB, rowFilter.seatsNumbOrder());
        }

        Sort sort = SortUtils.buildSort(sortOrders);
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return rowRepository.findAllBySectorId(sectorId, sortedPageable)
                .map(rowReadMapper::toDto);
    }

    /**
     * Finds a row by its ID.
     *
     * @param id the ID of the row
     * @return an {@link Optional} containing the found row, or empty if not found
     */
    public Optional<RowReadDto> findById(Long id){
        return rowRepository.findById(id)
                .map(rowReadMapper::toDto);
    }

    /**
     * Creates a new row.
     *
     * @param rowCreateEditDto the DTO of the row to create
     */
    @Transactional
    public RowReadDto createRow(RowCreateEditDto rowCreateEditDto, Long sectorId) {
        try {
            Row row = rowCreateEditMapper.toEntity(rowCreateEditDto);
            Sector sector = sectorService.findSectorById(sectorId);
            row.setSector(sector);

            Row savedRow = rowRepository.save(row);
            rowRepository.updateSectorAfterRowSave(sectorId, row.getSeatsNumb());
            log.info("Row created successfully with dto: {}", rowCreateEditDto);
            return rowReadMapper.toDto(savedRow);
        } catch (DataAccessException e){
            log.error("Failed to create row with dto: {}", rowCreateEditDto);
            throw new DaoCrudException(e);
        }
    }

    /**
     * Updates an existing row.
     *
     * @param id the ID of the row to update
     * @param rowCreateEditDto the DTO of the updated row
     */
    @Transactional
    public RowReadDto updateRow(Long id, RowCreateEditDto rowCreateEditDto, Long sectorId) {
        try {
            Optional<Row> optionalRow = rowRepository.findById(id);
            if (optionalRow.isEmpty()) {
                log.error("Failed to find row with provided id: {}", id);
                throw new DaoResourceNotFoundException("Row not found");
            }
            Row rowToUpdate = optionalRow.get();

            rowRepository.updateSectorBeforeRowUpdate(sectorId,
                    rowToUpdate.getSeatsNumb(), rowCreateEditDto.getSeatsNumb());

            rowCreateEditMapper.updateEntityFromDto(rowCreateEditDto, rowToUpdate);

            Sector sector = sectorService.findSectorById(sectorId);
            rowToUpdate.setSector(sector);

            rowRepository.save(rowToUpdate);
            rowRepository.flush();
            log.info("Row with id {} updated successfully with dto: {}", id, rowCreateEditDto);
            return rowReadMapper.toDto(rowToUpdate);
        } catch (DataAccessException e){
            log.error("Failed to update row {} with dto: {}", id, rowCreateEditDto);
            throw new DaoCrudException(e);
        }
    }

    /**
     * Deletes a row by its ID.
     *
     * @param id the ID of the row to delete
     */
    @Transactional
    public void deleteRow(Long id) {
        try {
            Optional<Row> row = rowRepository.findById(id);
            if (row.isPresent()) {
                rowRepository.updateSectorAfterRowDelete(row.get().getSector().getId(),
                        row.get().getSeatsNumb());
                rowRepository.delete(row.get());
                rowRepository.flush();
                log.info("Row with id {} deleted successfully.", id);
            } else {
                log.error("Failed to find row with provided id: {}", id);
                throw new DaoResourceNotFoundException("Row not found");
            }
        } catch (DataAccessException e){
            log.error("Failed to delete row with id {}", id);
            throw new DaoCrudException(e);
        }
    }
}