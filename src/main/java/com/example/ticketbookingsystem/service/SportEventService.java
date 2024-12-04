package com.example.ticketbookingsystem.service;

import com.example.ticketbookingsystem.dao.SportEventDao;
import com.example.ticketbookingsystem.dto.SportEventDto;
import com.example.ticketbookingsystem.entity.SportEvent;

import java.util.List;
import java.util.Optional;

public class SportEventService {
    private final static SportEventService INSTANCE = new SportEventService();
    private final SportEventDao sportEventDao = SportEventDao.getInstance();
    private SportEventService(){}

    public static SportEventService getInstance(){
        return INSTANCE;
    }

    public List<SportEvent> findAll(){
        return sportEventDao.findAll();
    }

    public Optional<SportEvent> findById(Long id){
        return sportEventDao.findById(id);
    }

    private SportEvent buildSportEventFromDto(SportEventDto sportEventDto) {
        return SportEvent.builder()
                .eventName(sportEventDto.getEventName())
                .eventDateTime(sportEventDto.getEventDateTime())
                .arena(sportEventDto.getArena())
                .build();
    }

    public void createSportEvent(SportEventDto sportEventDto) {
        SportEvent sportEvent = buildSportEventFromDto(sportEventDto);
        sportEventDao.save(sportEvent);
    }

    public void updateSportEvent(Long id, SportEventDto sportEventDto) {
        SportEvent sportEvent = buildSportEventFromDto(sportEventDto);
        sportEvent.setId(id);
        sportEventDao.update(sportEvent);
    }

    public void deleteSportEvent(Long id) {
        sportEventDao.delete(id);
    }
}
