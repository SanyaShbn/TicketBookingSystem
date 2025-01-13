package com.example.ticketbookingsystem.service;

import com.example.ticketbookingsystem.dao.RowDao;
import com.example.ticketbookingsystem.dto.RowDto;
import com.example.ticketbookingsystem.dto.RowFilter;
import com.example.ticketbookingsystem.entity.Row;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class RowService {
    private static final RowService INSTANCE = new RowService();
    private final RowDao rowDao = RowDao.getInstance();
    private RowService(){}
    public static RowService getInstance(){
        return INSTANCE;
    }

    public List<Row> findAll(){
        return rowDao.findAll();
    }

    public List<Row> findAll(RowFilter rowFilter, Long sectorId){
        return rowDao.findAll(rowFilter, sectorId);
    }

    public Optional<Row> findById(Long id){
        return rowDao.findById(id);
    }
    public void createRow(RowDto rowDto) {
        Row row = buildRowFromDto(rowDto);
        rowDao.save(row);
    }
    public void updateRow(Long id, RowDto rowDto){
        Row row = buildRowFromDto(rowDto);
        row.setId(id);
        rowDao.update(row);
    }
    public void deleteRow(Long id) {
        rowDao.delete(id);
    }

    private Row buildRowFromDto(RowDto rowDto) {
        return Row.builder()
                .rowNumber(rowDto.getRowNumber())
                .seatsNumb(rowDto.getSeatsNumb())
                .sector(rowDto.getSector())
                .build();
    }
}