package com.example.ticketbookingsystem.service;

import com.example.ticketbookingsystem.dto.*;
import com.example.ticketbookingsystem.dto.sport_event_dto.SportEventCreateEditDto;
import com.example.ticketbookingsystem.dto.sport_event_dto.SportEventFilter;
import com.example.ticketbookingsystem.dto.sport_event_dto.SportEventReadDto;
import com.example.ticketbookingsystem.entity.Arena;
import com.example.ticketbookingsystem.entity.SportEvent;
import com.example.ticketbookingsystem.mapper.sport_event_mapper.SportEventCreateEditMapper;
import com.example.ticketbookingsystem.mapper.sport_event_mapper.SportEventReadMapper;
import com.example.ticketbookingsystem.repository.SportEventRepository;
import com.example.ticketbookingsystem.utils.SortUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.ticketbookingsystem.entity.QSportEvent.sportEvent;

/**
 * Service class for managing sporting events.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SportEventService {

    private static final String SORT_BY_EVENT_DATE_TIME = "eventDateTime";

    private final SportEventRepository sportEventRepository;

    private final SportEventReadMapper sportEventReadMapper;

    private final SportEventCreateEditMapper sportEventCreateEditMapper;

    private final ArenaService arenaService;

    /**
     * Finds all sporting events.
     *
     * @return a list of all sporting events
     */
    public List<SportEventReadDto> findAll(){
        return sportEventRepository.findAll().stream()
                .map(sportEventReadMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Finds all sporting events matching the given filter.
     *
     * @param sportEventFilter the filter to apply
     * @param pageable object of Pageable interface to apply pagination correctly
     * @return a list of sporting events matching the filter
     */
    public Page<SportEventReadDto> findAll(SportEventFilter sportEventFilter, Pageable pageable){
        var predicate = QPredicates.builder()
                .add(sportEventFilter.startDate(), sportEvent.eventDateTime::after)
                .add(sportEventFilter.endDate(), sportEvent.eventDateTime::before)
                .add(sportEventFilter.arenaId(), sportEvent.arena.id::eq)
                .build();

        Map<String, String> sortOrders = new LinkedHashMap<>();
        if (sportEventFilter.sortOrder() != null && !sportEventFilter.sortOrder().isEmpty()) {
            sortOrders.put(SORT_BY_EVENT_DATE_TIME, sportEventFilter.sortOrder());
        }

        Sort sort = SortUtils.buildSort(sortOrders);
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        List<SportEvent> sportEvents = sportEventRepository.findAllWithArena(predicate, sortedPageable);

        return new PageImpl<>(
                sportEvents.stream().map(sportEventReadMapper::toDto).collect(Collectors.toList()),
                sortedPageable,
                sportEvents.size()
        );
    }

    /**
     * Finds a sporting event by its ID.
     *
     * @param id the ID of the sporting event
     * @return an {@link Optional} containing the found sporting event, or empty if not found
     */
    public Optional<SportEventReadDto> findById(Long id){
        return sportEventRepository.findById(id)
                .map(sportEventReadMapper::toDto);
    }

    /**
     * Creates a new sporting event.
     *
     * @param sportEventCreateEditDto the DTO of the sporting event to create
     */
    public void createSportEvent(SportEventCreateEditDto sportEventCreateEditDto, Long arenaId) {
        SportEvent sportEvent = sportEventCreateEditMapper.toEntity(sportEventCreateEditDto);
        Arena arena = arenaService.findArenaById(arenaId);
        sportEvent.setArena(arena);
        sportEventRepository.save(sportEvent);
        log.info("SportEvent created successfully with dto: {}", sportEventCreateEditDto);
    }

    /**
     * Updates an existing sporting event.
     *
     * @param id the ID of the sporting event to update
     * @param sportEventCreateEditDto the DTO of the updated sporting event
     */
    public void updateSportEvent(Long id, SportEventCreateEditDto sportEventCreateEditDto, Long arenaId) {
        SportEvent sportEvent = sportEventCreateEditMapper.toEntity(sportEventCreateEditDto);
        Arena arena = arenaService.findArenaById(arenaId);
        sportEvent.setId(id);
        sportEvent.setArena(arena);
        sportEventRepository.save(sportEvent);
        log.info("SportEvent with id {} updated successfully with dto: {}", id, sportEventCreateEditDto);
    }

    /**
     * Deletes a sporting event by its ID.
     *
     * @param id the ID of the event to delete
     */
    public void deleteSportEvent(Long id) {
        sportEventRepository.deleteById(id);
        log.info("SportEvent with id {} deleted successfully.", id);
    }

}
