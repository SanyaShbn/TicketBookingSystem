package com.example.ticketbookingsystem.service;

import com.example.ticketbookingsystem.dto.*;
import com.example.ticketbookingsystem.dto.sport_event_dto.SportEventCreateEditDto;
import com.example.ticketbookingsystem.dto.sport_event_dto.SportEventFilter;
import com.example.ticketbookingsystem.dto.sport_event_dto.SportEventReadDto;
import com.example.ticketbookingsystem.entity.Arena;
import com.example.ticketbookingsystem.entity.SportEvent;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.exception.DaoResourceNotFoundException;
import com.example.ticketbookingsystem.mapper.sport_event_mapper.SportEventCreateEditMapper;
import com.example.ticketbookingsystem.mapper.sport_event_mapper.SportEventReadMapper;
import com.example.ticketbookingsystem.repository.SportEventRepository;
import com.example.ticketbookingsystem.utils.SortUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
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

    private final RestTemplate restTemplate;

    private final String IMAGE_SERVICE_URL = "http://localhost:8081/api/v1/images";

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
    @Transactional
    public SportEventReadDto createSportEvent(SportEventCreateEditDto sportEventCreateEditDto,
                                              Long arenaId){
//                                              String posterImageUrl) {
        try {
            SportEvent sportEvent = sportEventCreateEditMapper.toEntity(sportEventCreateEditDto);
            Arena arena = arenaService.findArenaById(arenaId);
            sportEvent.setArena(arena);
//            if (posterImageUrl != null && !posterImageUrl.isEmpty()) {
//                sportEvent.setPosterImageUrl(posterImageUrl);
//            }
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
                                              Long arenaId
//                                              String posterImageUrl
    ) {
        try {
            SportEvent sportEvent = sportEventCreateEditMapper.toEntity(sportEventCreateEditDto);
            Arena arena = arenaService.findArenaById(arenaId);
            sportEvent.setId(id);
            sportEvent.setArena(arena);
//            if (posterImageUrl != null && !posterImageUrl.isEmpty()) {
//                String oldImageUrl = sportEventRepository.findById(id).get().getPosterImageUrl();
//                if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
//                    String oldImageId = extractImageIdFromUrl(oldImageUrl);
//                    deleteImage(oldImageId);
//                }
//                sportEvent.setPosterImageUrl(posterImageUrl);
//            }
            SportEvent updatedSportEvent = sportEventRepository.save(sportEvent);
            log.info("SportEvent with id {} updated successfully with dto: {}", id, sportEventCreateEditDto);
            return sportEventReadMapper.toDto(updatedSportEvent);
        } catch (DataAccessException e){
            log.error("Failed to update SportEvent {} with dto: {}", id, sportEventCreateEditDto);
            throw new DaoCrudException(e);
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

            String imageUrl = sportEventOptional.get().getPosterImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                String imageId = extractImageIdFromUrl(imageUrl);
                deleteImage(imageId);
            }

            log.info("SportEvent with id {} deleted successfully.", id);
        } catch (DataAccessException e){
            log.error("Failed to delete SportEvent with id {}", id);
            throw new DaoCrudException(e);
        }
    }

    private String extractImageIdFromUrl(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            String[] parts = imageUrl.split("/");
            return parts[parts.length - 1];  // Извлечение ID из URL
        }
        return null;
    }

    public void uploadImage(File file) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(file));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            String response = restTemplate.postForObject(IMAGE_SERVICE_URL + "/upload", requestEntity, String.class);
            log.info("Image upload response: {}", response);
        } catch (Exception e) {
            log.error("Error uploading image", e);
        }
    }

    public void deleteImage(String id) {
        restTemplate.delete(IMAGE_SERVICE_URL + "/" + id);
    }

}
