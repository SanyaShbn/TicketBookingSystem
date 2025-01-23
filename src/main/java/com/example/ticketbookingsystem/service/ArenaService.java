package com.example.ticketbookingsystem.service;

import com.example.ticketbookingsystem.dto.ArenaDto;
import com.example.ticketbookingsystem.dto.ArenaFilter;
import com.example.ticketbookingsystem.entity.Arena;
import com.example.ticketbookingsystem.exception.ValidationException;
import com.example.ticketbookingsystem.mapper.ArenaMapper;
import com.example.ticketbookingsystem.repository.ArenaRepository;
import com.example.ticketbookingsystem.validator.CreateOrUpdateArenaValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing arenas.
 */
@Service
@RequiredArgsConstructor
public class ArenaService {

    private final ArenaRepository arenaRepository;

    private final ArenaMapper arenaMapper;

    private final CreateOrUpdateArenaValidator createOrUpdateArenaValidator;

    /**
     * Finds all arenas.
     *
     * @return a list of all arenas
     */
    public List<ArenaDto> findAll(){
        return arenaRepository.findAll().stream()
                .map(arenaMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Finds all arenas matching the given filter.
     *
     * @param arenaFilter the filter to apply
     * @return a list of arenas matching the filter
     */
//    public List<ArenaDto> findAll(ArenaFilter arenaFilter){
//        return arenaRepository.findAll(arenaFilter).stream()
//                .map(arenaMapper::toDto)
//                .collect(Collectors.toList());
//    }

    /**
     * Finds an arena by its ID.
     *
     * @param id the ID of the arena
     * @return an {@link Optional} containing the found arena, or empty if not found
     */
    public Optional<ArenaDto> findById(Long id){
        return arenaRepository.findById(id)
                .map(arenaMapper::toDto);
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
        arenaRepository.save(arena);
    }

    /**
     * Updates an existing arena.
     *
     * @param arenaDto the DTO of the updated arena
     * @throws ValidationException if the updated arena is not valid
     */
    public void updateArena(ArenaDto arenaDto) {
        Arena arena = arenaMapper.toEntity(arenaDto);
        var validationResult = createOrUpdateArenaValidator.isValid(arena);
        if(!validationResult.isValid()){
            throw new ValidationException(validationResult.getErrors());
        }
        arenaRepository.save(arena);
    }

    /**
     * Deletes an arena by its ID.
     *
     * @param id the ID of the arena to delete
     */
    public void deleteArena(Long id) {
        arenaRepository.deleteById(id);
    }

    /**
     * Finds all unique cities of the arenas.
     *
     * @return a list of cities
     */
    public List<String> findAllArenasCities() {
        return arenaRepository.findAll().stream()
                .map(Arena::getCity)
                .distinct()
                .collect(Collectors.toList());
    }

}