package com.example.ticketbookingsystem.service;

import com.example.ticketbookingsystem.dto.sector_dto.SectorCreateEditDto;
import com.example.ticketbookingsystem.dto.sector_dto.SectorFilter;
import com.example.ticketbookingsystem.dto.sector_dto.SectorReadDto;
import com.example.ticketbookingsystem.entity.Arena;
import com.example.ticketbookingsystem.entity.Sector;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.exception.DaoResourceNotFoundException;
import com.example.ticketbookingsystem.mapper.sector_mapper.SectorCreateEditMapper;
import com.example.ticketbookingsystem.mapper.sector_mapper.SectorReadMapper;
import com.example.ticketbookingsystem.repository.SectorRepository;
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
 * Service class for managing arena's sectors.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SectorService {

    private static final String SORT_BY_SECTOR_NAME = "sectorName";

    private static final String SORT_BY_MAX_ROWS_NUMB = "maxRowsNumb";

    private static final String SORT_BY_MAX_SEATS_NUMB = "maxSeatsNumb";

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

    /**
     * Finds all sectors matching the given filter.
     *
     * @param arenaId the ID of the arena which sectors is needed to get
     * @param sectorFilter the filter to apply
     * @param pageable object of Pageable interface to apply pagination correctly
     * @return a list of sectors matching the filter
     */
    public Page<SectorReadDto> findAll(Long arenaId, SectorFilter sectorFilter, Pageable pageable){
        Map<String, String> sortOrders = new LinkedHashMap<>();
        if (sectorFilter.nameSortOrder() != null && !sectorFilter.nameSortOrder().isEmpty()) {
            sortOrders.put(SORT_BY_SECTOR_NAME, sectorFilter.nameSortOrder());
        }
        if (sectorFilter.maxRowsNumbSortOrder() != null && !sectorFilter.maxRowsNumbSortOrder().isEmpty()) {
            sortOrders.put(SORT_BY_MAX_ROWS_NUMB, sectorFilter.maxRowsNumbSortOrder());
        }
        if (sectorFilter.maxSeatsNumbSortOrder() != null && !sectorFilter.maxSeatsNumbSortOrder().isEmpty()) {
            sortOrders.put(SORT_BY_MAX_SEATS_NUMB, sectorFilter.maxSeatsNumbSortOrder());
        }

        Sort sort = SortUtils.buildSort(sortOrders);
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return sectorRepository.findAllByArenaId(arenaId, sortedPageable)
                .map(sectorReadMapper::toDto);
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

    public Sector findSectorById(Long id) {
        return sectorRepository.findById(id)
                .orElseThrow(() -> new DaoResourceNotFoundException("Sector not found"));
    }

    /**
     * Creates a new sector.
     *
     * @param sectorCreateEditDto the DTO of the sector to create
     */
    @Transactional
    public SectorReadDto createSector(SectorCreateEditDto sectorCreateEditDto, Long arenaId) {
        try {
            Sector sector = sectorCreateEditMapper.toEntity(sectorCreateEditDto);
            Arena arena = arenaService.findArenaById(arenaId);
            sector.setArena(arena);
            Sector updatedSector = sectorRepository.save(sector);
            sectorRepository.updateArenaAfterSectorSave(arenaId, sector.getMaxSeatsNumb());
            log.info("Sector created successfully with dto: {}", sectorCreateEditDto);
            return sectorReadMapper.toDto(updatedSector);
        } catch (DataAccessException e){
            log.error("Failed to create sector with dto: {}", sectorCreateEditDto);
            throw new DaoCrudException(e);
        }
    }

    /**
     * Updates an existing sector.
     *
     * @param id the ID of the sector to update
     * @param sectorCreateEditDto the DTO of the updated sector
     */
    @Transactional
    public SectorReadDto updateSector(Long id, SectorCreateEditDto sectorCreateEditDto, Long arenaId) {
        try {
            Sector sectorBeforeUpdate = getExistingSector(id);
            validateSector(sectorBeforeUpdate, sectorCreateEditDto);

            Sector sector = prepareSectorForUpdate(id, sectorCreateEditDto, arenaId, sectorBeforeUpdate);
            Sector updatedSector = saveSectorAndFlush(sector, arenaId, sectorBeforeUpdate);

            log.info("Sector with id {} updated successfully with dto: {}", id, sectorCreateEditDto);
            return sectorReadMapper.toDto(updatedSector);
        } catch (DataAccessException e) {
            log.error("Failed to update sector {} with dto: {}", id, sectorCreateEditDto);
            throw new DaoCrudException("Database access error occurred", e);
        }
    }

    private Sector getExistingSector(Long id) {
        return sectorRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Failed to find sector with provided id: {}", id);
                    return new DaoResourceNotFoundException("Sector not found");
                });
    }

    private void validateSector(Sector sectorBeforeUpdate, SectorCreateEditDto sectorCreateEditDto) {
        if (sectorBeforeUpdate.getAvailableRowsNumb() > sectorCreateEditDto.getMaxRowsNumb() ||
                sectorBeforeUpdate.getAvailableSeatsNumb() > sectorCreateEditDto.getMaxSeatsNumb()) {
            log.error("Failed to update sector {} with dto: {}", sectorBeforeUpdate.getId(), sectorCreateEditDto);
            throw new DaoCrudException(
                    "Available rows or seats exceed the maximum allowed in the new sector configuration."
            );
        }
    }

    private Sector prepareSectorForUpdate(Long id, SectorCreateEditDto sectorCreateEditDto, Long arenaId, Sector sectorBeforeUpdate) {
        Sector sector = sectorCreateEditMapper.toEntity(sectorCreateEditDto);
        Arena arena = arenaService.findArenaById(arenaId);
        sector.setId(id);
        sector.setArena(arena);
        sector.setAvailableRowsNumb(sectorBeforeUpdate.getAvailableRowsNumb());
        sector.setAvailableSeatsNumb(sectorBeforeUpdate.getAvailableSeatsNumb());
        return sector;
    }

    private Sector saveSectorAndFlush(Sector sector, Long arenaId, Sector sectorBeforeUpdate) {
        sectorRepository.updateArenaBeforeSectorUpdate(arenaId,
                sectorBeforeUpdate.getMaxSeatsNumb(), sector.getMaxSeatsNumb());
        Sector updatedSector = sectorRepository.save(sector);
        sectorRepository.flush();
        return updatedSector;
    }

    /**
     * Deletes a sector by its ID.
     *
     * @param id the ID of the sector to delete
     */
    @Transactional
    public void deleteSector(Long id) {
        try {
            Optional<Sector> sector = sectorRepository.findById(id);
            if (sector.isPresent()) {
                sectorRepository.updateArenaAfterSectorDelete(sector.get().getArena().getId(),
                        sector.get().getMaxSeatsNumb());
                sectorRepository.delete(sector.get());
                sectorRepository.flush();
                log.info("Sector with id {} deleted successfully.", id);
            } else {
                log.error("Failed to find sector with provided id: {}", id);
                throw new DaoResourceNotFoundException("Sector not found");
            }
        } catch (DataAccessException e){
            log.error("Failed to delete sector with id {}", id);
            throw new DaoCrudException(e);
        }
    }
}
