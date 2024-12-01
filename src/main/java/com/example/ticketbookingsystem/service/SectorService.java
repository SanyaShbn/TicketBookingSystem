package com.example.ticketbookingsystem.service;

import com.example.ticketbookingsystem.dao.SectorDao;
import com.example.ticketbookingsystem.dto.SectorDto;
import com.example.ticketbookingsystem.entity.Sector;

import java.util.List;
import java.util.Optional;

public class SectorService {
    private final static SectorService INSTANCE = new SectorService();
    private final SectorDao sectorDao = SectorDao.getInstance();
    private SectorService(){}
    public static SectorService getInstance(){
        return INSTANCE;
    }

    public List<Sector> findAll(){
        return sectorDao.findAll();
    }

    public Optional<Sector> findById(Long id){
        return sectorDao.findById(id);
    }
    public List<Sector> findAllByArenaId(Long arenaId){
        return sectorDao.findAllByArenaId(arenaId);
    }

    private Sector buildSectorFromDto(SectorDto sectorDto) {
        return Sector.builder()
                .sectorName(sectorDto.getSectorName())
                .arena(sectorDto.getArena())
                .build();
    }

    public void createSector(SectorDto sectorDto) {
        Sector sector = buildSectorFromDto(sectorDto);
        sectorDao.save(sector);
    }

    public void updateSector(Long id, SectorDto sectorDto) {
        Sector sector = buildSectorFromDto(sectorDto);
        sector.setId(id);
        sectorDao.update(sector);
    }

    public void deleteSector(Long id) {
        sectorDao.delete(id);
    }
}
