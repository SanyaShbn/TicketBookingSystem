package com.example.ticketbookingsystem.service;

import com.example.ticketbookingsystem.dto.ArenaCreateEditDto;
import com.example.ticketbookingsystem.dto.ArenaFilter;
import com.example.ticketbookingsystem.dto.ArenaReadDto;
import com.example.ticketbookingsystem.dto.QPredicates;
import com.example.ticketbookingsystem.entity.Arena;
import com.example.ticketbookingsystem.exception.DaoResourceNotFoundException;
import com.example.ticketbookingsystem.exception.ValidationException;
import com.example.ticketbookingsystem.mapper.ArenaCreateEditMapper;
import com.example.ticketbookingsystem.mapper.ArenaReadMapper;
import com.example.ticketbookingsystem.repository.ArenaRepository;
import com.example.ticketbookingsystem.utils.SortUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.ticketbookingsystem.entity.QArena.arena;

/**
 * Service class for managing arenas.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ArenaService {

    private final ArenaRepository arenaRepository;

    private final ArenaCreateEditMapper arenaCreateEditMapper;

    private final ArenaReadMapper arenaReadMapper;

    private static final String SORT_BY_CAPACITY = "capacity";

    private static final String SORT_BY_GENERAL_SEATS_NUMB = "generalSeatsNumb";

    /**
     * Finds all arenas mapped to ArenaReadDto class.
     *c
     * @return a list of all arenas
     */
    public List<ArenaReadDto> findAll(){
        return arenaRepository.findAll().stream()
                .map(arenaReadMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Finds all arenas matching the given filter and mapped to ArenaReadDto class.
     *
     * @param arenaFilter the filter to apply
     * @param pageable object of Pageable interface to apply pagination correctly
     * @return a page of arenas matching the filter
     */
    public Page<ArenaReadDto> findAll(ArenaFilter arenaFilter, Pageable pageable){
        var predicate = QPredicates.builder()
                .add(arenaFilter.city(), arena.city::containsIgnoreCase)
                .build();

        Map<String, String> sortOrders = new LinkedHashMap<>();
        if (arenaFilter.capacitySortOrder() != null && !arenaFilter.capacitySortOrder().isEmpty()) {
            sortOrders.put(SORT_BY_CAPACITY, arenaFilter.capacitySortOrder());
        }
        if (arenaFilter.seatsNumbSortOrder() != null && !arenaFilter.seatsNumbSortOrder().isEmpty()) {
            sortOrders.put(SORT_BY_GENERAL_SEATS_NUMB, arenaFilter.seatsNumbSortOrder());
        }

        Sort sort = SortUtils.buildSort(sortOrders);
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return arenaRepository.findAll(predicate, sortedPageable)
                .map(arenaReadMapper::toDto);
    }

    /**
     * Finds an arena by its ID.
     *
     * @param id the ID of the arena
     * @return an {@link Optional} containing the found arena mapped to ArenaReadDto, or empty if not found
     */
    public Optional<ArenaReadDto> findById(Long id){
        return arenaRepository.findById(id)
                .map(arenaReadMapper::toDto);
    }

    public Arena findArenaById(Long id) {
        return arenaRepository.findById(id)
                .orElseThrow(() -> new DaoResourceNotFoundException("Arena not found"));
    }

    /**
     * Creates a new arena.
     *
     * @param arenaCreateEditDto the DTO of the arena to create
     * @throws ValidationException if the arena is not valid
     */
    public void createArena(ArenaCreateEditDto arenaCreateEditDto) {
        Arena arena = arenaCreateEditMapper.toEntity(arenaCreateEditDto);
        arenaRepository.save(arena);
        log.info("Arena created successfully with dto: {}", arenaCreateEditDto);
    }

    /**
     * Updates an existing arena.
     *
     * @param arenaCreateEditDto the DTO of the updated arena
     * @throws ValidationException if the updated arena is not valid
     */
    public void updateArena(Long id, ArenaCreateEditDto arenaCreateEditDto) {
        Arena arena = arenaCreateEditMapper.toEntity(arenaCreateEditDto);
        arena.setId(id);
        arenaRepository.save(arena);
        log.info("Arena with id {} updated successfully with dto: {}", id, arenaCreateEditDto);
    }

    /**
     * Deletes an arena by its ID.
     *
     * @param id the ID of the arena to delete
     */
    public void deleteArena(Long id) {
        arenaRepository.deleteById(id);
        log.info("Arena with id {} deleted successfully.", id);
    }

}