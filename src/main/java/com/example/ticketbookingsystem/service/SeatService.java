package com.example.ticketbookingsystem.service;

import com.example.ticketbookingsystem.dto.seat_dto.SeatReadDto;
import com.example.ticketbookingsystem.mapper.seat_mapper.SeatReadMapper;
import com.example.ticketbookingsystem.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
     * Finds pageable seats to add seat tickets by a specific event ID.
     *
     * @param eventId the ID of the event
     * @param pageable The pagination information.
     * @return a page of seats which are not yet available for ticket purchasing
     */
    public Page<SeatReadDto> findByEventIdWithNoTickets(Long eventId, Pageable pageable){
        return seatRepository.findByEventIdWithNoTickets(eventId, pageable)
                .map(seatReadMapper::toDto);
    }

    /**
     * Finds the list of the whole seats available for a specific event
     * (not pageable, just the list with all the records).
     *
     * @param eventId the ID of the event
     * @return list of all seats for this sporting event
     */
    public List<SeatReadDto> findByEventId(Long eventId){
        return seatRepository.findByEventId(eventId).stream()
                .map(seatReadMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Finds pageable seats to add seat tickets by a specific event ID including the updating one.
     *
     * @param eventId the ID of the event
     * @param seatId the ID of the seat for which there is already a ticket (the updating one)
     * @param pageable The pagination information.
     * @return a page of seats which are not yet available for ticket purchasing and the updating one
     */
    public Page<SeatReadDto> findAllByEventIdWhenUpdate(Long eventId, Long seatId, Pageable pageable){
        return seatRepository.findAllByEventIdWhenUpdate(eventId, seatId, pageable)
                .map(seatReadMapper::toDto);
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
