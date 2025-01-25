package com.example.ticketbookingsystem.service;

import com.example.ticketbookingsystem.dto.ArenaReadDto;
import com.example.ticketbookingsystem.dto.SectorCreateEditDto;
import com.example.ticketbookingsystem.dto.SectorFilter;
import com.example.ticketbookingsystem.dto.SectorReadDto;
import com.example.ticketbookingsystem.entity.Arena;
import com.example.ticketbookingsystem.entity.Sector;
import com.example.ticketbookingsystem.exception.DaoResourceNotFoundException;
import com.example.ticketbookingsystem.mapper.SectorCreateEditMapper;
import com.example.ticketbookingsystem.mapper.SectorReadMapper;
import com.example.ticketbookingsystem.repository.SectorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing arena's sectors.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SectorService {

    private final SectorRepository sectorRepository;

    private final SectorReadMapper sectorReadMapper;

    private final SectorCreateEditMapper sectorCreateEditMapper;

    private final ArenaService arenaService;

    /**
     * Finds all sectors.
     *
     * @return a list of all sectors
     */
    public List<SectorReadDto> findAll(){
        return sectorRepository.findAll().stream()
                .map(sectorReadMapper::toDto)
                .collect(Collectors.toList());
    }

//    /**
//     * Finds all sectors matching the given filter.
//     *
//     * @param sectorFilter the filter to apply
//     * @param arenaId the ID of the arena which sectors is needed to get
//     * @return a list of sectors matching the filter
//     */
//    public List<SectorReadDto> findAll(SectorFilter sectorFilter, Long arenaId){
//        return sectorRepository.findAll(sectorFilter, arenaId).stream()
//                .map(sectorReadMapper::toDto)
//                .collect(Collectors.toList());;
//    }

    public List<SectorReadDto> findAllByArenaId(Long arenaId){
        return sectorRepository.findAllByArenaId(arenaId).stream()
                .map(sectorReadMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Finds a sector by its ID.
     *
     * @param id the ID of the sector
     * @return an {@link Optional} containing the found sector, or empty if not found
     */
    public Optional<SectorReadDto> findById(Long id){
        return sectorRepository.findById(id)
                .map(sectorReadMapper::toDto);
    }

    /**
     * Creates a new sector.
     *
     * @param sectorCreateEditDto the DTO of the sector to create
     */
    @Transactional
    public void createSector(SectorCreateEditDto sectorCreateEditDto, Long arenaId) {
        Sector sector = sectorCreateEditMapper.toEntity(sectorCreateEditDto);
        Arena arena = arenaService.findArenaById(arenaId);
        sector.setArena(arena);
        sectorRepository.save(sector);
        sectorRepository.updateArenaAfterSectorSave(sector.getArena().getId(), sector.getMaxSeatsNumb());
        log.info("Sector created successfully with dto: {}", sectorCreateEditDto);
    }

    /**
     * Updates an existing sector.
     *
     * @param id the ID of the sector to update
     * @param sectorCreateEditDto the DTO of the updated sector
     */
    @Transactional
    public void updateSector(Long id, SectorCreateEditDto sectorCreateEditDto, Long arenaId) {
        Optional<Sector> sectorBeforeUpdate = sectorRepository.findById(id);
        if (sectorBeforeUpdate.isEmpty()) {
            log.error("Failed to find sector with provided id: {}", id);
            throw new DaoResourceNotFoundException("Sector not found");
        }

        Sector sector = sectorCreateEditMapper.toEntity(sectorCreateEditDto);
        Arena arena = arenaService.findArenaById(arenaId);
        sector.setId(id);
        sector.setArena(arena);
        sectorRepository.updateArenaBeforeSectorUpdate(sector.getArena().getId(),
                sectorBeforeUpdate.get().getMaxSeatsNumb(), sector.getMaxSeatsNumb());
        sectorRepository.save(sector);
        log.info("Sector with id {} updated successfully with dto: {}", id, sectorCreateEditDto);
    }

    /**
     * Deletes a sector by its ID.
     *
     * @param id the ID of the sector to delete
     */
    @Transactional
    public void deleteSector(Long id) {
        Optional<Sector> sector = sectorRepository.findById(id);
        if (sector.isPresent()) {
            sectorRepository.updateArenaAfterSectorDelete(sector.get().getArena().getId(),
                    sector.get().getMaxSeatsNumb());
            sectorRepository.delete(sector.get());
            log.info("Sector with id {} deleted successfully.", id);
        } else {
            log.error("Failed to find sector with provided id: {}", id);
            throw new DaoResourceNotFoundException("Sector not found");
        }
    }
}
