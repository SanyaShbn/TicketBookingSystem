package com.example.ticketbookingsystem.service;

import com.example.ticketbookingsystem.dto.sport_event_dto.SportEventCreateEditDto;
import com.example.ticketbookingsystem.dto.sport_event_dto.SportEventFilter;
import com.example.ticketbookingsystem.dto.sport_event_dto.SportEventReadDto;
import com.example.ticketbookingsystem.entity.Arena;
import com.example.ticketbookingsystem.entity.QSportEvent;
import com.example.ticketbookingsystem.entity.SportEvent;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.exception.DaoResourceNotFoundException;
import com.example.ticketbookingsystem.mapper.sport_event_mapper.SportEventCreateEditMapper;
import com.example.ticketbookingsystem.mapper.sport_event_mapper.SportEventReadMapper;
import com.example.ticketbookingsystem.repository.SportEventRepository;
import com.example.ticketbookingsystem.utils.SortUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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

    private final ImageService imageService;

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
        Predicate predicate = toPredicate(sportEventFilter);

        Map<String, String> sortOrders = new LinkedHashMap<>();
        if (sportEventFilter.sortOrder() != null && !sportEventFilter.sortOrder().isEmpty()) {
            sortOrders.put(SORT_BY_EVENT_DATE_TIME, sportEventFilter.sortOrder());
        }

        Sort sort = SortUtils.buildSort(sortOrders);
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        return sportEventRepository.findAllWithArena(predicate, sortedPageable)
                .map(sportEventReadMapper::toDto);
    }

    private Predicate toPredicate(SportEventFilter sportEventFilter) {
        BooleanBuilder builder = new BooleanBuilder();
        QSportEvent sportEvent = QSportEvent.sportEvent;

        if (sportEventFilter.startDate() != null) {
            builder.and(sportEvent.eventDateTime.after(sportEventFilter.startDate()));
        }

        if (sportEventFilter.endDate() != null) {
            builder.and(sportEvent.eventDateTime.before(sportEventFilter.endDate()));
        }

        if (sportEventFilter.arenaName() != null && !sportEventFilter.arenaName().isEmpty()) {
            builder.and(sportEvent.arena.name.like(sportEventFilter.arenaName()));
        }

        return builder;
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
    @Transactional
    public SportEventReadDto createSportEvent(SportEventCreateEditDto sportEventCreateEditDto,
                                              Long arenaId){
        try {
            SportEvent sportEvent = sportEventCreateEditMapper.toEntity(sportEventCreateEditDto);
            Arena arena = arenaService.findArenaById(arenaId);
            sportEvent.setArena(arena);

            if (sportEventCreateEditDto.getImageFile() != null) {
                imageService.uploadImage(sportEventCreateEditDto.getImageFile());
            }

            SportEvent savedSportEvent = sportEventRepository.save(sportEvent);
            log.info("SportEvent created successfully with dto: {}", sportEventCreateEditDto);
            return sportEventReadMapper.toDto(savedSportEvent);
        } catch (DataAccessException e){
            log.error("Failed to create SportEvent with dto: {}", sportEventCreateEditDto);
            throw new DaoCrudException(e);
        }
    }

    /**
     * Updates an existing sporting event.
     *
     * @param id the ID of the sporting event to update
     * @param sportEventCreateEditDto the DTO of the updated sporting event
     */
    @Transactional
    public SportEventReadDto updateSportEvent(Long id,
                                              SportEventCreateEditDto sportEventCreateEditDto,
                                              Long arenaId) {
        try {
            SportEvent sportEventBeforeUpdate = sportEventRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("Failed to find sport event with provided id: {}", id);
                        return new DaoResourceNotFoundException("Sport event not found");
                    });

            SportEvent sportEvent = sportEventCreateEditMapper.toEntity(sportEventCreateEditDto);
            Arena arena = arenaService.findArenaById(arenaId);
            sportEvent.setId(id);
            sportEvent.setArena(arena);

            updatePosterImageIfNecessary(sportEventCreateEditDto, sportEvent, sportEventBeforeUpdate);

            SportEvent updatedSportEvent = sportEventRepository.save(sportEvent);
            log.info("SportEvent with id {} updated successfully with dto: {}", id, sportEventCreateEditDto);
            return sportEventReadMapper.toDto(updatedSportEvent);
        } catch (DataAccessException e){
            log.error("Failed to update SportEvent {} with dto: {}", id, sportEventCreateEditDto);
            throw new DaoCrudException(e);
        }
    }

    private void updatePosterImageIfNecessary(SportEventCreateEditDto dto, SportEvent sportEvent, SportEvent sportEventBeforeUpdate) {
        if (dto.getImageFile() != null) {
            imageService.uploadImage(dto.getImageFile());
        } else {
            sportEvent.setPosterImage(sportEventBeforeUpdate.getPosterImage());
        }
    }

    /**
     * Deletes a sporting event by its ID.
     *
     * @param id the ID of the event to delete
     */
    @Transactional
    public void deleteSportEvent(Long id) {
        Optional<SportEvent> sportEventOptional = sportEventRepository.findById(id);
        if (sportEventOptional.isEmpty()) {
            log.error("Failed to find sport event with provided id: {}", id);
            throw new DaoResourceNotFoundException("Sport Event not found with id " + id);
        }
        try {
            sportEventRepository.deleteById(id);
            log.info("SportEvent with id {} deleted successfully.", id);
        } catch (DataAccessException e){
            log.error("Failed to delete SportEvent with id {}", id);
            throw new DaoCrudException(e);
        }
    }

    /**
     * Deletes a poster image for a sport event by its filename and updates the database.
     *
     * @param filename the name of the poster image file to be deleted
     */
    @Transactional
    public void deletePosterImageForSportEvent(String filename) {
        imageService.deleteImageFromDb(filename);
        sportEventRepository.updatePosterImagesAfterImageDeleting(filename);
    }

}
