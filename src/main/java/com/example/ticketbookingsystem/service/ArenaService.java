package com.example.ticketbookingsystem.service;

import com.example.ticketbookingsystem.dao.ArenaDao;
import com.example.ticketbookingsystem.dto.ArenaDto;
import com.example.ticketbookingsystem.dto.ArenaFilter;
import com.example.ticketbookingsystem.entity.Arena;
import com.example.ticketbookingsystem.exception.ValidationException;
import com.example.ticketbookingsystem.mapper.ArenaMapper;
import com.example.ticketbookingsystem.validator.CreateOrUpdateArenaValidator;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing arenas.
 */
public class ArenaService {
    private static final ArenaService INSTANCE = new ArenaService();
    private final ArenaDao arenaDao = ArenaDao.getInstance();
    private final ArenaMapper arenaMapper = ArenaMapper.getInstance();
    private final CreateOrUpdateArenaValidator createOrUpdateArenaValidator = CreateOrUpdateArenaValidator.getInstance();
    private ArenaService(){}
    public static ArenaService getInstance(){
        return INSTANCE;
    }

    /**
     * Finds all arenas.
     *
     * @return a list of all arenas
     */
    public List<Arena> findAll(){
        return arenaDao.findAll();
    }

    /**
     * Finds all arenas matching the given filter.
     *
     * @param arenaFilter the filter to apply
     * @return a list of arenas matching the filter
     */
    public List<Arena> findAll(ArenaFilter arenaFilter){
        return arenaDao.findAll(arenaFilter);
    }

    /**
     * Finds an arena by its ID.
     *
     * @param id the ID of the arena
     * @return an {@link Optional} containing the found arena, or empty if not found
     */
    public Optional<Arena> findById(Long id){
        return arenaDao.findById(id);
    }

    /**
     * Creates a new arena.
     *
     * @param arenaDto the DTO of the arena to create
     * @throws ValidationException if the arena is not valid
     */
    public void createArena(ArenaDto arenaDto) {
        Arena arena = arenaMapper.toEntity(arenaDto);
        var validationResult = createOrUpdateArenaValidator.isValid(arena);
        if(!validationResult.isValid()){
            throw new ValidationException(validationResult.getErrors());
        }
        arenaDao.save(arena);
    }

    /**
     * Updates an existing arena.
     *
     * @param id the ID of the arena to update
     * @param arenaDto the DTO of the updated arena
     * @throws ValidationException if the updated arena is not valid
     */
    public void updateArena(Long id, ArenaDto arenaDto) {
        Arena arena = arenaMapper.toEntity(arenaDto);
        arena.setId(id);
        var validationResult = createOrUpdateArenaValidator.isValid(arena);
        if(!validationResult.isValid()){
            throw new ValidationException(validationResult.getErrors());
        }
        arenaDao.update(arena);
    }

    /**
     * Deletes an arena by its ID.
     *
     * @param id the ID of the arena to delete
     */
    public void deleteArena(Long id) {
        arenaDao.delete(id);
    }

    /**
     * Finds all unique cities of the arenas.
     *
     * @return a list of cities
     */
    public List<String> findAllArenasCities() {
        return arenaDao.findAllArenasCities();
    }

}
