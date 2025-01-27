package com.example.ticketbookingsystem.service;

import com.example.ticketbookingsystem.dto.*;
import com.example.ticketbookingsystem.entity.Arena;
import com.example.ticketbookingsystem.entity.Sector;
import com.example.ticketbookingsystem.exception.DaoResourceNotFoundException;
import com.example.ticketbookingsystem.mapper.SectorCreateEditMapper;
import com.example.ticketbookingsystem.mapper.SectorReadMapper;
import com.example.ticketbookingsystem.repository.SectorRepository;
import com.example.ticketbookingsystem.utils.SortUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final SectorRepository sectorRepository;

    private final SectorReadMapper sectorReadMapper;

    private final SectorCreateEditMapper sectorCreateEditMapper;

    private final ArenaService arenaService;

    private static final String SORT_BY_SECTOR_NAME = "sectorName";

    private static final String SORT_BY_MAX_ROWS_NUMB = "maxRowsNumb";

    private static final String SORT_BY_MAX_SEATS_NUMB = "maxSeatsNumb";

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
    public void createSector(SectorCreateEditDto sectorCreateEditDto, Long arenaId) {
        Sector sector = sectorCreateEditMapper.toEntity(sectorCreateEditDto);
        Arena arena = arenaService.findArenaById(arenaId);
        sector.setArena(arena);
        sectorRepository.save(sector);
        sectorRepository.updateArenaAfterSectorSave(arenaId, sector.getMaxSeatsNumb());
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
        sectorRepository.updateArenaBeforeSectorUpdate(arenaId,
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
