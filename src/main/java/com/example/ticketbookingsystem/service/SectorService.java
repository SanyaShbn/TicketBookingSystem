package com.example.ticketbookingsystem.service;

import com.example.ticketbookingsystem.dao.SectorDao;
import com.example.ticketbookingsystem.dto.SectorDto;
import com.example.ticketbookingsystem.dto.SectorFilter;
import com.example.ticketbookingsystem.entity.Sector;
import com.example.ticketbookingsystem.mapper.SectorMapper;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing arena's sectors.
 */
public class SectorService {
    private static final SectorService INSTANCE = new SectorService();
    private final SectorDao sectorDao = SectorDao.getInstance();
    private final SectorMapper sectorMapper = SectorMapper.getInstance();
    private SectorService(){}
    public static SectorService getInstance(){
        return INSTANCE;
    }

    /**
     * Finds all sectors.
     *
     * @return a list of all sectors
     */
    public List<Sector> findAll(){
        return sectorDao.findAll();
    }

    /**
     * Finds all sectors matching the given filter.
     *
     * @param sectorFilter the filter to apply
     * @param arenaId the ID of the arena which sectors is needed to get
     * @return a list of sectors matching the filter
     */
    public List<Sector> findAll(SectorFilter sectorFilter, Long arenaId){
        return sectorDao.findAll(sectorFilter, arenaId);
    }

    /**
     * Finds a sector by its ID.
     *
     * @param id the ID of the sector
     * @return an {@link Optional} containing the found sector, or empty if not found
     */
    public Optional<Sector> findById(Long id){
        return sectorDao.findById(id);
    }

    /**
     * Creates a new sector.
     *
     * @param sectorDto the DTO of the sector to create
     */
    public void createSector(SectorDto sectorDto) {
        Sector sector = sectorMapper.toEntity(sectorDto);
        sectorDao.save(sector);
    }

    /**
     * Updates an existing sector.
     *
     * @param id the ID of the sector to update
     * @param sectorDto the DTO of the updated sector
     */
    public void updateSector(Long id, SectorDto sectorDto) {
        Sector sector = sectorMapper.toEntity(sectorDto);
        sector.setId(id);
        sectorDao.update(sector);
    }

    /**
     * Deletes a sector by its ID.
     *
     * @param id the ID of the sector to delete
     */
    public void deleteSector(Long id) {
        sectorDao.delete(id);
    }
}
