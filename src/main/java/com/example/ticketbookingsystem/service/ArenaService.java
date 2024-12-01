package com.example.ticketbookingsystem.service;

import com.example.ticketbookingsystem.dao.ArenaDao;
import com.example.ticketbookingsystem.dto.ArenaDto;
import com.example.ticketbookingsystem.entity.Arena;
import com.example.ticketbookingsystem.exception.ValidationException;
import com.example.ticketbookingsystem.validator.CreateOrUpdateArenaValidator;

import java.util.List;
import java.util.Optional;

public class ArenaService {
    private final static ArenaService INSTANCE = new ArenaService();
    private final ArenaDao arenaDao = ArenaDao.getInstance();
    private final CreateOrUpdateArenaValidator createOrUpdateArenaValidator = CreateOrUpdateArenaValidator.getInstance();
    private ArenaService(){}
    public static ArenaService getInstance(){
        return INSTANCE;
    }

    public List<Arena> findAll(){
        return arenaDao.findAll();
    }

    public Optional<Arena> findById(Long id){
        return arenaDao.findById(id);
    }

    private Arena buildArenaFromDto(ArenaDto arenaDto) {
        return Arena.builder()
                .name(arenaDto.getName())
                .city(arenaDto.getCity())
                .capacity(arenaDto.getCapacity())
                .build();
    }

    public void createArena(ArenaDto arenaDto) {
        Arena arena = buildArenaFromDto(arenaDto);
        var validationResult = createOrUpdateArenaValidator.isValid(arena);
        if(!validationResult.isValid()){
            throw new ValidationException(validationResult.getErrors());
        }
        arenaDao.save(arena);
    }

    public void updateArena(Long id, ArenaDto arenaDto) {
        Arena arena = buildArenaFromDto(arenaDto);
        arena.setId(id);
        var validationResult = createOrUpdateArenaValidator.isValid(arena);
        if(!validationResult.isValid()){
            throw new ValidationException(validationResult.getErrors());
        }
        arenaDao.update(arena);
    }

    public void deleteArena(Long id) {
        arenaDao.delete(id);
    }
}
