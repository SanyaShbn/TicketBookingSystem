package com.example.ticketbookingsystem.service;

import com.example.ticketbookingsystem.dao.SportEventDao;
import com.example.ticketbookingsystem.dto.SportEventDto;
import com.example.ticketbookingsystem.dto.SportEventFilter;
import com.example.ticketbookingsystem.entity.SportEvent;
import com.example.ticketbookingsystem.mapper.SportEventMapper;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing sporting events.
 */
public class SportEventService {
    private static final SportEventService INSTANCE = new SportEventService();
    private final SportEventDao sportEventDao = SportEventDao.getInstance();
    private final SportEventMapper sportEventMapper = SportEventMapper.getInstance();
    private SportEventService(){}

    public static SportEventService getInstance(){
        return INSTANCE;
    }

    /**
     * Finds all sporting events.
     *
     * @return a list of all sporting events
     */
    public List<SportEvent> findAll(){
        return sportEventDao.findAll();
    }

    /**
     * Finds all sporting events matching the given filter.
     *
     * @param sportEventFilter the filter to apply
     * @return a list of sporting events matching the filter
     */
    public List<SportEvent> findAll(SportEventFilter sportEventFilter){
        return sportEventDao.findAll(sportEventFilter);
    }

    /**
     * Finds a sporting event by its ID.
     *
     * @param id the ID of the sporting event
     * @return an {@link Optional} containing the found sporting event, or empty if not found
     */
    public Optional<SportEvent> findById(Long id){
        return sportEventDao.findById(id);
    }

    /**
     * Creates a new sporting event.
     *
     * @param sportEventDto the DTO of the sporting event to create
     */
    public void createSportEvent(SportEventDto sportEventDto) {
        SportEvent sportEvent = sportEventMapper.toEntity(sportEventDto);
        sportEventDao.save(sportEvent);
    }

    /**
     * Updates an existing sporting event.
     *
     * @param id the ID of the sporting event to update
     * @param sportEventDto the DTO of the updated sporting event
     */
    public void updateSportEvent(Long id, SportEventDto sportEventDto) {
        SportEvent sportEvent = sportEventMapper.toEntity(sportEventDto);
        sportEvent.setId(id);
        sportEventDao.update(sportEvent);
    }

    /**
     * Deletes a sporting event by its ID.
     *
     * @param id the ID of the event to delete
     */
    public void deleteSportEvent(Long id) {
        sportEventDao.delete(id);
    }

}
