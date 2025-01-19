package com.example.ticketbookingsystem.service;

import com.example.ticketbookingsystem.dao.RowDao;
import com.example.ticketbookingsystem.dto.RowDto;
import com.example.ticketbookingsystem.dto.RowFilter;
import com.example.ticketbookingsystem.entity.Row;
import com.example.ticketbookingsystem.exception.ValidationException;
import com.example.ticketbookingsystem.mapper.RowMapper;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing sector rows.
 */
public class RowService {
    private static final RowService INSTANCE = new RowService();
    private final RowDao rowDao = RowDao.getInstance();
    private final RowMapper rowMapper = RowMapper.getInstance();
    private RowService(){}
    public static RowService getInstance(){
        return INSTANCE;
    }

    /**
     * Finds all rows.
     *
     * @return a list of all rows
     */
    public List<Row> findAll(){
        return rowDao.findAll();
    }

    /**
     * Finds all rows matching the given filter.
     *
     * @param rowFilter the filter to apply
     * @param sectorId the ID of the sector which rows of seats is needed to get
     * @return a list of rows matching the filter
     */
    public List<Row> findAll(RowFilter rowFilter, Long sectorId){
        return rowDao.findAll(rowFilter, sectorId);
    }

    /**
     * Finds a row by its ID.
     *
     * @param id the ID of the row
     * @return an {@link Optional} containing the found row, or empty if not found
     */
    public Optional<Row> findById(Long id){
        return rowDao.findById(id);
    }

    /**
     * Creates a new row.
     *
     * @param rowDto the DTO of the row to create
     */
    public void createRow(RowDto rowDto) {
        Row row = rowMapper.toEntity(rowDto);
        rowDao.save(row);
    }

    /**
     * Updates an existing row.
     *
     * @param id the ID of the row to update
     * @param rowDto the DTO of the updated row
     */
    public void updateRow(Long id, RowDto rowDto){
        Row row = rowMapper.toEntity(rowDto);
        row.setId(id);
        rowDao.update(row);
    }

    /**
     * Deletes a row by its ID.
     *
     * @param id the ID of the row to delete
     */
    public void deleteRow(Long id) {
        rowDao.delete(id);
    }
}