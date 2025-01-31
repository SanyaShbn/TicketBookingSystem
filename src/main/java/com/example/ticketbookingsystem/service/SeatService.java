package com.example.ticketbookingsystem.service;

import com.example.ticketbookingsystem.dto.seat_dto.SeatReadDto;
import com.example.ticketbookingsystem.mapper.seat_mapper.SeatReadMapper;
import com.example.ticketbookingsystem.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing arena's seats.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SeatService {

    private final SeatRepository seatRepository;

    private final SeatReadMapper seatReadMapper;

    /**
     * Finds all seats.
     *
     * @return a list of all seats
     */
    public List<SeatReadDto> findAll(){
        return seatRepository.findAll().stream()
                .map(seatReadMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Finds seats to add seat tickets by a specific event ID.
     *
     * @param eventId the ID of the event
     * @return a list of seats which are not yet available for ticket purchasing
     */
    public List<SeatReadDto> findByEventIdWithNoTickets(Long eventId){
        return seatRepository.findByEventIdWithNoTickets(eventId).stream()
                .map(seatReadMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<SeatReadDto> findByEventId(Long eventId){
        return seatRepository.findByEventId(eventId).stream()
                .map(seatReadMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Finds seats to add seat tickets by a specific event ID including the updating one.
     *
     * @param eventId the ID of the event
     * @return a list of seats which are not yet available for ticket purchasing and the updating one
     */
    public List<SeatReadDto> findAllByEventIdWhenUpdate(Long eventId, Long seatId){
        return seatRepository.findAllByEventIdWhenUpdate(eventId, seatId).stream()
                .map(seatReadMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Finds a seat by its ID.
     *
     * @param id the ID of the seat
     * @return an {@link Optional} containing the found seat, or empty if not found
     */
    public Optional<SeatReadDto> findById(Long id){
        return seatRepository.findById(id)
                .map(seatReadMapper::toDto);
    }

}
